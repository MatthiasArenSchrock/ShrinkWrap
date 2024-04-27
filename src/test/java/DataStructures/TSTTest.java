package DataStructures;

/*
 * Program     : TSTTest
 * Description : Test ternary Search Trie implementation
 * Author      : Matthias Schrock
 * Date        : 04-26-2024
 * Course      : CS375 Software Engineering II
 * Compile     : mvn compile
 * Execute     : mvn test
 */

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TSTTest {
    private TST<Integer> tst;

    @Before
    public void setUp() {
        tst = new TST<>();
    }

    @Test
    public void testSize() {
        assertEquals(0, tst.size());
        tst.put("blee", 1);
        assertEquals(1, tst.size());
    }

    @Test
    public void testContains() {
        assertFalse(tst.contains("blee"));
        tst.put("blee", 1);
        assertTrue(tst.contains("blee"));
    }

    @Test
    public void testGet() {
        assertNull(tst.get("blee"));
        tst.put("blee", 1);
        assertEquals(Integer.valueOf(1), tst.get("blee"));
    }

    @Test
    public void testPut() {
        tst.put("blee", 1);
        assertTrue(tst.contains("blee"));
        assertEquals(Integer.valueOf(1), tst.get("blee"));
        tst.put("blee", 2);
        assertEquals(Integer.valueOf(2), tst.get("blee"));
    }

    @Test
    public void testLongestPrefix() {
        assertEquals(tst.longestPrefix("blee"), "");
        tst.put("bl", 129);
        tst.put("le", 130);
        tst.put("ee", 131);
        assertEquals("bl", tst.longestPrefix("blee"));
        assertEquals("bl", tst.longestPrefix("blah"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutWithNullKey() {
        tst.put(null, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetWithNullKey() {
        tst.get(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testContainsWithNullKey() {
        tst.contains(null);
    }

    @Test
    public void testLongestPrefixWithNullString() {
        assertNull(tst.longestPrefix(null));
    }
}