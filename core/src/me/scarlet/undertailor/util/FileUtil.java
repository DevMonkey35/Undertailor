/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Tellerva, Marc Lawrence
 *
 * Permission is hereby granted, free of charge, to any
 * person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the
 * Software without restriction, including without
 * limitation the rights to use, copy, modify, merge,
 * publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice
 * shall be included in all copies or substantial portions
 * of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */

package me.scarlet.undertailor.util;

import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for interaction with files.
 */
public class FileUtil {

    /**
     * Returns a mapping of identifier strings and files
     * loaded from the given root folder, scanned
     * recursively.
     * 
     * @param root the root folder to search in
     * @param filter the file filter to query about which
     *        files to include, or null to allow all
     * 
     * @return a mapping of identifier and File pairs
     * 
     * @see #loadWithIdentifiers(File, FileFilter, boolean)
     */
    public static Map<String, File> loadWithIdentifiers(File root, FileFilter filter) {
        return loadWithIdentifiers(root, filter, true);
    }

    /**
     * Returns a mapping of identifier strings and files
     * loaded from the given root folder.
     * 
     * <p>With the given directory tree,</p>
     * 
     * <pre>
     * root
     *  | animations
     *    | something.animation
     *    | something2.animation
     *  | sounds
     *    | shot.wav
     *    | shot3.ogg
     * </pre>
     * 
     * <p>the result would be:</p>
     * <table>
     * <tr>
     * <td>animations.something</td>
     * <td>File</td>
     * </tr>
     * <tr>
     * <td>animations.something2</td>
     * <td>File</td>
     * </tr>
     * <tr>
     * <td>sounds.shot</td>
     * <td>File</td>
     * </tr>
     * <tr>
     * <td>sounds.shot3</td>
     * <td>File</td>
     * </tr>
     * </table>
     * 
     * <p></p>
     * 
     * @param root the root folder
     * @param filter the filter to query about which files
     *        to include, or null to allow all
     * 
     * @return a mapping of identifier and File pairs
     */
    public static Map<String, File> loadWithIdentifiers(File root, FileFilter filter, boolean recursive) {
        return loadWithIdentifiers(new HashMap<>(), recursive, null, root, filter);
    }

    /**
     * Internal method.
     * 
     * <p>Simply hides the mess of parameters in favor of
     * the two simple parameters required by the public
     * method.</p>
     * 
     * @see #loadWithIdentifiers(File, FileFilter)
     */
    private static Map<String, File> loadWithIdentifiers(Map<String, File> parentMap,
        boolean recursive, String append, File root, FileFilter filter) {
        if (!root.exists()) {
            root.mkdirs();
        }

        if (!root.isDirectory()) {
            throw new IllegalArgumentException("Provided File reference was not a directory");
        }

        for (File file : root.listFiles((FileFilter) file -> {
            return file.isDirectory() || (filter != null ? filter.accept(file) : true);
        })) {
            String identifier =
                (append == null ? "" : append + ".") + file.getName().split("\\.")[0].replaceAll(" ", "_");
            if (file.isDirectory() && recursive) {
                loadWithIdentifiers(parentMap, true, identifier, file, filter);
            } else {
                parentMap.put(identifier, file);
            }
        }

        return parentMap;
    }
}
