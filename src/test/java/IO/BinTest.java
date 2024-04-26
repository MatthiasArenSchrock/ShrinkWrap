package IO;

/*
 * Program     : BinTest
 * Description : Test read binary data from input stream
 * Author      : Matthias Schrock
 * Date        : 04-26-2024
 * Course      : CS375 Software Engineering II
 * Compile     : mvn compile
 * Execute     : mvn test
 */

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class BinTest {
    private Bin bin;
    private Path dir, testFile;

    @Before
    public void setUp() throws IOException {
        dir = Paths.get("src", "test", "resource");
        Files.createDirectories(dir);

        testFile = dir.resolve("Blee.txt");
        Files.createFile(testFile);

        bin = new Bin(testFile.toString());
    }

    @Test
    public void test() throws IOException {
        assertTrue(Files.exists(testFile));
    }
}