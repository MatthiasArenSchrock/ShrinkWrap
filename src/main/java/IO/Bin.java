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
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Read binary data from input stream
 *
 * <br><br>
 * "A wise person will hear and increase in learning,
 * And a person of understanding will acquire wise counsel"
 * (New American Standard Bible, 2020, Proverbs 1:5).
 * @see AutoCloseable
 * @author Matthias Schrock
 */
public class Bin implements AutoCloseable {
    private final InputStream bis;
    private int buf;
    private int n;

    /**
     * Create a binary reader from a file
     * @param s file name
     * @throws IOException if an I/O error occurs
     */
    public Bin(String s) throws IOException {
        try {
            Path path = Paths.get(s);
            bis = new BufferedInputStream(Files.newInputStream(path));
            fill();
        } catch (NoSuchFileException e) {
            throw new NoSuchFileException("File not found: " + s);
        } catch (IOException e) {
            throw new IOException("Error opening file " + s);
        }
    }

    /**
     * Create a binary reader from an input stream
     * @param is the input stream
     */
    public Bin(InputStream is) {
        bis = is;
        buf = 0;
        n = 0;
    }

    /**
     * Fill the buffer with a byte from the input stream
     * @throws IOException if an I/O error occurs
     */
    private void fill() throws IOException {
        buf = bis.read();
        n = Byte.SIZE;
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
     * Read a string from the input stream
     * @return string value
     * @throws IOException if an I/O error occurs
     */
    public String readString() throws IOException {
        StringBuilder sb = new StringBuilder();
        while (!isEmpty()) {
            sb.append(readChar());
        }

        return sb.toString();
    }

    /**
     * Read all bytes from the input stream
     * @return byte array
     * @throws IOException if an I/O error occurs
     */
    public byte[] readAllBytes() throws IOException {
        return readString().getBytes();
    }

    /**
     * Read a byte from the input stream
     * @return byte value
     * @throws IOException if an I/O error occurs
     */
    public char readChar() throws IOException {
        checkEmpty();

        int x = buf;
        if (n == Byte.SIZE) {
            fill();
            return (char) (x & 0xff);
        }

        x <<= (Byte.SIZE - n);
        int oldN = n;
        fill();

        if (!isEmpty()) {
            n = oldN;
            x |= buf >>> n;
        }
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
            x <<= Byte.SIZE;
            x |= readChar();
        }

        return x;
    }

    /**
     * Read r bits from input stream and return as an integer
     * @param r number of bits to read
     * @return integer value
     * @throws IOException if an I/O error occurs
     * @throws IllegalArgumentException if r is not between 1 and 32
     */
    public int readInt(int r) throws IOException {
        if (r < 1 || r > 32) {
            throw new IllegalArgumentException("Illegal value for r = " + r);
        }

        if (r == 32) {
            return readInt();
        }

        int x = 0;
        for (int i = 0; i < r; i++) {
            x <<= 1;
            if (readBit()) {
                x |= 1;
            }
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
            x <<= Byte.SIZE;
            x |= readChar();
        }

        return x;
    }

    /**
     * Read a bit/boolean from the input stream
     * @return bit/boolean value
     * @throws IOException if an I/O error occurs
     */
    public boolean readBit() throws IOException {
        checkEmpty();
        n--;
        boolean bit = ((buf >> n) & 1) == 1;
        if (n == 0) {
            fill();
        }

        return bit;
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