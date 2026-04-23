package me.mp1282.visualtesttesting.system.inbuilt;

import me.mp1282.visualtest.system.inbuilt.category.FileInbuiltMethods;
import me.mp1282.visualtest.system.inbuilt.category.LoggingInbuiltMethods;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TestLoggingInbuiltMethods {

    private Path tempDir;
    private String logFile;

    @Before
    public void setUp() throws IOException {
        tempDir = Files.createTempDirectory("vt-logging-test-");
        logFile = tempDir.resolve("test.log").toString();
        /* ensure no previous log file target is active */
        LoggingInbuiltMethods.closeLogFile();
    }

    @After
    public void tearDown() throws IOException {
        LoggingInbuiltMethods.closeLogFile();
        Files.walk(tempDir)
             .sorted(java.util.Comparator.reverseOrder())
             .forEach(p -> p.toFile().delete());
    }

    /* --- setLogFile / closeLogFile --- */

    @Test
    public void testSetLogFileCreatesFile() {
        Assert.assertTrue(LoggingInbuiltMethods.setLogFile(logFile));
        Assert.assertTrue(FileInbuiltMethods.fileExists(logFile));
    }

    @Test
    public void testSetLogFileReturnsFalseForInvalidPath() {
        String bad = tempDir.resolve("no-such-dir/file.log").toString();
        Assert.assertFalse(LoggingInbuiltMethods.setLogFile(bad));
    }

    /* closeLogFile must not throw when no file is open */
    @Test
    public void testCloseLogFileWhenNotOpen() {
        LoggingInbuiltMethods.closeLogFile();
        LoggingInbuiltMethods.closeLogFile(); // second call must also be safe
    }

    /* --- clearLogFile --- */

    @Test
    public void testClearLogFile() {
        FileInbuiltMethods.writeFile(logFile, "existing content");
        Assert.assertTrue(LoggingInbuiltMethods.clearLogFile(logFile));
        String content = FileInbuiltMethods.readFile(logFile);
        Assert.assertEquals("", content);
    }

    @Test
    public void testClearLogFileCreatesEmptyFile() {
        /* clearLogFile on a non-existent path should create an empty file */
        Assert.assertTrue(LoggingInbuiltMethods.clearLogFile(logFile));
        Assert.assertTrue(FileInbuiltMethods.fileExists(logFile));
        Assert.assertEquals("", FileInbuiltMethods.readFile(logFile));
    }

    @Test
    public void testClearLogFileReturnsFalseForInvalidPath() {
        String bad = tempDir.resolve("no-such-dir/file.log").toString();
        Assert.assertFalse(LoggingInbuiltMethods.clearLogFile(bad));
    }

    /* --- log methods write to file when a log file is set --- */

    @Test
    public void testLogWritesToFile() {
        LoggingInbuiltMethods.setLogFile(logFile);
        LoggingInbuiltMethods.log("test message");
        LoggingInbuiltMethods.closeLogFile();

        String content = FileInbuiltMethods.readFile(logFile);
        Assert.assertTrue(content.contains("[LOG]"));
        Assert.assertTrue(content.contains("test message"));
    }

    @Test
    public void testLogInfoWritesToFile() {
        LoggingInbuiltMethods.setLogFile(logFile);
        LoggingInbuiltMethods.logInfo("info message");
        LoggingInbuiltMethods.closeLogFile();

        String content = FileInbuiltMethods.readFile(logFile);
        Assert.assertTrue(content.contains("[INFO]"));
        Assert.assertTrue(content.contains("info message"));
    }

    @Test
    public void testLogWarningWritesToFile() {
        LoggingInbuiltMethods.setLogFile(logFile);
        LoggingInbuiltMethods.logWarning("warn message");
        LoggingInbuiltMethods.closeLogFile();

        String content = FileInbuiltMethods.readFile(logFile);
        Assert.assertTrue(content.contains("[WARN]"));
        Assert.assertTrue(content.contains("warn message"));
    }

    @Test
    public void testLogErrorWritesToFile() {
        LoggingInbuiltMethods.setLogFile(logFile);
        LoggingInbuiltMethods.logError("error message");
        LoggingInbuiltMethods.closeLogFile();

        String content = FileInbuiltMethods.readFile(logFile);
        Assert.assertTrue(content.contains("[ERROR]"));
        Assert.assertTrue(content.contains("error message"));
    }

    @Test
    public void testLogValueWritesToFile() {
        LoggingInbuiltMethods.setLogFile(logFile);
        LoggingInbuiltMethods.logValue("count", 42);
        LoggingInbuiltMethods.closeLogFile();

        String content = FileInbuiltMethods.readFile(logFile);
        Assert.assertTrue(content.contains("count"));
        Assert.assertTrue(content.contains("42"));
    }

    @Test
    public void testLogSeparatorWritesToFile() {
        LoggingInbuiltMethods.setLogFile(logFile);
        LoggingInbuiltMethods.logSeparator();
        LoggingInbuiltMethods.closeLogFile();

        String content = FileInbuiltMethods.readFile(logFile);
        Assert.assertTrue(content.contains("---"));
    }

    /* Multiple log calls must all appear in the file in order */
    @Test
    public void testMultipleLogsAppend() {
        LoggingInbuiltMethods.setLogFile(logFile);
        LoggingInbuiltMethods.logInfo("first");
        LoggingInbuiltMethods.logInfo("second");
        LoggingInbuiltMethods.logInfo("third");
        LoggingInbuiltMethods.closeLogFile();

        String content = FileInbuiltMethods.readFile(logFile);
        int posFirst  = content.indexOf("first");
        int posSecond = content.indexOf("second");
        int posThird  = content.indexOf("third");
        Assert.assertTrue(posFirst  < posSecond);
        Assert.assertTrue(posSecond < posThird);
    }

    /* setLogFile must append to an existing file, not overwrite it */
    @Test
    public void testSetLogFileAppendsToExisting() {
        FileInbuiltMethods.writeFile(logFile, "pre-existing\n");
        LoggingInbuiltMethods.setLogFile(logFile);
        LoggingInbuiltMethods.log("new entry");
        LoggingInbuiltMethods.closeLogFile();

        String content = FileInbuiltMethods.readFile(logFile);
        Assert.assertTrue(content.contains("pre-existing"));
        Assert.assertTrue(content.contains("new entry"));
    }
}
