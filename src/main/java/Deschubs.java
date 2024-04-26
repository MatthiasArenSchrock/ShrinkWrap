/*
 * Program     : Deschubs
 * Description : Decompress one to many files or an archive using the extension as clue for decompressor file,
 *               LZW (.ll) or Huffman (.hh)
 * Author      : Matthias Schrock
 * Date        : 04-25-2024
 * Course      : CS375 Software Engineering II
 * Compile     : javac *.java
 * Execute     : java Deschubs <filename>.hh|ll|zl
 */

import DataStructures.TrieNode;
import IO.Bin;
import IO.Bout;
import lombok.NoArgsConstructor;

import java.io.IOException;

/**
 * Decompression class for LZW and Huffman encoded files as well as LZW compressed archives
 * @author Matthias Schrock
 */
@NoArgsConstructor
public class Deschubs {
    /**
     * Decompress a Huffman encoded file
     * @param fnm file name
     * @throws IOException if an I/O error occurs
     */
    public void deHuffman(String fnm) throws IOException {
        try (Bin bin = new Bin(fnm);
            Bout bout = new Bout(fnm.substring(0, fnm.lastIndexOf('.')))) {
            TrieNode root = readTrie(bin);

            int length = bin.readInt();
            for (int i = 0; i < length; i++) {
                TrieNode node = root;
                while (!node.isLeaf()) {
                    if (bin.readBit()) {
                        node = node.right();
                    } else {
                        node = node.left();
                    }
                }
                bout.write(node.ch());
            }
            bout.flush();
        }
    }

    /**
     * Read a trie from the input stream
     * @param bin input stream
     * @return root of the trie
     * @throws IOException if an I/O error occurs
     */
    private TrieNode readTrie(Bin bin) throws IOException {
        boolean isLeaf = bin.readBit();
        if (isLeaf) {
            return new TrieNode(bin.readChar(), -1, null, null);
        }
        else {
            return new TrieNode('\0', -1, readTrie(bin), readTrie(bin));
        }
    }

    /**
     * Decompress an LZW encoded file
     * @param fnm file name
     * @throws IOException if an I/O error occurs
     */
    public void deLZW(String fnm) throws IOException {
        String[] st = new String[SchubsL.getL()];
        int i;

        for (i = 0; i < SchubsL.getR(); i++) {
            st[i] = "" + (char) i;
        }
        st[i++] = "";

        try (Bin bin = new Bin(fnm);
            Bout bout = new Bout(fnm.substring(0, fnm.lastIndexOf('.')))) {
            if (bin.isEmpty()) {
                return;
            }

            int codeword = bin.readInt(SchubsL.getW());
            String val = st[codeword];

            while (true) {
                bout.write(val);

                codeword = bin.readInt(SchubsL.getW());
                if (codeword == SchubsL.getR()) {
                    break;
                }

                String s = st[codeword];
                if (i == codeword) {
                    s = val + val.charAt(0);
                }
                if (i < SchubsL.getL()) {
                    st[i++] = val + s.charAt(0);
                }

                val = s;
            }
        }
    }

    public void untar(String fnm) {}

    /**
     * Decompress a file using extension as clue for decompressor
     * @param fnm file name
     * @throws IOException if an I/O error occurs
     */
    private void decompress(String fnm) throws IOException {
        if (fnm.endsWith(".hh")) {
            deHuffman(fnm);
        } else if (fnm.endsWith(".ll")) {
            deLZW(fnm);
        } else if (fnm.endsWith(".zl")) {
            untar(fnm);
        } else {
            throw new RuntimeException("Invalid file extension");
        }
    }

    public static void main(String[] args) throws IOException {
//        args = new String[] { "test1.txt.ll", "test2.txt.ll" }; // for testing
        args = new String[] { "test1.txt.hh", "test2.txt.hh" }; // for testing
        if (args.length < 1) {
            throw new RuntimeException("Usage: java Deschubs <filename>.hh|ll|zl | <GLOB>");
        }

        Deschubs deschubs = new Deschubs();
        for (String fnm : args) {
            deschubs.decompress(fnm);
        }
    }
}