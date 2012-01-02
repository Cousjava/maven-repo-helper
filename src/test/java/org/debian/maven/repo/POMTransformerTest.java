package org.debian.maven.repo;

/*
 * Copyright 2009 Ludovic Claude.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.custommonkey.xmlunit.XMLUnit;

public class POMTransformerTest extends TestBase {

    private POMTransformer instance;

    public void setUp() {
        super.setUp();
        instance = new POMTransformer();
        instance.getRules().setRulesFile(null);
        instance.getIgnoreRules().setRulesFile(null);
        instance.getPublishedRules().setRulesFile(null);
    }

    public void testTransformMavenPom() throws Exception {
        usePom("maven.xml");
        boolean noParent = true;
        instance.addDefaultRules();
        instance.addRule(new DependencyRule("org.codehaus.plexus plexus-container-default jar s/1\\.0-alpha-.*/1.0-alpha/"));
        instance.addRule(new DependencyRule("org.apache.maven.plugins maven-assembly-plugin maven-plugin s/.*/2.2/"));
        instance.transformPom(pom, updatedPom, noParent, true, false, false, null, null);
        assertXMLEqual(read("maven.transformed"), read(updatedPom));
    }

    public void testTransformMavenCorePom() throws Exception {
        usePom("maven-core.xml");
        boolean noParent = false;
        instance.addDefaultRules();
        instance.addRule(new DependencyRule("org.codehaus.plexus plexus-container-default jar s/1\\.0-alpha-.*/1.0-alpha/"));
        instance.transformPom(pom, updatedPom, noParent, true, false, false, null, "maven2");
        assertXMLEqual(read("maven-core.transformed"), read(updatedPom));
    }

    public void testTransformMavenJavadocPluginPom() throws Exception {
        XMLUnit.setIgnoreComments(true);

        usePom("maven-javadoc-plugin.xml");
        boolean noParent = true;
        instance.addDefaultRules();
        instance.addRule(new DependencyRule("org.codehaus.modello modello-maven-plugin maven-plugin s/.*/1.0.1/"));
        instance.addRule(new DependencyRule("org.codehaus.mojo clirr-maven-plugin * *"));
        instance.addRule(new DependencyRule("org.apache.bcel bcel jar s/5\\..*/5.x/"));
        instance.addRule(new DependencyRule("* maven-plugin-plugin maven-plugin s/.*/2.5/"));
        POMInfo transformedPOM = instance.transformPom(pom, updatedPom, noParent, true, false, false, null, null);
        assertXMLEqual(read("maven-javadoc-plugin.transformed"), read(updatedPom));
        assertNull(transformedPOM.getParent());
    }

    public void testTransformModelloPom() throws Exception {
        usePom("modello-core.xml");
        boolean noParent = false;
        instance.addDefaultRules();
        instance.transformPom(pom, updatedPom, noParent, true, false, false, null, "libmodello-java");
        assertXMLEqual(read("modello-core.transformed"), read(updatedPom));
    }

    public void testTransformPlexusContainerDefaultPom() throws Exception {
        usePom("plexus-container-default.xml");
        instance.addDefaultRules();
        instance.addRule(new DependencyRule("org.codehaus.plexus plexus-container-default jar s/1\\.0-alpha-.*/1.0-alpha/"));
        instance.transformPom(pom, updatedPom);
        assertXMLEqual(read("plexus-container-default.transformed"), read(updatedPom));
    }

    public void testTransformDoxiaFmlPom() throws Exception {
        usePom("doxia-module-fml.xml");
        boolean noParent = false;
        instance.setRepository(getRepository());
        instance.addDefaultRules();
        instance.transformPom(pom, updatedPom, noParent, true, true, false, null, "libdoxia-java");
        assertXMLEqual(read("doxia-module-fml.transformed"), read(updatedPom));
    }

    public void testTransformAntlr3Pom() throws Exception {
        usePom("antlr3.xml");
        boolean noParent = false;
        instance.setRepository(getRepository());
        instance.addDefaultRules();
        instance.addRule(new DependencyRule("org.antlr stringtemplate * s/3\\..*/3.x/ *"));
        instance.addRule(new DependencyRule("antlr antlr jar s/2\\..*/2.x/ *"));
        instance.addRule(new DependencyRule("junit junit jar s/4\\..*/4.x/ *"));
        instance.addRule(new DependencyRule("org.antlr antlr3-maven-plugin maven-plugin s/.*/3.2/"));
        instance.addRule(new DependencyRule("org.antlr stringtemplate jar s/3\\..*/3.x/ *"));
        instance.addIgnoreRule(new DependencyRule("org.codehaus.mojo findbugs-maven-plugin maven-plugin *"));
        POMInfo transformedPOM = instance.transformPom(pom, updatedPom, noParent, true, true, true, null, "libantlr3-java");
        assertXMLEqual(read("antlr3.transformed"), read(updatedPom));
        assertEquals("3.2", transformedPOM.getParent().getVersion());
        assertEquals(1, transformedPOM.getDependencies().size());
        assertTrue(transformedPOM.getDependencies().contains(new Dependency("org.antlr", "stringtemplate", "jar", "3.x")));
        assertEquals(1, transformedPOM.getExtensions().size());
        assertTrue(transformedPOM.getExtensions().contains(new Dependency("org.apache.maven.wagon", "wagon-ssh-external", "jar", "debian")));
        assertEquals(1, transformedPOM.getPlugins().size());
        assertTrue(transformedPOM.getPlugins().contains(new Dependency("org.apache.maven.plugins", "maven-compiler-plugin", "maven-plugin", "2.0.2")));
    }

    public void testTransformAntlr3ParentPom() throws Exception {
        usePom("antlr3-parent.xml");
        boolean noParent = false;
        instance.addDefaultRules();
        instance.addRule(new DependencyRule("org.antlr stringtemplate * s/3\\..*/3.x/ *"));
        instance.addRule(new DependencyRule("antlr antlr jar s/2\\..*/2.x/ *"));
        instance.addRule(new DependencyRule("junit junit jar s/4\\..*/4.x/ *"));
        instance.addRule(new DependencyRule("org.antlr stringtemplate jar s/3\\..*/3.x/ *"));
        instance.addRule(new DependencyRule("org.antlr antlr3-maven-plugin maven-plugin s/.*/3.2/"));
        instance.addIgnoreRule(new DependencyRule("org.codehaus.mojo findbugs-maven-plugin maven-plugin *"));
        instance.addIgnoreRule(new DependencyRule("org.codehaus.mojo buildnumber-maven-plugin maven-plugin *"));
        instance.addIgnoreRule(new DependencyRule("* maven-assembly-plugin maven-plugin *"));
        instance.addIgnoreRule(new DependencyRule("org.apache.maven.wagon * * *"));
        instance.addIgnoreModule(pom, "gunit");
        instance.addIgnoreModule(pom, "gunit-maven-plugin");

        instance.transformPom(pom, updatedPom, noParent, true, true, false, null, "libantlr3-java");
        assertXMLEqual(read("antlr3-parent.transformed"), read(updatedPom));
    }

    public void testTransformAntlr3ToolsPom() throws Exception {
        XMLUnit.setIgnoreComments(true);

        usePom("antlr3-tools.xml");
        boolean noParent = false;
        instance.setRepository(getRepository());

        instance.addDefaultRules();
        instance.addRule(new DependencyRule("org.antlr stringtemplate * s/3\\..*/3.x/ *"));
        instance.addRule(new DependencyRule("antlr antlr jar s/2\\..*/2.x/ *"));
        instance.addRule(new DependencyRule("junit junit jar s/4\\..*/4.x/ *"));
        instance.addRule(new DependencyRule("org.antlr stringtemplate jar s/3\\..*/3.x/ *"));
        instance.addRule(new DependencyRule("org.antlr antlr3-maven-plugin maven-plugin s/.*/3.2/"));
        instance.usePluginVersionsFromRepository();
        //instance.addRule(new DependencyRule("org.codehaus.mojo antlr-maven-plugin maven-plugin s/.*/2.1/"));
        instance.addIgnoreRule(new DependencyRule("org.codehaus.mojo findbugs-maven-plugin maven-plugin *"));
        instance.addIgnoreRule(new DependencyRule("org.codehaus.mojo buildnumber-maven-plugin maven-plugin *"));
        instance.addIgnoreRule(new DependencyRule("* maven-assembly-plugin maven-plugin *"));
        instance.addIgnoreRule(new DependencyRule("org.apache.maven.wagon * * *"));
        instance.addIgnoreModule(pom, "gunit");
        instance.addIgnoreModule(pom, "gunit-maven-plugin");

        instance.transformPom(pom, updatedPom, noParent, true, true, true, null, "libantlr3-java");
        assertXMLEqual(read("antlr3-tools.transformed"), read(updatedPom));
    }

    public void testTransformHivernateValidatorTckRunnerPom() throws Exception {
        usePom("hibernate-validator-tck-runner.pom");
        boolean noParent = false;
        instance.setRepository(getRepository());

        instance.addDefaultRules();
        instance.usePluginVersionsFromRepository();

        instance.transformPom(pom, updatedPom, noParent, true, true, true, null, "libhibernate-validator-java");
        assertXMLEqual(read("hibernate-validator-tck-runner.transformed"), read(updatedPom));
    }

    public void testTransformHivernateValidatorPom() throws Exception {
        usePom("hibernate-validator.pom");
        boolean noParent = false;
        instance.setRepository(getRepository());

        instance.addDefaultRules();
        instance.usePluginVersionsFromRepository();

        instance.transformPom(pom, updatedPom, noParent, true, true, true, null, "libhibernate-validator-java");
        assertXMLEqual(read("hibernate-validator.transformed"), read(updatedPom));
    }

    public void testTransformPlexusUtils2Pom() throws Exception {
        usePom("plexus-utils2.pom");
        boolean noParent = false;

        instance.addDefaultRules();
        instance.addRule(new DependencyRule("org.codehaus.plexus plexus-utils jar s/2\\../2.x/ * *"));
        instance.addRule(new DependencyRule("org.codehaus.plexus plexus pom s/2\\..*/2.x/ * *"));
        instance.addIgnoreRule(new DependencyRule("org.apache.maven.plugins maven-release-plugin * *"));

        POMInfo transformedPOM = instance.transformPom(pom, updatedPom, noParent, true, true, false, null, "libplexus-utils2-java");
        assertXMLEqual(read("plexus-utils2.transformed"), read(updatedPom));
        assertEquals("2.x", transformedPOM.getParent().getVersion());
    }

    public void testTransformAntlrMavenPluginPom() throws Exception {
        usePom("antlr-maven-plugin.pom");
        boolean noParent = true;
        instance.setRepository(getRepository());

        instance.addDefaultRules();
        instance.usePluginVersionsFromRepository();
        instance.addRule(new DependencyRule("s/org.apache.maven.shared/org.apache.maven.plugin-testing/ maven-plugin-testing * s/.*/debian/ *"));
        instance.addRule(new DependencyRule("s/org.apache.maven.shared/org.apache.maven.plugin-testing/ maven-plugin-testing-harness * s/.*/debian/ *"));
        instance.addRule(new DependencyRule("s/org.apache.maven.shared/org.apache.maven.plugin-testing/ maven-plugin-testing-tools * s/.*/debian/ *"));
        instance.addRule(new DependencyRule("s/org.apache.maven.shared/org.apache.maven.plugin-testing/ maven-test-tools * s/.*/debian/ *"));

        POMInfo transformedPom = instance.transformPom(pom, updatedPom, noParent, true, true, false, null, "libantlr-maven-plugin-java");
        assertXMLEqual(read("antlr-maven-plugin.transformed"), read(updatedPom));
        assertEquals("2.3", ((Dependency) transformedPom.getPluginManagement().get(2)).getVersion());
    }

    public void testTransformMavenPackagerUtilsPom() throws Exception {
        usePom("maven-packager-utils.pom");
        boolean noParent = false;

        instance.addDefaultRules();
        instance.addRule(new DependencyRule("junit junit jar s/3\\..*/3.x/"));
        instance.addRule(new DependencyRule("org.codehaus.plexus plexus-container-default jar s/1\\.0-alpha-.*/1.0-alpha/"));

        POMInfo transformedPOM = instance.transformPom(pom, updatedPom, noParent, true, true, true, null, "maven-repo-helper");
        assertXMLEqual(read("maven-packager-utils.transformed"), read(updatedPom));
        assertEquals("1.2", transformedPOM.getParent().getVersion());
    }

    public void testTransformPlexusComponentsPomUsingListOfPoms() throws Exception {
        usePom("libplexus-components-java.poms");

        instance.setVerbose(true);
        instance.setRepository(getRepository());
        instance.addDefaultRules();
        instance.usePluginVersionsFromRepository();

        ListOfPOMs listOfPoms = new ListOfPOMs(getFileInClasspath("libplexus-components-java.poms"));
        listOfPoms.setBaseDir(getFileInClasspath("plexus-components/pom.xml").getParentFile());
        instance.setListOfPOMs(listOfPoms);
        instance.transformPoms("libplexus-components-java", false, null);

        assertXMLEqual(read("plexus-components.transformed"), read("plexus-components/pom.xml"));

    }

    private Repository getRepository() {
        Repository repo = new Repository(getFileInClasspath("repository/root.dir").getParentFile());
        repo.scan();
        return repo;
    }

}
