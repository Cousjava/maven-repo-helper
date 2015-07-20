/*
 * Copyright 2015 Emmanuel bourg
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

package org.debian.maven.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.commons.io.input.BOMInputStream;

import static org.apache.commons.io.ByteOrderMark.*;

public class Readers {

    /**
     * Detects a BOM in the specified input stream and returns a Reader
     * using the charset detected, or ISO-8859-1 otherwise.
     */
    public static Reader read(InputStream in) throws IOException {
        BOMInputStream bis = new BOMInputStream(in, false, UTF_8, UTF_16BE, UTF_16LE, UTF_32BE, UTF_32LE);
        if (bis.hasBOM()) {
            return new InputStreamReader(bis, bis.getBOM().getCharsetName());
        } else {
            return new InputStreamReader(bis, "ISO-8859-1");
        }
    }

    public static Reader read(File file) throws IOException {
        return read(new FileInputStream(file));
    }
}
