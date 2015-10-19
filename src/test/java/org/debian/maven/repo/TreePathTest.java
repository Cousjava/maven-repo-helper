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

package org.debian.maven.repo;

import org.junit.Test;

import static org.junit.Assert.*;

public class TreePathTest {

    @Test
    public void testMatches() {
        TreePath<String> path = new TreePath<String>();
        path.add("a");
        path.add("b");
        path.add("c");
        path.add("d");

        assertTrue(path.matches("a/b/c/d"));
        assertTrue(path.matches("b/c/d"));
        assertTrue(path.matches("c/d"));
        assertTrue(path.matches("d"));

        assertTrue(path.matches("*/b/c/d"));
        assertTrue(path.matches("a/*/c/d"));
        assertTrue(path.matches("a/b/*/d"));
        assertTrue(path.matches("a/b/c/*"));

        assertTrue(path.matches("/*/b/c/d"));
        assertTrue(path.matches("/a/*/c/d"));
        assertTrue(path.matches("/a/b/*/d"));
        assertTrue(path.matches("/a/b/c/*"));

        assertFalse(path.matches("/b/c/d"));
        assertFalse(path.matches("/c/d"));
        assertFalse(path.matches("/d"));
    }
}
