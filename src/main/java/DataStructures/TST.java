package DataStructures;

/*
 * Program     : TST
 * Description : Ternary Search Trie implementation
 * Author      : Matthias Schrock
 * Date        : 04-25-2024
 * Course      : CS375 Software Engineering II
 * Compile     : javac TST.java
 * Execute     : N/A
 */

/**
 * Ternary Search Trie implementation
 *
 * @param <T> the type of the value to store
 * @author Matthias Schrock
 */
public class TST<T> {
    private int n;
    private TSTNode<T> root;

    /**
     * @return the number of key/value pairs in the trie
     */
    public int size() {
        return n;
    }

    /**
     * Check if the trie contains a key
     * 
     * @param key the key to search for
     * @return true if the key is in the trie, false otherwise
     */
    public boolean contains(String key) {
        return get(key) != null;
    }

    /**
     * Get the value associated with a key
     * 
     * @param key the key to search for
     * @return the value associated with the key
     */
    public T get(String key) {
        check(key);

        TSTNode<T> n = get(root, key, 0);
        if (n == null) {
            return null;
        }
        return n.getVal();
    }

    /**
     * Return sub-trie from a given key
     * 
     * @param node the node to start from
     * @param key  the key to search for
     * @param d    the depth of the key
     * @return the sub-trie from the given key
     */
    private TSTNode<T> get(TSTNode<T> node, String key, int d) {
        check(key);
        if (node == null) {
            return null;
        }

        char c = key.charAt(d);
        if (c < node.getC()) {
            return get(node.getLeft(), key, d);
        } else if (c > node.getC()) {
            return get(node.getRight(), key, d);
        } else if (d < key.length() - 1) {
            return get(node.getMid(), key, d + 1);
        } else {
            return node;
        }
    }

    /**
     * Insert string into the symbol table
     * 
     * @param key the key to insert
     * @param val the value to associate with the key
     */
    public void put(String key, T val) {
        if (!contains(key)) {
            n++;
        }
        root = put(root, key, val, 0);
    }

    /**
     * Insert a key/value pair into the trie
     * 
     * @param node the node to start from
     * @param key  the key to insert
     * @param val  the value to associate with the key
     * @param d    the depth of the key
     * @return the node with the key/value pair inserted
     */
    private TSTNode<T> put(TSTNode<T> node, String key, T val, int d) {
        char c = key.charAt(d);
        if (node == null) {
            node = new TSTNode<>();
            node.setC(c);
        }

        if (c < node.getC()) {
            node.setLeft(put(node.getLeft(), key, val, d));
        } else if (c > node.getC()) {
            node.setRight(put(node.getRight(), key, val, d));
        } else if (d < key.length() - 1) {
            node.setMid(put(node.getMid(), key, val, d + 1));
        } else {
            node.setVal(val);
        }

        return node;
    }

    /**
     * Find and return the longest prefix of a string in the trie
     * 
     * @param s the string to search for
     * @return the longest prefix of the string in the trie
     */
    public String longestPrefix(String s) {
        if (s == null || s.isEmpty()) {
            return null;
        }

        int len = 0;
        TSTNode<T> node = root;
        int i = 0;
        while (node != null && i < s.length()) {
            char c = s.charAt(i);
            if (c < node.getC()) {
                node = node.getLeft();
            } else if (c > node.getC()) {
                node = node.getRight();
            } else {
                i++;
                if (node.getVal() != null) {
                    len = i;
                }
                node = node.getMid();
            }
        }

        return s.substring(0, len);
    }

    private void check(String key) {
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("illegal key");
        }
    }
}