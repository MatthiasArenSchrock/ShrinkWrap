package IO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/*
 * Program     : BoutTest
 * Description : Test write binary data from input stream
 * Author      : Matthias Schrock
 * Date        : 04-26-2024
 * Course      : CS375 Software Engineering II
 * Compile     : mvn compile
 * Execute     : mvn test
 */

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class BoutTest {
    private Bout bout;
    private Path testFile;
    private static final String BLEE = "Blee\nBlah\nBlue";

    @Before
    public void setUp() throws IOException {
        Path dir = Paths.get("src", "test", "resource", "IO");
        Files.createDirectories(dir);

        testFile = dir.resolve("Blee.txt");
        Files.write(testFile, new byte[0], StandardOpenOption.CREATE);

        bout = new Bout(testFile.toString());
        assertNotNull(bout);
    }

    @After
    public void tearDown() throws IOException {
        if (bout != null) {
            bout.close();
        }
    }

    @Test(expected = FileAlreadyExistsException.class)
    public void testOpenOption() throws IOException {
        tearDown();
        bout = new Bout(testFile.toString(), StandardOpenOption.CREATE_NEW);
    }

    @Test
    public void testWriteBit() throws IOException {
        for (char c : BLEE.toCharArray()) {
            for (int i = 7; i >= 0; i--) {
                bout.writeBit(((c >> i) & 1) == 1);
            }
        }
        bout.close();

        testContent(BLEE);
    }

    @Test
    public void testWriteByte() throws IOException {
        for (char c : BLEE.toCharArray()) {
            bout.writeByte(c);
        }
        bout.close();

        testContent(BLEE);

        // Test offset
        // ASCII: 'a' = 97 = 01100001
        // bout writes (00110000 = '0' = 48) + 1
        setUp();
        bout.writeBit(false);
        bout.writeByte(97);
        bout.close();

        try (InputStream is = Files.newInputStream(testFile)) {
            assertEquals(48, is.read());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWriteByteGreaterThanMax() throws IOException {
        bout.writeByte(256);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWriteByteLessThanMin() throws IOException {
        bout.writeByte(-1);
    }

    @Test
    public void testWriteChar() throws IOException {
        for (char c : BLEE.toCharArray()) {
            bout.write(c);
        }
        bout.close();

        testContent(BLEE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWriteCharGreaterThanMax() throws IOException {
        bout.write((char) 256);
    }

    @Test
    public void testWriteString() throws IOException {
        bout.write(BLEE);
        bout.close();

        testContent(BLEE);
    }

    @Test
    public void testWriteInt() throws IOException {
        for (char c : BLEE.toCharArray()) {
            bout.write((int) c);
        }
        bout.close();

        char[] content = new String(Files.readAllBytes(testFile)).toCharArray();
        int n = 0;
        for (int i = 3; i < content.length; i+=4) {
            assertEquals(BLEE.charAt(n++), content[i]);
        }
    }

    @Test
    public void testWriteRbitInt() throws IOException {
        for (char c : BLEE.toCharArray()) {
            bout.write((int) c, 8);
        }
        bout.close();

        testContent(BLEE);
    }

    @Test
    public void testWriteOptimalRbitInt() throws IOException {
        bout.write(808464432, 32);
        bout.close();

        testContent("0000");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWriteRbitIntWithLenGreaterThanMax() throws IOException {
        bout.write(256, 33);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWriteRbitIntWithLenLessThanMin() throws IOException {
        bout.write(256, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWriteRbitIntGreaterThanMax() throws IOException {
        bout.write(256, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWriteRbitIntLessThanMin() throws IOException {
        bout.write(-1, 1);
    }

    @Test
    public void testWriteLong() throws IOException {
        for (char c : BLEE.toCharArray()) {
            bout.write((long) c);
        }
        bout.close();

        char[] content = new String(Files.readAllBytes(testFile)).toCharArray();
        int n = 0;
        for (int i = 7; i < content.length; i+=8) {
            assertEquals(BLEE.charAt(n++), content[i]);
        }
    }

    @Test
    public void testFlush() throws IOException {
        for (char c : BLEE.toCharArray()) {
            for (int i = 7; i >= 0; i--) {
                bout.writeBit(((c >> i) & 1) == 1);
            }
        }

        bout.flush();
        testContent(BLEE);
    }

    private void testContent(String expected) throws IOException {
        String content = new String(Files.readAllBytes(testFile));
        assertEquals(expected, content);
    }
}