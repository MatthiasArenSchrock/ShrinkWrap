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

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

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
        try (Bin bin = new Bin(fnm);
            Bout bout = new Bout(fnm.substring(0, fnm.lastIndexOf('.')))) {
            decompressLZW(bin, bout);
        }
    }

    /**
     * Decompress an LZW encoded stream
     * @param fnm file name
     * @param tar output stream
     * @throws IOException if an I/O error occurs
     */
    public void deLZW(String fnm, ByteArrayOutputStream tar) throws IOException {
        try (Bin bin = new Bin(fnm);
            Bout bout = new Bout(tar)) {
            decompressLZW(bin, bout);
        }
    }

    /**
     * LZW decompression algorithm
     * @param bin input stream
     * @param bout output stream
     * @throws IOException if an I/O error occurs
     */
    private void decompressLZW(Bin bin, Bout bout) throws IOException {
        String[] st = new String[SchubsL.getL()];
        int i;

        for (i = 0; i < SchubsL.getR(); i++) {
            st[i] = "" + (char) i;
        }
        st[i++] = "";

        if (bin.isEmpty()) {
            return;
        }

        int codeword = bin.readInt(SchubsL.getW());
        String val = st[codeword];

        while (!bin.isEmpty()) {
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

    /**
     * Unarchive an LZW compressed archive
     * @param fnm file name
     * @throws IOException if an I/O error occurs
     */
    public void unarchive(String fnm) throws IOException {
        ByteArrayOutputStream tar = new ByteArrayOutputStream();
        deLZW(fnm, tar);

        try (Bin bin = new Bin(new ByteArrayInputStream(
                Arrays.copyOfRange(tar.toByteArray(), 0, tar.size() - 1)))) {
            extract(bin);
        }
    }

    /**
     * Extract files from an un-/de-compressed archive
     * @param bin input stream
     * @throws IOException if an I/O error occurs
     */
    private void extract(Bin bin) throws IOException {
        while (!bin.isEmpty()) {
            int fnmsz = bin.readInt();
            sep(bin);

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < fnmsz; i++) {
                sb.append(bin.readChar());
            }
            String filename = sb.toString();
            check(filename);
            sep(bin);

            long filesize = bin.readLong();
            sep(bin);

            try (Bout out = new Bout(filename)) {
                for (int i = 0; i < filesize; i++) {
                    out.write(bin.readChar());
                }
            }

            if (!bin.isEmpty()) {
                if (!sep(bin)) break;
            }
        }
    }

    private boolean sep(Bin bin) throws IOException {
        return bin.readChar() == SchubsArc.getSep();
    }

    /**
     * Check if a file exists or is a directory and create parent directories if necessary
     * @param fnm file name
     * @throws IOException if an I/O error occurs
     */
    private void check(String fnm) throws IOException {
        Path p = Path.of(fnm);

        if (Files.exists(p) || Files.isDirectory(p)) {
            throw new RuntimeException("Error unarchiving: " + p +
                    " already exists or is a directory. Please move or rename it and try again");
        }

        if (p.getParent() != null) {
            Files.createDirectories(p.getParent());
        }
    }

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
            unarchive(fnm);
        } else {
            throw new IllegalArgumentException("Invalid file extension");
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            throw new IllegalArgumentException("Usage: java Deschubs <filename>.hh|ll|zl | <GLOB>");
        }

        Deschubs deschubs = new Deschubs();
        for (String fnm : args) {
            deschubs.decompress(fnm);
        }
    }
}