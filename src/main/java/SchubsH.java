/*
 * Program     : SchubsH
 * Description : Compress one to many files using Huffman encoding
 * Author      : Matthias Schrock
 * Date        : 04-25-2024
 * Course      : CS375 Software Engineering II
 * Compile     : javac *.java
 * Execute     : Singe File CLI: java SchubsH <filename>
    *            GLOB CLI: java SchubsH <GLOB>
 */

public class SchubsH {
    private static final int R = 256;

    public static void main(String[] args) {
        validateInput(args);
    }

    private static void validateInput(String[] args) {
        if (args.length < 1) {
            throw new RuntimeException("Usage: java SchubsH <filename> | <GLOB>");
        }
    }
}
