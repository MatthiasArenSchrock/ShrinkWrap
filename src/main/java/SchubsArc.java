/*
 * Program     : SchubsArc
 * Description : Create a tar of files using LZW encoding
 * Author      : Matthias Schrock
 * Date        : 04-25-2024
 * Course      : CS375 Software Engineering II
 * Compile     : javac *.java
 * Execute     : java SchubsArc <archive-name>.zl <file1> <[file2] [file3] ...>
 */

import IO.Bin;
import IO.Bout;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * Create compressed and uncompressed tape archives
 *
 * @author Matthias Schrock
 */
@NoArgsConstructor
public class SchubsArc {
    /**
     * Separator character
     * @see SchubsL
     */
    @Getter
    private static final char sep = (char) (SchubsL.getR() - 1);

    /**
     * Compress a set of input files into an LZW table archive
     *
     * @param achv name of the archive
     * @param fnms list of files to be included in the archive
     * @throws IOException if an I/O error occurs
     */
    public void compress(String achv, String... fnms) throws IOException {
        SchubsL schubsL = new SchubsL();

        try (ByteArrayOutputStream tar = new ByteArrayOutputStream()) {
            tar(fnms).writeTo(tar);
            schubsL.compress(achv, new ByteArrayInputStream(tar.toByteArray()));
        }
    }

    /**
     * Create a tar for compression
     *
     * @param fnms list of files to be included in the archive
     * @return archived output stream
     * @throws IOException if an I/O error occurs
     */
    private ByteArrayOutputStream tar(String[] fnms) throws IOException {
        ByteArrayOutputStream tarCont = new ByteArrayOutputStream();
        try (Bout bout = new Bout(tarCont)) {
            for (String fnm : fnms) {
                Path filePath = Path.of(fnm);
                check(filePath);

                bout.write(fnm.length());
                bout.write(sep);
                bout.write(fnm);
                bout.write(sep);
                bout.write(Files.size(filePath));
                bout.write(sep);

                copy(fnm, bout);

                if (!fnm.equals(fnms[fnms.length - 1])) {
                    bout.write(sep);
                }
            }
        }

        return tarCont;
    }

    private void check(Path filePath) throws IOException {
        if (!Files.isRegularFile(filePath)) {
            if (Files.isDirectory(filePath)) {
                throw new IOException(filePath + " is a directory. Use glob instead: " + filePath +
                        File.separator + "*<extension>");
            }
            throw new IOException(filePath + " does not exist or cannot be accessed");
        }
    }

    /**
     * Copy the contents of a file to a binary output stream
     *
     * @param fnm  name of the file to be copied
     * @param bout binary output stream
     * @throws IOException if an I/O error occurs
     */
    private void copy(String fnm, Bout bout) throws IOException {
        try (Bin bin = new Bin(fnm)) {
            while (!bin.isEmpty()) {
                bout.write(bin.readChar());
            }
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            throw new IllegalArgumentException("Usage: java SchubsArc <archive_name>.zl <[file1 file2 ...]>");
        }

        if (!args[0].endsWith(".zl")) {
            args[0] += ".zl";
        }

        if (Files.exists(Path.of(args[0]))) {
            throw new FileAlreadyExistsException("Archive already exists: " + args[0]);
        }

        new SchubsArc().compress(args[0], Arrays.copyOfRange(args, 1, args.length));
    }
}