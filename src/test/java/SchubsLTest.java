/*
 * Program     : SchubsLTest
 * Description : Test compress one to many files using LZW encoding
 * Author      : Matthias Schrock
 * Date        : 04-26-2024
 * Course      : CS375 Software Engineering II
 * Compile     : mvn compile
 * Execute     : mvn test
 */

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SchubsLTest {
    private Path dir;
    private final PrintStream originalErr = System.err;
    private final ByteArrayOutputStream newErr = new ByteArrayOutputStream();

    @Before
    public void setUp() throws IOException {
        dir = Paths.get("src", "test", "resource", "SchubsL");
        Files.createDirectories(dir);

        System.setErr(new PrintStream(newErr));
    }

    @After
    public void restoreStreams() {
        System.setErr(originalErr);
    }

    @Test
    public void testLZWEmptyFile() throws IOException {
        Path blankText = dir.resolve("Blank.txt");
        Files.write(blankText, new byte[0]);

        new SchubsL().compress(blankText.toString());

        // LZW is going to write value of R for empty file
        checkFileContents(Paths.get(blankText + ".ll"), new byte[] { 16, 0 });

        new Deschubs().deLZW(blankText + ".ll");
        checkFileContents(blankText, new byte[0]);
    }

    @Test(expected = NoSuchFileException.class)
    public void testLZWMissingFile() throws IOException {
        new SchubsL().compress("Missing.txt");
    }

    @Test
    public void testLZWManyThings() throws IOException {
        String manyThingsText = """
                "The fear of the Lord is the beginning of wisdom,
                and the knowledge of the Holy One is understanding"
                (New American Standard Bible, 2020, Proverbs 9:10).
                """;
        Path manyThings = dir.resolve("ManyThings.txt");
        Files.write(manyThings, manyThingsText.getBytes());

        new SchubsL().compress(manyThings.toString());
        new Deschubs().deLZW(manyThings + ".ll");

        checkFileContents(manyThings, manyThingsText.getBytes());
    }

    @Test
    public void testLZWGlob() throws IOException {
        Path glob = dir.resolve("glob");
        Files.createDirectories(glob);
        Map<Path, String> files = new HashMap<>();
        files.put(dir.resolve("Blee.txt"), "Blee\nBlah\nBlue");
        files.put(glob.resolve("Blah.txt"), "Blah");
        files.put(glob.resolve("Blue.txt"), "Blue");

        for (Map.Entry<Path, String> entry : files.entrySet()) {
            Files.write(entry.getKey(), entry.getValue().getBytes());
            Files.deleteIfExists(Paths.get(entry.getKey() + ".ll"));
        }

        SchubsL.main(files.keySet().stream().map(Path::toString).toArray(String[]::new));
        Deschubs.main(files.keySet().stream().map(Path::toString).map(s -> s + ".ll").toArray(String[]::new));

        for (Map.Entry<Path, String> entry : files.entrySet()) {
            checkFileContents(entry.getKey(), entry.getValue().getBytes());
        }
    }

    @Test
    public void testLZWLongword() throws IOException {
        String longWord = "Supercalifragilisticexpialidocious";
        Path longWordText = dir.resolve("LongWord.txt");
        Files.write(longWordText, longWord.getBytes());

        new SchubsL().compress(longWordText.toString());
        new Deschubs().deLZW(longWordText + ".ll");
        checkFileContents(longWordText, longWord.getBytes());
    }

    @Test
    public void testLZWLowercase() throws IOException {
        String lowercase = "abcdefghijklmnopqrstuvwxyz";
        Path lowercaseText = dir.resolve("Lowercase.txt");
        Files.write(lowercaseText, lowercase.getBytes());

        new SchubsL().compress(lowercaseText.toString());
        new Deschubs().deLZW(lowercaseText + ".ll");
        checkFileContents(lowercaseText, lowercase.getBytes());
    }

    @Test
    public void testLZWUppercase() throws IOException {
        String uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Path uppercaseText = dir.resolve("Uppercase.txt");
        Files.write(uppercaseText, uppercase.getBytes());

        new SchubsL().compress(uppercaseText.toString());
        new Deschubs().deLZW(uppercaseText + ".ll");
        checkFileContents(uppercaseText, uppercase.getBytes());
    }

    @Test
    public void testLZWWrongNumArgs() {
        SchubsL.main(new String[] {});

        assertEquals(newErr.toString(), "Usage: java SchubsL <filename> | <GLOB>" + "\n");
    }

    @Test
    public void testLZWFileAlreadyExists() throws IOException {
        Path blank = dir.resolve("Blank.txt");
        Files.write((blank), new byte[0]);
        Files.write(Path.of(blank + ".ll"), new byte[0]);
        SchubsL.main(new String[] { blank.toString() });

        assertEquals(newErr.toString(), blank.toString() + ".ll already exists. Try deleting or renaming it first." + "\n");
    }

    @Test
    public void testLZWInputFileIsDirectory() {
        SchubsL.main(new String[] { dir.toString() });

        assertEquals(newErr.toString(), "Input file is a directory. Use Glob instead: " + "java SchubsL "
                + dir.toString() + File.separator + "<glob>" + "\n");
    }

    private void checkFileContents(Path file, byte[] expected) throws IOException {
        byte[] actual = Files.readAllBytes(file);
        assertEquals(0, Arrays.compare(expected, actual));
    }
}