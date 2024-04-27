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

import DataStructures.TST;
import IO.Bin;
import IO.Bout;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.IOException;

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
    private static final int R = 128;
    /**
     * Number of codewords = 2^W
     */
    @Getter
    private static final int L = 256;
    /**
     * Codeword width
     */
    @Getter
    private static final int W = 8;

    /**
     * Compress a file using LZW
     * @param fnm the file to compress
     * @throws IOException if an I/O error occurs
     */
    public void compress(String fnm) throws IOException {
        TST<Integer> st = new TST<>();

        for (int i = 0; i < R; i++) {
            String s = "" + (char) i;
            st.put(s, i);
        }
        int code = R + 1;

        try (Bin bin = new Bin(fnm);
            Bout bout = new Bout(fnm + ".ll")) {
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
    }

    public static void main(String[] args) throws IOException {
//        args = new String[] { "test1.txt", "test2.txt" };
        if (args.length == 0) {
            throw new IllegalArgumentException("Usage: java SchubsL <filename> | <GLOB>");
        }

        SchubsL schubsL = new SchubsL();
        for (String arg : args) {
            schubsL.compress(arg);
        }
    }
}