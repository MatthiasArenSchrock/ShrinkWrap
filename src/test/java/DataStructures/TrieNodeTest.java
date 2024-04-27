package DataStructures;

/*
 * Program     : TrieNodeTest
 * Description : Node object for Trie implementation
 * Author      : Matthias Schrock
 * Date        : 04-26-2024
 * Course      : CS375 Software Engineering II
 * Compile     : mvn compile
 * Execute     : mvn test
 */

import org.junit.Test;

import static org.junit.Assert.*;

public class TrieNodeTest {
    @Test
    public void testIsLeaf() {
        TrieNode leafNode = new TrieNode('a', 1, null, null);
        assertTrue(leafNode.isLeaf());

        TrieNode nonLeafNode = new TrieNode('a', 1, new TrieNode('b', 2, null, null), null);
        assertFalse(nonLeafNode.isLeaf());
    }

    @Test
    public void testCompareTo() {
        TrieNode node1 = new TrieNode('a', 1, null, null);
        TrieNode node2 = new TrieNode('b', 2, null, null);
        assertTrue(node1.compareTo(node2) < 0);
        assertTrue(node2.compareTo(node1) > 0);
        assertEquals(0, node1.compareTo(node1));
    }
}