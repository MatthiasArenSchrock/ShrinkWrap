package IO;

/*
 * Program     : BinTest
 * Description : Test read binary data from input stream
 * Author      : Matthias Schrock
 * Date        : 04-26-2024
 * Course      : CS375 Software Engineering II
 * Compile     : mvn compile
 * Execute     : mvn test
 */

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static org.junit.Assert.*;

public class BinTest {
    private Path testFile;
    private Bin bin;
    private static final String BLEE = "Blee\nBlah\nBlue";

    @Before
    public void setUp() throws IOException {
        Path dir = Paths.get("src", "test", "resource", "IO");
        Files.createDirectories(dir);

        testFile = dir.resolve("Blee.txt");
        Files.write(testFile, BLEE.getBytes(), StandardOpenOption.CREATE);

        bin = new Bin(testFile.toString());
        assertNotNull(bin);
    }

    @After
    public void tearDown() throws IOException {
        bin.close();
    }

    @Test
    public void testIsEmpty() throws IOException {
        assertFalse(bin.isEmpty());
        bin.readAllBytes();
        assertTrue(bin.isEmpty());
    }

    @Test
    public void testReadString() throws IOException {
        assertEquals(BLEE, bin.readString());
    }

    @Test
    public void testReadAllBytes() throws IOException {
        assertArrayEquals(BLEE.getBytes(), bin.readAllBytes());
    }

    @Test
    public void testReadChar() throws IOException {
        int i = 0;
        while(!bin.isEmpty()) {
            assertEquals(BLEE.charAt(i++), bin.readChar());
        }
        bin.close();

        // Test offset
        // File contains "Blee" = 01000010 01101100 01100101 01100101
        // throwing away the first bit, we expect 10000100 = 132
        setUp();
        bin.readBit();
        int expected = BLEE.charAt(0);
        expected <<= 1;
        assertEquals((char) expected, bin.readChar());
    }

    @Test(expected = IOException.class)
    public void testReadCharWithEmptyStream() throws IOException {
        while(!bin.isEmpty()) {
            bin.readChar();
        }

        bin.readChar();
    }

    @Test
    public void testReadInt() throws IOException {
        int expected = 0;
        for (int i = 0; i < 4; i++) {
            expected <<= 8;
            expected |= BLEE.charAt(i);
        }

        assertEquals(expected, bin.readInt());
    }

    @Test
    public void testReadIntWithR() throws IOException {
        int i = 0;
        while(!bin.isEmpty()) {
            assertEquals((int) BLEE.charAt(i++), bin.readInt(8));
        }
    }

    @Test
    public void testReadOptimalRbitInt() throws IOException {
        int expected = 0;
        for (int i = 0; i < 4; i++) {
            expected <<= 8;
            expected |= BLEE.toCharArray()[i];
        }
        assertEquals(expected, bin.readInt(32));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testReadIntWithRAboveMax() throws IOException {
        bin.readInt(33);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testReadIntWithRBelowMin() throws IOException {
        bin.readInt(0);
    }

    @Test
    public void testReadLong() throws IOException {
        long expected = 0;
        for (int i = 0; i < 8; i++) {
            expected <<= 8;
            expected |= BLEE.charAt(i);
        }

        assertEquals(expected, bin.readLong());
    }

    @Test
    public void testReadBit() throws IOException {
        for (char c : BLEE.toCharArray()) {
            for (int i = 7; i >= 0; i--) {
                assertEquals(((c >> i) & 1) == 1, bin.readBit());
            }
        }
    }
}