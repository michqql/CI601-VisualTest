package me.mp1282.visualtest.system.inbuilt.category;

import me.mp1282.visualtest.system.inbuilt.InbuiltFunctionProvider;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

@InbuiltFunctionProvider(providerName = "Files")
public class FileInbuiltMethods {

    // --- Write ---

    /** Writes (or overwrites) a file with the given text content. Returns true on success. */
    public static boolean writeFile(String path, String content) {
        try {
            Files.writeString(Paths.get(path), content);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /** Appends text to an existing file (or creates it). Returns true on success. */
    public static boolean appendToFile(String path, String content) {
        try {
            Files.writeString(Paths.get(path), content, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /** Appends text followed by a newline. Returns true on success. */
    public static boolean appendLine(String path, String line) {
        return appendToFile(path, line + System.lineSeparator());
    }

    // --- Read ---

    /** Reads the entire content of a file as a String. Returns an empty string on failure. */
    public static String readFile(String path) {
        try {
            return Files.readString(Paths.get(path));
        } catch (IOException e) {
            return "";
        }
    }

    /** Reads all lines of a file into an ArrayList. Returns an empty list on failure. */
    public static ArrayList<Object> readLines(String path) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(path));
            return new ArrayList<>(lines);
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    /** Reads a single line (0-based index) from a file. Returns an empty string if the line doesn't exist. */
    public static String readLine(String path, int lineIndex) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(path));
            return (lineIndex >= 0 && lineIndex < lines.size()) ? lines.get(lineIndex) : "";
        } catch (IOException e) {
            return "";
        }
    }

    // --- Existence / Info ---

    public static boolean fileExists(String path) {
        return Files.exists(Paths.get(path));
    }

    public static boolean isDirectory(String path) {
        return Files.isDirectory(Paths.get(path));
    }

    public static long fileSize(String path) {
        try {
            return Files.size(Paths.get(path));
        } catch (IOException e) {
            return -1L;
        }
    }

    // --- Manipulation ---

    /** Deletes a file. Returns true on success. */
    public static boolean deleteFile(String path) {
        try {
            return Files.deleteIfExists(Paths.get(path));
        } catch (IOException e) {
            return false;
        }
    }

    /** Creates all directories along the given path. Returns true on success. */
    public static boolean createDirectories(String path) {
        try {
            Files.createDirectories(Paths.get(path));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /** Lists filenames (not full paths) inside a directory. Returns an empty list on failure. */
    public static ArrayList<Object> listFiles(String directoryPath) {
        try {
            ArrayList<Object> names = new ArrayList<>();
            Files.list(Paths.get(directoryPath)).forEach(p -> names.add(p.getFileName().toString()));
            return names;
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    // --- Path Utilities ---

    public static String getFileName(String path) {
        Path p = Paths.get(path).getFileName();
        return p != null ? p.toString() : "";
    }

    public static String getParentPath(String path) {
        Path p = Paths.get(path).getParent();
        return p != null ? p.toString() : "";
    }

    public static String joinPath(String base, String child) {
        return Paths.get(base, child).toString();
    }

    public static String absolutePath(String path) {
        return Paths.get(path).toAbsolutePath().toString();
    }

    private FileInbuiltMethods() throws IllegalAccessException {
        throw new IllegalAccessException("FileInbuiltMethods should not be instantiated");
    }
}
