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

/**
 * Node object for Trie implementation
 *
 * @author Matthias Schrock
 */
public record TrieNode(char ch, int freq, DataStructures.TrieNode left,
                       DataStructures.TrieNode right) implements Comparable<TrieNode> {
    public boolean isLeaf() {
        return (left == null && right == null);
    }

    /**
     * Compare the frequency of two nodes
     *
     * @param node the object to be compared.
     * @return the value 0 if the argument node has the same frequency as this node;
     */
    public int compareTo(TrieNode node) {
        return this.freq - node.freq;
    }
}