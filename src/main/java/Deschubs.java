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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import DataStructures.TrieNode;
import IO.Bin;
import IO.Bout;
import lombok.NoArgsConstructor;

/**
 * Decompression class for LZW and Huffman encoded files as well as LZW
 * compressed archives
 *
 * @author Matthias Schrock
 */
@NoArgsConstructor
public class Deschubs {
    /**
     * Decompress a Huffman encoded file
     * 
     * @param fnm file name
     * @param stdOpen open option. By default, option is set to CREATE
     * @throws IOException if an I/O error occurs
     */
    public void deHuffman(String fnm, StandardOpenOption... stdOpen) throws IOException {
        try (Bin bin = new Bin(fnm);
                Bout bout = new Bout(fnm.substring(0, fnm.lastIndexOf('.')), stdOpen)) {
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
     * 
     * @param bin input stream
     * @return root of the trie
     * @throws IOException if an I/O error occurs
     */
    private TrieNode readTrie(Bin bin) throws IOException {
        boolean isLeaf = bin.readBit();
        if (isLeaf) {
            return new TrieNode(bin.readChar(), -1, null, null);
        } else {
            return new TrieNode('\0', -1, readTrie(bin), readTrie(bin));
        }
    }

    /**
     * Decompress an LZW encoded file
     * 
     * @param fnm file name
     * @param stdOpen open option. By default, option is set to CREATE
     * @throws IOException if an I/O error occurs
     */
    public void deLZW(String fnm, StandardOpenOption... stdOpen) throws IOException {
        try (Bin bin = new Bin(fnm);
                Bout bout = new Bout(fnm.substring(0, fnm.lastIndexOf('.')), stdOpen)) {
            decompressLZW(bin, bout);
        }
    }

    /**
     * Decompress an LZW encoded stream
     * 
     * @param fnm file name
     * @param tar output stream. By default, option is set to CREATE
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
     * 
     * @param bin  input stream
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

        int codeword = bin.readInt(SchubsL.getW());
        String val = st[codeword];

        while (!bin.isEmpty() && codeword != SchubsL.getR()) {
            validate(val);
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

    private void validate(String val) {
        if (val == null) {
            throw new RuntimeException("Invalid LZW compressed file");
        }
    }

    /**
     * Unarchive an LZW compressed archive
     * 
     * @param fnm file name
     * @param stdOpen open option. By default, option is set to CREATE
     * @throws IOException if an I/O error occurs
     */
    public void unarchive(String fnm, StandardOpenOption... stdOpen) throws IOException {
        ByteArrayOutputStream tar = new ByteArrayOutputStream();
        deLZW(fnm, tar);

        try (Bin bin = new Bin(new ByteArrayInputStream(tar.toByteArray()))) {
            extract(bin, stdOpen);
        }
    }

    /**
     * Extract files from an un-/de-compressed archive
     * 
     * @param bin input stream
     * @param stdOpen open option. By default, option is set to CREATE
     * @throws IOException if an I/O error occurs
     */
    private void extract(Bin bin, StandardOpenOption... stdOpen) throws IOException {
        while (!bin.isEmpty()) {
            int fnmsz = bin.readInt();
            bin.readChar();

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < fnmsz; i++) {
                sb.append(bin.readChar());
            }
            String filename = sb.toString();
            check(filename);
            bin.readChar();

            long filesize = bin.readLong();
            bin.readChar();

            try (Bout out = new Bout(filename, stdOpen)) {
                for (int i = 0; i < filesize; i++) {
                    out.write(bin.readChar());
                }
            }

            // Ignore EOF
            if (!bin.isEmpty()) {
                if (bin.readChar() != SchubsArc.getSep())
                    break;
            }
        }
    }

    /**
     * Check if a file exists or is a directory and create parent directories if
     * necessary
     * 
     * @param fnm file name
     * @throws IOException if an I/O error occurs
     */
    private void check(String fnm) throws IOException {
        Path par = Path.of(fnm).getParent();
        if (par != null) {
            Files.createDirectories(par);
        }
    }

    /**
     * Decompress a file using extension as clue for decompressor
     * 
     * @param fnm file name
     * @throws IOException if an I/O error occurs
     */
    private void decompress(String fnm) throws IOException {
        if (fnm.endsWith(".hh")) {
            deHuffman(fnm, StandardOpenOption.CREATE_NEW);
        } else if (fnm.endsWith(".ll")) {
            deLZW(fnm, StandardOpenOption.CREATE_NEW);
        } else if (fnm.endsWith(".zl")) {
            unarchive(fnm, StandardOpenOption.CREATE_NEW);
        } else {
            throw new IllegalArgumentException("Invalid file extension");
        }
    }

    public static void main(String[] args) {
        try {
            validateArgs(args);

            Deschubs deschubs = new Deschubs();
            for (String fnm : args) {
                deschubs.decompress(fnm);
            }
        } catch (IllegalArgumentException | IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private static void validateArgs(String[] args) {
        if (args.length < 1) {
            throw new IllegalArgumentException("Usage: java Deschubs <filename>.hh|ll|zl | <GLOB>");
        }
    }
}