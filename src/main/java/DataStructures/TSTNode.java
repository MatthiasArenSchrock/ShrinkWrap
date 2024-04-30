package DataStructures;

/*
 * Program     : TSTNode
 * Description : Ternary Search Trie Node for TST implementation
 * Author      : Matthias Schrock
 * Date        : 04-25-2024
 * Course      : CS375 Software Engineering II
 * Compile     : javac TSTNode.java
 * Execute     : N/A
 */

import lombok.Data;

/**
 * Ternary Search Trie Node for TST implementation
 *
 * @param <T> type of value stored in the node
 * @author Matthias Schrock
 * @see DataStructures.TST
 */
@Data
public class TSTNode<T> {
    /**
     * character stored in the node
     */
    private char c;
    private TSTNode<T> left, mid, right;

    /**
     * value stored in the node
     */
    private T val;
}