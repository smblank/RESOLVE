/**
 * Utilities.java
 * ---------------------------------
 * Copyright (c) 2016
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.init.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;
import edu.clemson.cs.rsrg.errorhandling.ErrorHandler;
import org.antlr.v4.runtime.ANTLRInputStream;

/**
 * <p>This class contains static helper methods that for processing
 * files and to convert <code>File</code> objects to <code>ResolveFile</code>
 * objects.</p>
 *
 * @author Yu-Shan Sun
 * @author Daniel Welch
 * @version 1.0
 */
public class Utilities {

    /**
     * <p>Converts a regular <code>File</code> object to the
     * <code>ResolveFile</code> accepted by the compiler.</p>
     *
     * @param file The Java File object.
     * @param moduleType The extension of the file.
     * @param workspacePath The current RESOLVE workspace path.
     *
     * @return A <code>ResolveFile</code> object representing the file.
     *
     * @throws IOException
     */
    public static ResolveFile convertToResolveFile(File file,
            ModuleType moduleType, String workspacePath) throws IOException {
        // Convert to the internal representation of a RESOLVE file
        String name = Utilities.getFileName(file.getName(), moduleType);
        List<String> pkgList =
                Utilities.getPackageList(file.getAbsolutePath(), workspacePath);
        ANTLRInputStream inputStream =
                new ANTLRInputStream(new FileInputStream(file));

        return new ResolveFile(name, moduleType, inputStream, pkgList, file
                .getAbsolutePath());
    }

    /**
     * <p>Converts the specified pathname to a <code>File</code>
     * representing the absolute path to the pathname.</p>
     *
     * @param pathname The file path.
     *
     * @return The <code>File</code> specified by the path.
     */
    public static File getAbsoluteFile(String pathname) {
        return new File(pathname).getAbsoluteFile();
    }

    /**
     * <p>Returns the file name without the extension.</p>
     *
     * @param fileName Name of the file with the extension.
     * @param moduleType The extension of the file.
     *
     * @return Name of the file without the extension.
     */
    public static String getFileName(String fileName, ModuleType moduleType) {
        return fileName.substring(0, fileName.lastIndexOf(moduleType
                .getExtension()) - 1);
    }

    /**
     * <p>The folder RESOLVE and it's sub folders are viewed by the compiler
     * as "packages", therefore we will need to store these for future use.</p>
     *
     * @param filePath The absolute path to the file.
     * @param workspacePath The current workspace's absolute path.
     *
     * @return A list containing the RESOLVE "packages"
     */
    public static List<String> getPackageList(String filePath,
            String workspacePath) {
        // Obtain the relative path using the workspace path and current file path.
        String relativePath = filePath.substring(workspacePath.length() + 1);

        // Add all package names using the Java Collections
        List<String> pkgList = new LinkedList<String>();
        Collections.addAll(pkgList, relativePath.split(Pattern
                .quote(File.separator)));

        return pkgList;
    }

    /**
     * <p>Get the absolute path to the RESOLVE Workspace. This workspace
     * must have the same structure as the one hosted on Github.</p>
     *
     * @param path The absolute path we are going to search.
     *
     * @return File path to the current RESOLVE workspace.
     */
    public static File getWorkspaceDir(String path) {
        File resolvePath = null;
        String resolveDirName = "RESOLVE";

        // Look in the specified path
        if (path != null) {
            resolvePath = new File(path);

            // Not a valid path
            if (!resolvePath.exists()) {
                System.err.println("Warning: Directory '" + resolveDirName
                        + "' not found, using current " + "directory.");
                resolvePath = null;
            }
        }

        // Attempt to locate the folder containing the folder "RESOLVE"
        if (resolvePath == null) {
            File currentDir = getAbsoluteFile("");

            // Check to see if our current path is the path that contains
            // the RESOLVE folder.
            if (currentDir.getName().equals(resolveDirName)) {
                resolvePath = currentDir;
            }

            // Attempt to locate the "RESOLVE" folder
            while ((resolvePath == null)
                    && (currentDir.getParentFile() != null)) {
                currentDir = currentDir.getParentFile();
                if (currentDir.getName().equals(resolveDirName)) {
                    // We store the path that contains the "RESOLVE" folder
                    resolvePath = currentDir.getParentFile();
                }
            }

            // Probably will crash because we can't find "RESOLVE"
            if (resolvePath == null) {
                System.err.println("Warning: Directory '" + resolveDirName
                        + "' not found, using current " + "directory.");

                resolvePath = getAbsoluteFile("");
            }
        }

        return resolvePath;
    }

    /**
     * <p>Determines if the specified filename is a valid RESOLVE file type.</p>
     *
     * @return A RESOLVE extension type object if it is an extension we
     * recognize or null if it is not.
     */
    public static ModuleType getModuleType(String filename) {
        ModuleType type = null;

        if (filename.endsWith(ModuleType.THEORY.getExtension())) {
            type = ModuleType.THEORY;
        }
        else if (filename.endsWith(ModuleType.CONCEPT.getExtension())) {
            type = ModuleType.CONCEPT;
        }
        else if (filename.endsWith(ModuleType.ENHANCEMENT.getExtension())) {
            type = ModuleType.ENHANCEMENT;
        }
        else if (filename.endsWith(ModuleType.REALIZATION.getExtension())) {
            type = ModuleType.REALIZATION;
        }
        else if (filename.endsWith(ModuleType.FACILITY.getExtension())) {
            type = ModuleType.FACILITY;
        }
        else if (filename.endsWith(ModuleType.PROFILE.getExtension())) {
            type = ModuleType.PROFILE;
        }

        return type;
    }

    /**
     * <p>A helper method to form a string using the values inside the
     * Java collection.</p>
     *
     * @param data The collection of values.
     * @param separator The separator to be used.
     * @param <T> The type of the values inside the collection.
     *
     * @return The formatted string.
     */
    public static <T> String join(Collection<T> data, String separator) {
        return join(data, separator, "", "");
    }

    /**
     * <p>A helper method to form a string using the values inside the
     * Java collection.</p>
     *
     * @param data The collection of values.
     * @param separator The separator to be used.
     * @param left The left most value for the string.
     * @param right The right most value for the string.
     * @param <T> The type of the values inside the collection.
     *
     * @return The formatted string.
     */
    public static <T> String join(Collection<T> data, String separator,
            String left, String right) {
        return join(data.iterator(), separator, left, right);
    }

    /**
     * <p>A helper method to form a string using an iterator.</p>
     *
     * @param iter An iterator for the collection of values.
     * @param separator The separator to be used.
     * @param left The left most value for the string.
     * @param right The right most value for the string.
     * @param <T> The type of the values inside the collection.
     *
     * @return The formatted string.
     */
    public static <T> String join(Iterator<T> iter, String separator,
            String left, String right) {
        StringBuilder buf = new StringBuilder();

        buf.append(left);
        while (iter.hasNext()) {
            buf.append(iter.next());
            if (iter.hasNext()) {
                buf.append(separator);
            }
        }
        buf.append(right);

        return buf.toString();
    }

    /**
     * <p>A helper method to form a string using the values inside the
     * array.</p>
     *
     * @param array The array of values.
     * @param separator The separator to be used.
     * @param <T> The type of the values inside the collection.
     *
     * @return The formatted string.
     */
    public static <T> String join(T[] array, String separator) {
        return join(Arrays.asList(array), separator);
    }

}