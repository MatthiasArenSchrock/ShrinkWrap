/*
 * Program     : SchubsLTest
 * Description : Test compress one to many files using LZW encoding
 * Author      : Matthias Schrock
 * Date        : 04-26-2024
 * Course      : CS375 Software Engineering II
 * Compile     : mvn compile
 * Execute     : mvn test
 */

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class SchubsLTest {
    private Path dir;

    @Before
    public void setUp() throws IOException {
        dir = Paths.get("src", "test", "resource", "SchubsL");
        Files.createDirectories(dir);
    }

    @Test
    public void testLZWEmptyFile() throws IOException {
        Path blankText = dir.resolve("Blank.txt");
        Files.write(blankText, new byte[0]);

        new SchubsL().compress(blankText.toString());

        // LZW is going to write value of R for empty file
        checkFileContents(Paths.get(blankText + ".ll"), new byte[] {-128});

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

    @Test(expected = IllegalArgumentException.class)
    public void testLZWWrongNumArgs() throws IOException {
        SchubsL.main(new String[] {});
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
            Files.write(entry.getKey(), entry.getValue().getBytes(), StandardOpenOption.CREATE);
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

        checkFileContents(Paths.get(longWordText + ".ll"),
                new byte[] {83, 117, 112, 101, 114, 99, 97, 108, 105, 102, 114, 97, 103, 105, -120, 115, 116, 105,
                        99, 101, 120, 112, 105, -121, 105, 100, 111, 99, 105, 111, 117, 115, -128});

        new Deschubs().deLZW(longWordText + ".ll");
        checkFileContents(longWordText, longWord.getBytes());
    }

    @Test
    public void testLZWLowercase() throws IOException {
        String lowercase = "abcdefghijklmnopqrstuvwxyz";
        Path lowercaseText = dir.resolve("Lowercase.txt");
        Files.write(lowercaseText, lowercase.getBytes());

        new SchubsL().compress(lowercaseText.toString());

        checkFileContents(Paths.get(lowercaseText + ".ll"),
                new byte[] {97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114,
                        115, 116, 117, 118, 119, 120, 121, 122, -128});

        new Deschubs().deLZW(lowercaseText + ".ll");
        checkFileContents(lowercaseText, lowercase.getBytes());
    }

    @Test
    public void testLZWUppercase() throws IOException {
        String uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Path uppercaseText = dir.resolve("Uppercase.txt");
        Files.write(uppercaseText, uppercase.getBytes());

        new SchubsL().compress(uppercaseText.toString());

        checkFileContents(Paths.get(uppercaseText + ".ll"),
                new byte[] {65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85,
                        86, 87, 88, 89, 90, -128});

        new Deschubs().deLZW(uppercaseText + ".ll");
        checkFileContents(uppercaseText, uppercase.getBytes());
    }

    @Test(expected = FileAlreadyExistsException.class)
    public void testLZWFileAlreadyExists() throws IOException {
        Path blank = dir.resolve("Blank.txt");
        Files.write(Path.of(blank + ".ll"), new byte[0], StandardOpenOption.CREATE);
        SchubsL.main(new String[] { blank.toString() });
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLZWInputFileIsDirectory() throws IOException {
        SchubsL.main(new String[] { dir.toString() });
    }

    private void checkFileContents(Path file, byte[] expected) throws IOException {
        byte[] actual = Files.readAllBytes(file);
        assertEquals(0, Arrays.compare(expected, actual));
    }
}