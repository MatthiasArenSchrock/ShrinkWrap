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

@Data
public class TSTNode<T> {
    private char c;
    private TSTNode<T> left, mid, right;
    private T val;
}