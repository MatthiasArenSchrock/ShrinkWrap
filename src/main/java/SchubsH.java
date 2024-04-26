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

import DataStructures.Node;
import IO.Bin;
import IO.Bout;

import java.io.IOException;
import java.util.PriorityQueue;

public class SchubsH {
    private static final int R = 256;

    /**
     * Compress a file
     * @param fnm file name
     * @throws IOException if an I/O error occurs
     */
    public void compress(String fnm) throws IOException {


        try (Bin bin = new Bin(fnm);
             Bout bout = new Bout(fnm.substring(0, fnm.lastIndexOf('.')) + ".hh")) {
            byte[] input = bin.readAllBytes();

            int[] freq = new int[R];
            for (int i = 0; i < input.length; i++) {
                freq[input[i]]++;
            }

            Node root = buildTrie(freq);

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
    private Node buildTrie(int[] freq) {
        PriorityQueue<Node> pq = new PriorityQueue<>();
        for (char c = 0; c < R; c++) {
            if (freq[c] > 0) {
                pq.add(new Node(c, freq[c], null, null));
            }
        }

        while (pq.size() > 1) {
            Node left = pq.poll();
            Node right = pq.poll();
            Node parent = new Node('\0', left.getFreq() + right.getFreq(), left, right);
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
    private void writeTrie(Node n, Bout bout) throws IOException {
        if (n.isLeaf()) {
            bout.writeBit(true);
            bout.write(n.getCh());
            return;
        }
        bout.writeBit(false);

        writeTrie(n.getLeft(), bout);
        writeTrie(n.getRight(), bout);
    }

    /**
     * Build the code table
     * @param ct code table
     * @param n node of the trie
     * @param s bitstring
     */
    private void buildCode(String[] ct, Node n, String s) {
        if (!n.isLeaf()) {
            buildCode(ct, n.getLeft(), s + '0');
            buildCode(ct, n.getRight(), s + '1');
        } else {
            ct[n.getCh()] = s;
        }
    }

    /**
     * Compress multiple files for glob
     * @param fnms file names
     * @throws IOException if an I/O error occurs
     */
    private void compressFiles(String[] fnms) throws IOException {
        for (String fnm : fnms) {
            compress(fnm);
        }
    }

    public static void main(String[] args) {
        args = new String[] { "test1.txt", "test2.txt" }; // For testing
        validateInput(args);

        try {
            SchubsH sh = new SchubsH();
            sh.compressFiles(args);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void validateInput(String[] args) {
        if (args.length < 1) {
            throw new RuntimeException("Usage: java SchubsH <filename> | <GLOB>");
        }
    }
}