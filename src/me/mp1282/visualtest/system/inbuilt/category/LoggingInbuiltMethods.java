package me.mp1282.visualtest.system.inbuilt.category;

import me.mp1282.visualtest.system.inbuilt.InbuiltFunctionProvider;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@InbuiltFunctionProvider(providerName = "Logging")
public class LoggingInbuiltMethods {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static PrintWriter fileWriter = null;

    private static String format(String level, String message) {
        return "[" + LocalDateTime.now().format(FORMATTER) + "] [" + level + "] " + message;
    }

    private static void emit(String line) {
        System.out.println(line);
        if (fileWriter != null) {
            fileWriter.println(line);
            fileWriter.flush();
        }
    }

    // --- Log Levels ---

    public static void log(String message) {
        emit(format("LOG", message));
    }

    public static void logInfo(String message) {
        emit(format("INFO", message));
    }

    public static void logWarning(String message) {
        emit(format("WARN", message));
    }

    public static void logError(String message) {
        emit(format("ERROR", message));
    }

    /** Logs a labelled value — useful for quick variable inspection. */
    public static void logValue(String label, Object value) {
        emit(format("LOG", label + " = " + value));
    }

    /** Logs a separator line to make output easier to read. */
    public static void logSeparator() {
        emit("------------------------------");
    }

    // --- File Output Configuration ---

    /**
     * Opens (or creates) a log file and redirects log output to it in addition to stdout.
     * Appends to the file if it already exists.
     */
    public static boolean setLogFile(String path) {
        closeLogFile();
        try {
            fileWriter = new PrintWriter(new FileWriter(path, true));
            return true;
        } catch (IOException e) {
            fileWriter = null;
            return false;
        }
    }

    /** Closes the current log file output. Console output continues. */
    public static void closeLogFile() {
        if (fileWriter != null) {
            fileWriter.close();
            fileWriter = null;
        }
    }

    /**
     * Clears the log file at the given path (does not change the active log file target).
     * Returns true on success.
     */
    public static boolean clearLogFile(String path) {
        try {
            new FileWriter(path, false).close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private LoggingInbuiltMethods() throws IllegalAccessException {
        throw new IllegalAccessException("LoggingInbuiltMethods should not be instantiated");
    }
}
