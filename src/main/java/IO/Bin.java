package IO;

/*
 * Program     : Bin
 * Description : Read binary data from input stream
 * Author      : Matthias Schrock
 * Date        : 04-25-2024
 * Course      : CS375 Software Engineering II
 * Compile     : javac Bin.java
 * Execute     : N/A
 */

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Read binary data from input stream
 * <br><br>
 * "A wise person will hear and increase in learning,
 * And a person of understanding will acquire wise counsel"
 * (New American Standard Bible, 2020, Proverbs 1:5).
 * @see AutoCloseable
 */
public class Bin implements AutoCloseable {
    private static final int BYTE_SIZE = 8;
    private BufferedInputStream bis;
    private int buf;
    private int n;

    /**
     * Create a binary reader from a file
     * @param s file name
     * @throws IOException if an I/O error occurs
     */
    public Bin(String s) throws IOException {
        Path path = Paths.get(s);
        bis = new BufferedInputStream(Files.newInputStream(path));
        fill();
    }

    /**
     * Fill the buffer with a byte from the input stream
     * @throws IOException if an I/O error occurs
     */
    private void fill() throws IOException {
        buf = bis.read();
        n = BYTE_SIZE;
    }

    /**
     * Check if the input stream is empty
     * @return true if the input stream is empty, false otherwise
     */
    public boolean isEmpty() {
        return buf == -1;
    }

    /**
     * Check if the input stream is empty
     * @throws IOException if an I/O error occurs
     */
    private void checkEmpty() throws IOException {
        if (isEmpty()) {
            throw new IOException("Reading from empty input stream");
        }
    }

    /**
     * Read all bytes from the input stream
     * @return byte array
     * @throws IOException if an I/O error occurs
     */
    public byte[] readAllBytes() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        while (!isEmpty()) {
            buffer.write(readChar());
        }

        buffer.flush();
        return buffer.toByteArray();
    }

    /**
     * Read a byte from the input stream
     * @return byte value
     * @throws IOException if an I/O error occurs
     */
    public char readChar() throws IOException {
        checkEmpty();

        int x = buf;
        if (n == 8) {
            fill();
            return (char) (x & 0xff);
        }

        x <<= (BYTE_SIZE - n);
        int oldN = n;
        fill();

        checkEmpty();

        n = oldN;
        x |= buf >>> n;
        return (char) (x & 0xff);
    }

    /**
     * Read an integer from the input stream
     * @return integer value
     * @throws IOException if an I/O error occurs
     */
    public int readInt() throws IOException {
        int x = 0;
        for (int i = 0; i < 4; i++) {
            x <<= BYTE_SIZE;
            x |= readChar();
        }

        return x;
    }

    /**
     * Read a long from the input stream
     * @return long value
     * @throws IOException if an I/O error occurs
     */
    public long readLong() throws IOException {
        long x = 0;
        for (int i = 0; i < 8; i++) {
            x <<= BYTE_SIZE;
            x |= readChar();
        }

        return x;
    }

    /**
     * Close the input stream
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void close() throws IOException {
        bis.close();
    }
}