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

import DataStructures.TrieNode;
import IO.Bin;
import IO.Bout;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.util.PriorityQueue;

/**
 * Compress one to many files using Huffman encoding
 * @author Matthias Schrock
 */
@NoArgsConstructor
public class SchubsH {
    private static final int R = 256;

    /**
     * Compress a file
     * @param fnm file name
     * @throws IOException if an I/O error occurs
     */
    public void compress(String fnm) throws IOException {
        try (Bin bin = new Bin(fnm);
             Bout bout = new Bout(fnm + ".hh")) {
            byte[] input = bin.readAllBytes();

            int[] freq = new int[R];
            for (byte value : input) {
                freq[value]++;
            }

            TrieNode root = buildTrie(freq);

            String[] ct = new String[R];
            buildCode(ct, root, "");

            writeTrie(root, bout);
            bout.write(input.length);

            for (byte b : input) {
                String code = ct[b];
                for (int j = 0; j < code.length(); j++) {
                    bout.writeBit(code.charAt(j) == '1');
                }
            }
        }
    }

    /**
     * Build a trie from the frequency array
     * @param freq frequency array
     * @return root of the trie
     */
    private TrieNode buildTrie(int[] freq) {
        PriorityQueue<TrieNode> pq = new PriorityQueue<>();
        for (char c = 0; c < R; c++) {
            if (freq[c] > 0) {
                pq.add(new TrieNode(c, freq[c], null, null));
            }
        }

        while (pq.size() > 1) {
            TrieNode left = pq.poll();
            TrieNode right = pq.poll();
            TrieNode parent = new TrieNode('\0', left.freq() + right.freq(), left, right);
            pq.add(parent);
        }

        return pq.poll();
    }

    /**
     * Write the trie to the output stream
     * @param n node of the trie
     * @param bout output stream
     * @throws IOException if an I/O error occurs
     */
    private void writeTrie(TrieNode n, Bout bout) throws IOException {
        if (n.isLeaf()) {
            bout.writeBit(true);
            bout.write(n.ch());
            return;
        }
        bout.writeBit(false);

        writeTrie(n.left(), bout);
        writeTrie(n.right(), bout);
    }

    /**
     * Build the code table
     * @param ct code table
     * @param n node of the trie
     * @param s bitstring
     */
    private void buildCode(String[] ct, TrieNode n, String s) {
        if (!n.isLeaf()) {
            buildCode(ct, n.left(), s + '0');
            buildCode(ct, n.right(), s + '1');
        } else {
            ct[n.ch()] = s;
        }
    }

    public static void main(String[] args) throws IOException {
        args = new String[] { "test1.txt", "test2.txt" }; // For testing
        if (args.length < 1) {
            throw new IllegalArgumentException("Usage: java SchubsH <filename> | <GLOB>");
        }

        SchubsH sh = new SchubsH();
        for (String arg : args) {
            sh.compress(arg);
        }
    }
}