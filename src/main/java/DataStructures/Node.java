package DataStructures;

/*
 * Program     : Node
 * Description : Node object for Trie implementation
 * Author      : Matthias Schrock
 * Date        : 04-25-2024
 * Course      : CS375 Software Engineering II
 * Compile     : javac Node.java
 * Execute     : N/A
 */

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Node implements Comparable<Node> {
    private final char ch;
    private final int freq;
    private final Node left, right;

    public boolean isLeaf() {
        return (left == null && right == null);
    }

    public int compareTo(Node node) {
        return this.freq - node.freq;
    }
}