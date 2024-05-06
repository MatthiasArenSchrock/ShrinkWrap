/*
 * Program     : DeschubsTest
 * Description : Test decompression one to many files or an archive using the extension as clue for decompressor file,
 *               LZW (.ll) or Huffman (.hh)
 * Author      : Matthias Schrock
 * Date        : 04-26-2024
 * Course      : CS375 Software Engineering II
 * Compile     : mvn compile
 * Execute     : mvn test
 */

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DeschubsTest {
    private Path dir;
    private final PrintStream originalErr = System.err;
    private final ByteArrayOutputStream newErr = new ByteArrayOutputStream();

    @Before
    public void setUp() throws IOException {
        dir = Paths.get("src", "test", "resource", "Deschubs");
        Files.createDirectories(dir);

        System.setErr(new PrintStream(newErr));
    }

    @After
    public void restoreStreams() {
        System.setErr(originalErr);
    }

    @Test
    public void testDeschubsWrongNumArgs() throws IOException {
        Deschubs.main(new String[] {});

        assertEquals(newErr.toString(), "Usage: java Deschubs <filename>.hh|ll|zl | <GLOB>" + "\n");
    }
    
    @Test
    public void testDeschubsUnsupportedCompress() throws IOException {
        Deschubs.main(new String[] { dir.resolve("test.inval").toString() });

        assertEquals(newErr.toString(), "Invalid file extension" + "\n");
    }

    @Test(expected = FileAlreadyExistsException.class)
    public void testExtractingExistingFiles() throws IOException {
        Path archive = dir.resolve("BleeBlahBlue.zl");
        Path blee = dir.resolve("Blee.txt");
        Path blah = dir.resolve("Blah.txt");
        Files.write(blee, "Blee".getBytes());
        Files.write(blah, "Blah".getBytes());

        new SchubsArc().compress(archive.toString(), blee.toString(), blah.toString());
        new Deschubs().unarchive(archive.toString());
    }

    @Test(expected = RuntimeException.class)
    public void testImproperCompression() throws IOException {
        Path archive = dir.resolve("BleeBlahBlue.zl");
        Files.write(archive, "Not proper archive format".getBytes());

        new Deschubs().unarchive(archive.toString());
    }
}