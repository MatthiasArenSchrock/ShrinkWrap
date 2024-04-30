package IO;

/*
 * Program     : Bout
 * Description : Write binary data from input stream
 * Author      : Matthias Schrock
 * Date        : 04-25-2024
 * Course      : CS375 Software Engineering II
 * Compile     : javac Bout.java
 * Execute     : N/A
 */

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Read binary data from input stream
 * <br><br>
 * "The tongue of the wise makes knowledge pleasant,
 * but the mouth of fools spouts foolishness"
 * (New American Standard Bible, 2020, Proverbs 15:2).
 * @see AutoCloseable
 */
public class Bout implements AutoCloseable {
    private final OutputStream bos;
    private int buf;
    private int n;

    /**
     * Create a binary writer from a file
     * @param s file name
     * @throws IOException if an I/O error occurs
     */
    public Bout(String s) throws IOException {
        bos = new BufferedOutputStream(Files.newOutputStream(Paths.get(s)));
        buf = 0;
        n = 0;
    }

    /**
     * Create a binary writer from a buffered output stream
     * @param baos the buffered output stream
     */
    public Bout(ByteArrayOutputStream baos) {
        this.bos = baos;
        buf = 0;
        n = 0;
    }

    /**
     * Write a bit/boolean to the output stream
     * @param bit the bit/boolean to write
     * @throws IOException if an I/O error occurs
     */
    public void writeBit(boolean bit) throws IOException {
        buf <<= 1;

        if (bit) {
            buf |= 1;
        }

        n++;
        if (n == Byte.SIZE) {
            clearBuf();
        }
    }

    /**
     * Write 8-bit byte to the output stream
     * @param x the byte to write
     * @throws IOException if an I/O error occurs
     */
    public void writeByte(int x) throws IOException {
        if (x < 0 || x >= 256) {
            throw new IllegalArgumentException("Illegal 8-bit byte: " + x);
        }

        if (n == 0) {
            bos.write(x);
            return;
        }

        for (int i = 0; i < Byte.SIZE; i++) {
            writeBit(((x >>> (Byte.SIZE - i - 1)) & 1) == 1);
        }
    }

    /**
     * Write 8-bit char to the output stream
     * @param c the character to write
     * @throws IOException if an I/O error occurs
     */
    public void write(char c) throws IOException {
        if (c >= 256) {
            throw new IllegalArgumentException("Illegal 8-bit char: " + c);
        }

        writeByte(c);
    }

    /**
     * Write a string to the output stream
     * @param s the string to write
     * @throws IOException if an I/O error occurs
     */
    public void write(String s) throws IOException {
        for (char c : s.toCharArray()) {
            write(c);
        }
    }

    /**
     * Write 32-bit int to the output stream
     * @param x the integer to write
     * @throws IOException if an I/O error occurs
     */
    public void write(int x) throws IOException {
        writeByte((x >>> 24) & 0xff);
        writeByte((x >>> 16) & 0xff);
        writeByte((x >>> 8) & 0xff);
        writeByte((x) & 0xff);
    }

    /**
     * Write r-bit int to the output stream
     * @param x the integer to write
     * @param r the number of relevant bits in the integer
     * @throws IOException if an I/O error occurs
     */
    public void write(int x, int r) throws IOException {
        if (r == 32) {
            write(x);
            return;
        }
        if (r < 1 || r > 32) {
            throw new IllegalArgumentException("Illegal value for r = " + r);
        }
        if (x < 0 || x >= (1 << r)) {
            throw new IllegalArgumentException("Illegal " + r + "-bit char: " + x);
        }

        for (int i = 0; i < r; i++) {
            writeBit(((x >>> (r - i - 1)) & 1) == 1);
        }
    }

    /**
     * Write 64-bit long to output stream
     * @param l the long to write
     * @throws IOException if an I/O error occurs
     */
    public void write(long l) throws IOException {
        write((int) (l >>> 32));
        write((int) l);
    }

    /**
     * Close the output stream
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void close() throws IOException {
        flush();
        bos.close();
    }

    /**
     * flush the buffer
     * @throws IOException if an I/O error occurs
     */
    public void flush() throws IOException {
        clearBuf();
        bos.flush();
    }

    /**
     * Clear the buffer
     * @throws IOException if an I/O error occurs
     */
    private void clearBuf() throws IOException {
        if (n == 0) {
            return;
        }
        if (n > 0) {
            buf <<= (Byte.SIZE - n);
        }

        bos.write(buf);
        n = 0;
        buf = 0;
    }
}