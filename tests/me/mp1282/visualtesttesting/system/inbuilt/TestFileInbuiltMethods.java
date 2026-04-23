package me.mp1282.visualtesttesting.system.inbuilt;

import me.mp1282.visualtest.system.inbuilt.category.FileInbuiltMethods;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class TestFileInbuiltMethods {

    private Path tempDir;
    private String testFile;

    @Before
    public void setUp() throws IOException {
        tempDir  = Files.createTempDirectory("vt-file-test-");
        testFile = tempDir.resolve("test.txt").toString();
    }

    @After
    public void tearDown() throws IOException {
        /* best-effort cleanup */
        Files.walk(tempDir)
             .sorted(java.util.Comparator.reverseOrder())
             .forEach(p -> p.toFile().delete());
    }

    /* --- write / read --- */

    @Test
    public void testWriteAndReadFile() {
        Assert.assertTrue(FileInbuiltMethods.writeFile(testFile, "hello world"));
        Assert.assertEquals("hello world", FileInbuiltMethods.readFile(testFile));
    }

    /* writeFile must overwrite existing content */
    @Test
    public void testWriteOverwrites() {
        FileInbuiltMethods.writeFile(testFile, "first");
        FileInbuiltMethods.writeFile(testFile, "second");
        Assert.assertEquals("second", FileInbuiltMethods.readFile(testFile));
    }

    /* readFile on a non-existent path must return an empty string, not throw */
    @Test
    public void testReadFileMissing() {
        String result = FileInbuiltMethods.readFile(testFile + ".missing");
        Assert.assertEquals("", result);
    }

    /* --- append --- */

    @Test
    public void testAppendToFile() {
        FileInbuiltMethods.writeFile(testFile, "line1");
        Assert.assertTrue(FileInbuiltMethods.appendToFile(testFile, "line2"));
        String content = FileInbuiltMethods.readFile(testFile);
        Assert.assertTrue(content.contains("line1"));
        Assert.assertTrue(content.contains("line2"));
    }

    @Test
    public void testAppendLine() {
        FileInbuiltMethods.writeFile(testFile, "");
        FileInbuiltMethods.appendLine(testFile, "alpha");
        FileInbuiltMethods.appendLine(testFile, "beta");
        ArrayList<Object> lines = FileInbuiltMethods.readLines(testFile);
        Assert.assertEquals(2, lines.size());
        Assert.assertEquals("alpha", lines.get(0));
        Assert.assertEquals("beta",  lines.get(1));
    }

    /* --- readLines / readLine --- */

    @Test
    public void testReadLines() {
        FileInbuiltMethods.writeFile(testFile, "a\nb\nc");
        ArrayList<Object> lines = FileInbuiltMethods.readLines(testFile);
        Assert.assertEquals(3, lines.size());
        Assert.assertEquals("a", lines.get(0));
        Assert.assertEquals("c", lines.get(2));
    }

    @Test
    public void testReadLineByIndex() {
        FileInbuiltMethods.writeFile(testFile, "first\nsecond\nthird");
        Assert.assertEquals("first",  FileInbuiltMethods.readLine(testFile, 0));
        Assert.assertEquals("second", FileInbuiltMethods.readLine(testFile, 1));
        Assert.assertEquals("third",  FileInbuiltMethods.readLine(testFile, 2));
    }

    /* out-of-bounds line index must return empty string, not throw */
    @Test
    public void testReadLineOutOfBounds() {
        FileInbuiltMethods.writeFile(testFile, "only line");
        Assert.assertEquals("", FileInbuiltMethods.readLine(testFile, 5));
        Assert.assertEquals("", FileInbuiltMethods.readLine(testFile, -1));
    }

    @Test
    public void testReadLinesMissing() {
        ArrayList<Object> lines = FileInbuiltMethods.readLines(testFile + ".missing");
        Assert.assertNotNull(lines);
        Assert.assertTrue(lines.isEmpty());
    }

    /* --- fileExists / isDirectory --- */

    @Test
    public void testFileExists() {
        Assert.assertFalse(FileInbuiltMethods.fileExists(testFile));
        FileInbuiltMethods.writeFile(testFile, "");
        Assert.assertTrue(FileInbuiltMethods.fileExists(testFile));
    }

    @Test
    public void testIsDirectory() {
        Assert.assertTrue (FileInbuiltMethods.isDirectory(tempDir.toString()));
        FileInbuiltMethods.writeFile(testFile, "");
        Assert.assertFalse(FileInbuiltMethods.isDirectory(testFile));
    }

    /* --- fileSize --- */

    @Test
    public void testFileSize() {
        FileInbuiltMethods.writeFile(testFile, "abc");
        long size = FileInbuiltMethods.fileSize(testFile);
        Assert.assertTrue(size > 0);
    }

    /* missing file must return -1 */
    @Test
    public void testFileSizeMissing() {
        Assert.assertEquals(-1L, FileInbuiltMethods.fileSize(testFile + ".missing"));
    }

    /* --- deleteFile --- */

    @Test
    public void testDeleteFile() {
        FileInbuiltMethods.writeFile(testFile, "to be deleted");
        Assert.assertTrue(FileInbuiltMethods.fileExists(testFile));
        Assert.assertTrue(FileInbuiltMethods.deleteFile(testFile));
        Assert.assertFalse(FileInbuiltMethods.fileExists(testFile));
    }

    /* deleting a non-existent file must return false (deleteIfExists semantics) */
    @Test
    public void testDeleteFileMissing() {
        Assert.assertFalse(FileInbuiltMethods.deleteFile(testFile + ".missing"));
    }

    /* --- createDirectories / listFiles --- */

    @Test
    public void testCreateDirectories() {
        String nested = tempDir.resolve("a/b/c").toString();
        Assert.assertTrue(FileInbuiltMethods.createDirectories(nested));
        Assert.assertTrue(FileInbuiltMethods.isDirectory(nested));
    }

    @Test
    public void testListFiles() {
        FileInbuiltMethods.writeFile(tempDir.resolve("f1.txt").toString(), "");
        FileInbuiltMethods.writeFile(tempDir.resolve("f2.txt").toString(), "");
        ArrayList<Object> names = FileInbuiltMethods.listFiles(tempDir.toString());
        Assert.assertTrue(names.contains("f1.txt"));
        Assert.assertTrue(names.contains("f2.txt"));
    }

    /* listFiles on a missing directory must return empty list, not throw */
    @Test
    public void testListFilesMissingDir() {
        ArrayList<Object> names = FileInbuiltMethods.listFiles(tempDir.resolve("no-such-dir").toString());
        Assert.assertNotNull(names);
        Assert.assertTrue(names.isEmpty());
    }

    /* --- path utilities --- */

    @Test
    public void testGetFileName() {
        Assert.assertEquals("test.txt", FileInbuiltMethods.getFileName(testFile));
    }

    @Test
    public void testGetFileNameRoot() {
        /* root path has no parent filename component */
        Assert.assertEquals("", FileInbuiltMethods.getFileName(""));
    }

    @Test
    public void testGetParentPath() {
        String parent = FileInbuiltMethods.getParentPath(testFile);
        Assert.assertEquals(tempDir.toString(), parent);
    }

    @Test
    public void testJoinPath() {
        String joined = FileInbuiltMethods.joinPath(tempDir.toString(), "sub/file.txt");
        Assert.assertTrue(joined.endsWith("file.txt"));
        Assert.assertTrue(joined.contains("sub"));
    }

    @Test
    public void testAbsolutePath() {
        String abs = FileInbuiltMethods.absolutePath(testFile);
        /* absolute path must not contain ./ or ../ segments */
        Assert.assertFalse(abs.contains("./"));
        Assert.assertTrue(abs.length() > 0);
    }
}
