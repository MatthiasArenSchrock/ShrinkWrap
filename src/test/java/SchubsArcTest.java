/*
 * Program     : SchubsArcTest
 * Description : Test create a tar of files using LZW encoding
 * Author      : Matthias Schrock
 * Date        : 04-26-2024
 * Course      : CS375 Software Engineering II
 * Compile     : mvn compile
 * Execute     : mvn test
 */

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SchubsArcTest {
    private Path dir;
    private final PrintStream originalErr = System.err;
    private ByteArrayOutputStream newErr;

    @Before
    public void setUp() throws IOException {
        dir = Paths.get("src", "test", "resource", "SchubsArc");
        Files.createDirectories(dir);

        newErr = new ByteArrayOutputStream();
        System.setErr(new PrintStream(newErr));
    }

    @After
    public void restoreStreams() throws IOException {
        newErr.close();
        System.setErr(originalErr);
    }

    @Test
    public void testArcWrongNumArgs() {
        SchubsArc.main(new String[] {});

        assertHasErrorMessage();
    }

    @Test
    public void testArcFileExtension() throws IOException {
        Path blee = dir.resolve("Blee.txt");
        Path arc = dir.resolve("Blee.zl");
        Files.write(blee, "Blee\nBlah\nBlue".getBytes());

        Files.deleteIfExists(arc);
        SchubsArc
                .main(new String[] { blee.toString().substring(0, blee.toString().lastIndexOf('.')), blee.toString() });
        assertTrue(Files.exists(arc));
    }

    @Test
    public void testArcEmptyFile() throws IOException {
        Path blankText = dir.resolve("Blank.txt");
        Path blankArc = dir.resolve("Blank.zl");
        Files.write(blankText, new byte[0]);

        new SchubsArc().compress(blankArc.toString(), new String[] { blankText.toString() });
        new Deschubs().unarchive(blankArc.toString());
        checkFileContents(blankText, new byte[0]);
    }

    @Test(expected = IOException.class)
    public void testArcMissingFile() throws IOException {
        new SchubsArc().compress(dir.resolve("Missing.zl").toString(), new String[] { "Missing.txt" });
    }

    @Test
    public void testArcAlreadyExists() throws IOException {
        Path blankText = dir.resolve("Blank.txt");
        Path blankArc = dir.resolve("Blank.zl");
        Files.write(blankText, new byte[0]);
        Files.write(blankArc, new byte[0]);

        SchubsArc.main(new String[] { blankArc.toString(), blankText.toString() });

        assertHasErrorMessage();
    }

    @Test
    public void testArcDoesNotClobber() throws IOException {
        Path blee = dir.resolve("Blee.txt");
        Path arc = dir.resolve("Blee.zl");
        Files.write(blee, "Blee\nBlah\nBlue".getBytes());
        Files.deleteIfExists(arc);

        SchubsArc.main(new String[] { arc.toString(), blee.toString() });
        Deschubs.main(new String[] { arc.toString() });

        assertHasErrorMessage();
    }

    @Test
    public void testArcGlob() throws IOException {
        Path glob = dir.resolve("glob");
        Files.createDirectories(glob);
        Map<Path, String> files = new HashMap<>();
        files.put(dir.resolve("Blee.txt"), "Blee\nBlah\nBlue");
        files.put(glob.resolve("Blah.txt"), "Blah");
        files.put(glob.resolve("Blue.txt"), "Blue");

        // Create test files
        for (Map.Entry<Path, String> entry : files.entrySet()) {
            Files.write(entry.getKey(), entry.getValue().getBytes());
        }

        // Remove the archive file if it exists
        Files.deleteIfExists(dir.resolve("Glob.zl"));

        // Create the archive
        String[] originalArray = files.keySet().stream().map(Path::toString).toArray(String[]::new);
        String[] newArray = new String[originalArray.length + 1];
        newArray[0] = dir.resolve("Glob.zl").toString();
        System.arraycopy(originalArray, 0, newArray, 1, originalArray.length);
        SchubsArc.main(newArray);

        // Delete original files
        for (Map.Entry<Path, String> entry : files.entrySet()) {
            Files.deleteIfExists(entry.getKey());
        }

        // Decompress
        Deschubs.main(new String[] { dir.resolve("glob.zl").toString() });

        // Check for original files
        for (Map.Entry<Path, String> entry : files.entrySet()) {
            checkFileContents(entry.getKey(), entry.getValue().getBytes());
        }
    }

    @Test(expected = IOException.class)
    public void testArcInputFileIsDirectory() throws IOException {
        new SchubsArc().compress(dir.resolve("Dir.zl").toString(), new String[] { dir.toString() });
    }

    @Test
    public void testArcManyThings() throws IOException {
        String daniel221 = """
                "It is He who changes the times and the periods;
                He removes kings and appoints kings;
                He gives wisdom to wise men,
                and knowledge to people of understanding"
                (New American Standard Bible, 2020, Daniel 2:21).
                """;
        String daniel222 = """
                "It is He who reveals the profound and hidden things;
                He knows what is in the darkness,
                and the light dwells with Him"
                (New American Standard Bible, 2020, Daniel 2:22).
                """;
        Path d221 = dir.resolve("Daniel2-21.txt");
        Path d222 = dir.resolve("Daniel2-22.txt");
        Path archive = dir.resolve("Daniel.zl");
        Files.write(d221, daniel221.getBytes());
        Files.write(d222, daniel222.getBytes());

        new SchubsArc().compress(archive.toString(), new String[] { d221.toString(), d222.toString() });
        new Deschubs().unarchive(archive.toString());

        checkFileContents(d221, daniel221.getBytes());
        checkFileContents(d222, daniel222.getBytes());
    }

    @Test
    public void testArcLongword() throws IOException {
        String longWord = "Supercalifragilisticexpialidocious";
        Path longWordPath = dir.resolve("LongWord.txt");
        Files.write(longWordPath, longWord.getBytes());

        new SchubsArc().compress(dir.resolve("LongWord.zl").toString(), new String[] { longWordPath.toString() });
        new Deschubs().unarchive(dir.resolve("LongWord.zl").toString());

        checkFileContents(longWordPath, longWord.getBytes());
    }

    @Test
    public void testArcLowercase() throws IOException {
        String lowercase = "abcdefghijklmnopqrstuvwxyz";
        Path lowercasePath = dir.resolve("Lowercase.txt");
        Files.write(lowercasePath, lowercase.getBytes());

        new SchubsArc().compress(dir.resolve("Lowercase.zl").toString(), new String[] { lowercasePath.toString() });
        new Deschubs().unarchive(dir.resolve("Lowercase.zl").toString());

        checkFileContents(lowercasePath, lowercase.getBytes());
    }

    @Test
    public void testArcUppercase() throws IOException {
        String uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Path uppercasePath = dir.resolve("Uppercase.txt");
        Files.write(uppercasePath, uppercase.getBytes());

        new SchubsArc().compress(dir.resolve("Uppercase.zl").toString(), new String[] { uppercasePath.toString() });
        new Deschubs().unarchive(dir.resolve("Uppercase.zl").toString());

        checkFileContents(uppercasePath, uppercase.getBytes());
    }

    private void checkFileContents(Path file, byte[] expected) throws IOException {
        byte[] actual = Files.readAllBytes(file);
        assertEquals(0, Arrays.compare(expected, actual));
    }

    private void assertHasErrorMessage() {
        assertTrue(newErr.size() > 0);
    }
}