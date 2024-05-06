/*
 * Program     : SchubsHTest
 * Description : Test compress one to many files using Huffman encoding
 * Author      : Matthias Schrock
 * Date        : 04-26-2024
 * Course      : CS375 Software Engineering II
 * Compile     : mvn compile
 * Execute     : mvn test
 */

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class SchubsHTest {
    private Path dir;

    @Before
    public void setUp() throws IOException {
        dir = Paths.get("src", "test", "resource", "SchubsH");
        Files.createDirectories(dir);
    }

    @Test
    public void testHuffmanNormalOperation() throws IOException {
        String blee = "Blee\nBlah\nBlue";
        Path bleeText = dir.resolve("Blee.txt");
        Files.write(bleeText, blee.getBytes(), StandardOpenOption.CREATE);

        new SchubsH().compress(bleeText.toString());

        checkFileContents(Paths.get(bleeText + ".hh"),
                new byte[] {44, -74, -59, 8, -80, -38, 23, 88, 80, 0, 0, 0, 116, -121, -50, 111, -49, 0});

        new Deschubs().deHuffman(bleeText + ".hh");
        checkFileContents(bleeText, blee.getBytes());
    }

    @Test
    public void testHuffmanEmptyFile() throws IOException {
        Path blankText = dir.resolve("Blank.txt");
        Files.write(blankText, new byte[0], StandardOpenOption.CREATE);

        new SchubsH().compress(blankText.toString());

        // EOF, 0 - root, 0000 - 4-byte int length
        checkFileContents(Paths.get(blankText + ".hh"),
                new byte[] {-128, 0, 0, 0, 0, 0});

        new Deschubs().deHuffman(blankText + ".hh");
        checkFileContents(blankText, new byte[0]);
    }

    @Test(expected = NoSuchFileException.class)
    public void testHuffmanMissingFile() throws IOException {
        new SchubsH().compress("Missing.txt");
    }

    @Test
    public void testHuffmanManyThings() throws IOException {
        String manyThingsText = """
                "Your speech must always be with grace,
                as though seasoned with salt,
                so that you will know how you should respond to each person"
                (New American Standard Bible, 2020, Colossians 4:6).
                """;
        Path manyThingsTest = dir.resolve("ManyThings.txt");
        Files.write(manyThingsTest, manyThingsText.getBytes());

        new SchubsH().compress(manyThingsTest.toString());
        new Deschubs().deHuffman(manyThingsTest + ".hh");

        checkFileContents(manyThingsTest, manyThingsText.getBytes());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHuffmanWrongNumArgs() throws IOException {
        SchubsH.main(new String[] {});
    }

    @Test
    public void testHuffmanGlob() throws IOException {
        Path glob = dir.resolve("glob");
        Files.createDirectories(glob);
        Map<Path, String> files = new HashMap<>();
        files.put(dir.resolve("Blee.txt"), "Blee\nBlah\nBlue");
        files.put(glob.resolve("Blah.txt"), "Blah");
        files.put(glob.resolve("Blue.txt"), "Blue");

        for (Map.Entry<Path, String> entry : files.entrySet()) {
            Files.write(entry.getKey(), entry.getValue().getBytes(), StandardOpenOption.CREATE);
            Files.deleteIfExists(Paths.get(entry.getKey() + ".hh"));
        }

        SchubsH.main(files.keySet().stream().map(Path::toString).toArray(String[]::new));
        Deschubs.main(files.keySet().stream().map(Path::toString).map(s -> s + ".hh").toArray(String[]::new));

        for (Map.Entry<Path, String> entry : files.entrySet()) {
            checkFileContents(entry.getKey(), entry.getValue().getBytes());
        }
    }

    @Test
    public void testHuffmanLongWord() throws IOException {
        String longWord = "Supercalifragilisticexpialidocious";
        Path longWordText = dir.resolve("LongWord.txt");
        Files.write(longWordText, longWord.getBytes());

        new SchubsH().compress(longWordText.toString());

        checkFileContents(Paths.get(longWordText + ".hh"),
                new byte[] {45, 37, -51, 103, -70, 46, -74, 81, 111, -72, 23, -117, 53,
                        -56, 84, -20, -106, 21, -78,-58, 0, 0, 0, 69, -122, -105, -65, -34,
                        43, 122, -93, -124, 89, -17, 73, 55, -116, -57, -112, -56});

        new Deschubs().deHuffman(longWordText + ".hh");
        checkFileContents(longWordText, longWord.getBytes());
    }

    @Test
    public void testHuffmanLowercase() throws IOException {
        String lowercase = "abcdefghijklmnopqrstuvwxyz";
        Path lowercaseText = dir.resolve("Lowercase.txt");
        Files.write(lowercaseText, lowercase.getBytes());

        new SchubsH().compress(lowercaseText.toString());

        checkFileContents(Paths.get(lowercaseText + ".hh"),
                new byte[] {11, -83, -59, 114, -71, -106, 107, 97, 111, -74, -83, 118, 48, -80, -34, -106,
                        123, 113, 116, -76, -81, 22, 65, 101, -75, 47, 54, 34, -17, 104, 93, -82, 0, 0, 0,
                        3, 80, -37, -17, -124, -105, 107, -105, 43, 102, -49, -119, 29, 3, -36, -74, -94});

        new Deschubs().deHuffman(lowercaseText + ".hh");
        checkFileContents(lowercaseText, lowercase.getBytes());
    }

    @Test
    public void testHuffmanUppercase() throws IOException {
        String uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Path uppercaseText = dir.resolve("Uppercase.txt");
        Files.write(uppercaseText, uppercase.getBytes());

        new SchubsH().compress(uppercaseText.toString());

        checkFileContents(Paths.get(uppercaseText + ".hh"),
                new byte[] {10, -83, 69, 82, -87, -108, 106, 97, 79, -90, -87, 116, 48, -96, -42, -108,122,
                        113, 84, -92, -85, 20, 65, 69, -91, 43, 52, 34, -81, 72, 85, -86, 0, 0, 0, 3, 80,
                        -37, -17, -124, -105, 107, -105, 43, 102, -49, -119, 29, 3, -36, -74, -94});

        new Deschubs().deHuffman(uppercaseText + ".hh");
        checkFileContents(uppercaseText, uppercase.getBytes());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHuffmanFileAlreadyExists() throws IOException {
        Path blank = dir.resolve("Blank.txt");
        Files.write(Path.of(blank + ".hh"), new byte[0], StandardOpenOption.CREATE);
        SchubsH.main(new String[] { blank.toString() });
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHuffmanInputFileIsDirectory() throws IOException {
        SchubsH.main(new String[] { dir.toString() });
    }

    private void checkFileContents(Path file, byte[] expected) throws IOException {
        byte[] actual = Files.readAllBytes(file);
        assertEquals(0, Arrays.compare(expected, actual));
    }
}