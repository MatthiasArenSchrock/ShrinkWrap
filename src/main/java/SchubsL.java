/*
 * Program     : SchubsL
 * Description : Compress one to many files using LZW encoding
 * Author      : Matthias Schrock
 * Date        : 04-25-2024
 * Course      : CS375 Software Engineering II
 * Compile     : javac *.java
 * Execute     : Singe File CLI: java SchubsL <filename>
 *               GLOB CLI: java SchubsL <GLOB>
 */

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import DataStructures.TST;
import IO.Bin;
import IO.Bout;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Compress one to many files using LZW encoding
 * @author Matthias Schrock
 */
@NoArgsConstructor
public class SchubsL {
    /**
     * Number of input chars
     */
    @Getter
    private static final int R = 256;
    /**
     * Number of codewords = 2^W
     */
    @Getter
    private static final int L = 4096;
    /**
     * Codeword width
     */
    @Getter
    private static final int W = 12;

    /**
     * Compress a file using LZW
     * @param fnm the file to compress
     * @throws IOException if an I/O error occurs
     */
    public void compress(String fnm) throws IOException {
        try (Bin bin = new Bin(fnm);
            Bout bout = new Bout(fnm + ".ll")) {
            LZWAlgorithm(bin, bout);
        }
    }

    /**
     * Compress an input stream using LZW
     * @param fnm the output filename
     * @param is the input stream
     * @throws IOException if an I/O error occurs
     */
    public void compress(String fnm, InputStream is) throws IOException {
        try (Bin bin = new Bin(is);
             Bout bout = new Bout(fnm)) {
            LZWAlgorithm(bin, bout);
        }
    }

    /**
     * LZW compression algorithm
     * @param bin the input stream
     * @param bout the output stream
     * @throws IOException if an I/O error occurs
     */
    private void LZWAlgorithm(Bin bin, Bout bout) throws IOException {
        TST<Integer> st = new TST<>();
        for (int i = 0; i < R; i++) {
            String s = "" + (char) i;
            st.put(s, i);
        }

        int code = R + 1;
        String input = bin.readString();

        while (!input.isEmpty()) {
            String s = st.longestPrefix(input);
            bout.write(st.get(s), W);
            int t = s.length();
            if (t < input.length() && code < L) {
                st.put(input.substring(0, t + 1), code++);
            }
            input = input.substring(t);
        }

        bout.write(R, W);
    }

    public static void main(String[] args) {
        try {
            validateArgs(args);

            SchubsL schubsL = new SchubsL();
            for (String arg : args) {
                schubsL.compress(arg);
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private static void validateArgs(String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("Usage: java SchubsL <filename> | <GLOB>");
        }
        if (Files.exists(Path.of(args[0] + ".ll"))) {
            throw new IllegalArgumentException(args[0] + ".ll already exists");
        }
        if (Files.isDirectory(Path.of(args[0]))) {
            throw new IllegalArgumentException("Input file is a directory. Use Glob instead: " +
                    "java SchubsL " + args[0] + File.separator + "<glob>");
        }
    }
}