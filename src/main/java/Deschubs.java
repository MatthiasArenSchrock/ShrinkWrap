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

import DataStructures.Node;
import IO.Bin;
import IO.Bout;

import java.io.IOException;

public class Deschubs {
    public void deHuffman(String fnm) throws IOException {
        try (Bin bin = new Bin(fnm);
            Bout bout = new Bout(fnm.substring(0, fnm.lastIndexOf('.')))) {
            Node root = readTrie(bin);

            int length = bin.readInt();
            for (int i = 0; i < length; i++) {
                Node node = root;
                while (!node.isLeaf()) {
                    if (bin.readBit()) {
                        node = node.getRight();
                    } else {
                        node = node.getLeft();
                    }
                }
                bout.write(node.getCh());
            }
            bout.flush();
        }
    }

    private Node readTrie(Bin bin) throws IOException {
        boolean isLeaf = bin.readBit();
        if (isLeaf) {
            return new Node(bin.readChar(), -1, null, null);
        }
        else {
            return new Node('\0', -1, readTrie(bin), readTrie(bin));
        }
    }

    public void deLZW(String fnm) {}

    public void untar(String fnm) {}

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
        args = new String[] { "test1.txt.hh", "test2.txt.hh" };
        if (args.length < 1) {
            throw new RuntimeException("Usage: java Deschubs <filename>.hh|ll|zl");
        }

        Deschubs deschubs = new Deschubs();
        for (String fnm : args) {
            deschubs.decompress(fnm);
        }
    }
}