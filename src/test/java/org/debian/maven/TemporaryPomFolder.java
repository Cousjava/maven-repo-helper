package org.debian.maven;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.debian.maven.util.Readers;
import org.junit.rules.TemporaryFolder;

public class TemporaryPomFolder extends TemporaryFolder {

    private List<Closeable> openedReaders = new ArrayList<Closeable>();
    private File updatedPom;

    public String pomInUse;

    public File copyResource(String resource, File file) throws IOException {
        InputStream in = this.getClass().getResourceAsStream("/" + resource);
        FileOutputStream out = new FileOutputStream(file);
        IOUtils.copy(in, out);
        in.close();
        out.close();
        return file;
    }

    public File usePom(String resource) throws IOException {
        pomInUse = resource;
        File pom = newFile("original.pom");
        return copyResource(resource, pom);
    }

    public Reader read(String resource) throws IOException {
        Reader r = Readers.read(this.getClass().getResourceAsStream("/" + resource));
        openedReaders.add(r);
        return r;
    }

    public File updatedPom() throws IOException {
        if (updatedPom == null) {
            updatedPom = newFile("updated.pom");
        }
        return updatedPom;
    }

    public Reader read(File file) throws IOException {
        Reader r = Readers.read(file);
        openedReaders.add(r);
        return r;
    }

    @Override
    protected void after() {
        for (Closeable reader : openedReaders) {
            try {
                reader.close();
            } catch (IOException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            }
        }
        openedReaders.clear();
        updatedPom = null;
        super.after();
    }

    public static File getFileInClasspath(String resource) {
        if (!resource.startsWith("/")) {
            resource = "/" + resource;
        }
        URL url = TemporaryPomFolder.class.getResource(resource);
        File f;
        try {
            f = new File(url.toURI());
        } catch (URISyntaxException e) {
            f = new File(url.getPath());
        }
        return f;
    }

    public static String basename(String fileName) {
        String[] tokens = fileName.split("\\.(?=[^\\.]+$)");
        return tokens[0];
    }
}
