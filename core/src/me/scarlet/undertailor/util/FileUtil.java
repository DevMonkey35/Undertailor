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
    public static Map<String, File> loadWithIdentifiers(File root, FileFilter filter) {
        return loadWithIdentifiers(new HashMap<>(), null, root, filter);
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
    private static Map<String, File> loadWithIdentifiers(Map<String, File> parentMap, String append,
        File root, FileFilter filter) {
        for (File file : root.listFiles((FileFilter) file -> {
            return file.isDirectory() || (filter != null ? filter.accept(file) : true);
        })) {
            String identifier =
                (append == null ? "" : append + ".") + file.getName().split("\\.")[0];
            if (file.isDirectory()) {
                loadWithIdentifiers(parentMap, identifier, file, filter);
            } else {
                parentMap.put(identifier, file);
            }
        }

        return parentMap;
    }
}
