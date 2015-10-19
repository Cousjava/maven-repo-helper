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

import java.util.LinkedList;

/**
 * Path in a tree like structure.
 * 
 * @param <S> the type of the elements in the path
 */
class TreePath<S> {

    private LinkedList<S> path = new LinkedList<S>();

    /**
     * Appends an element to the path.
     */
    public void add(S el) {
        path.addLast(el);
    }

    /**
     * Removes the last element of the path.
     */
    public void remove() {
        path.removeLast();
    }

    /**
     * Tells if the path contains the specified element.
     */
    public boolean contains(S el) {
        return path.contains(el);
    }

    /**
     * Returns the length of the path.
     */
    public int size() {
        return path.size();
    }

    /**
     * Returns the element of the path at the specified index.
     */
    public S get(int index) {
        return path.get(index);
    }

    /**
     * Returns the n-th parent of the last element in the path.
     * 
     * @param generations 1: parent, 2: grand parent, etc
     */
    public S parent(int generations) {
        int index = (path.size() - 1) - generations;
        return index >= 0 ? path.get(index) : null;
    }

    /**
     * Does this path match the pattern?
     *
     * The pattern is separated by slashes / and can contain the * wildcard for any path element to match
     * anything. The matching is anchored at the end of the path. So the pattern does not need to start at
     * the root.
     *
     * A pattern that starts with a slash is also anchored at the start.
     *
     * @param patternString
     */
    public boolean matches(String patternString) {
        if (patternString.startsWith("/")) {
            return matches(patternString.substring(1), true);
        } else {
            return matches(patternString, false);
        }
    }

    private boolean matches(String patternString, boolean anchored) {
        String[] patterns = patternString.split("/");

        if (anchored && patterns.length != path.size()) {
            return false;
        }

        int pathIndex = path.size() - patterns.length - 1;
        if (pathIndex < -1) {
            return false;
        }

        for (String pattern : patterns) {
            ++pathIndex;
            if ("*".equals(pattern)) {
                continue;
            }
            if (!pattern.equals(path.get(pathIndex))) {
                return false;
            }
        }
        return true;
    }
    
    public POMInfo.DependencyType match() {
        for (POMInfo.DependencyType depType : POMInfo.DependencyType.values()) {
            if (matches(depType.pattern)) {
                return depType;
            }
        }
        return null;
    }
}
