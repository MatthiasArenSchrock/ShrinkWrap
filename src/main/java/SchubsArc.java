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
import lombok.NoArgsConstructor;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

@NoArgsConstructor
public class SchubsArc {
    /**
     * Compress a set of input files into an LZW table archive
     *
     * @param achv name of the archive
     * @param fnms list of files to be included in the archive
     * @throws IOException if an I/O error occurs
     */
    public void compress(String achv, String[] fnms) throws IOException {
        SchubsL schubsL = new SchubsL();
        try (ByteArrayOutputStream tarCont = tar(fnms)) {
            String test = tarCont.toString();
            schubsL.compress(achv, new ByteArrayInputStream(tarCont.toByteArray()));
        }
    }

    /**
     * Create a tar for compression
     * @param fnms list of files to be included in the archive
     * @return archived output stream
     * @throws IOException if an I/O error occurs
     */
    public ByteArrayOutputStream tar(String[] fnms) throws IOException {
        ByteArrayOutputStream tarCont = new ByteArrayOutputStream();
        try (Bout bout = new Bout(tarCont)) {
            archive(fnms, bout);
        }

        return tarCont;
    }

    /**
     * Create an uncompressed tape archive from a set of input files
     * @param achv name of the archive
     * @param fnms list of files to be included in the archive
     * @throws IOException if an I/O error occurs
     */
    public void tar(String achv, String[] fnms) throws IOException {
        Path par = Path.of(achv).getParent();
        if (par != null) {
            Files.createDirectories(par);
        }

        try (Bout bout = new Bout(achv)) {
            archive(fnms, bout);
        }
    }

    /**
     * Archive a set of files
     * @param fnms list of files to be included in the archive
     * @param bout binary output stream
     * @throws IOException if an I/O error occurs
     */
    private void archive(String[] fnms, Bout bout) throws IOException {
//        char sep = (char) 255;

        for (String fnm : fnms) {
            Path filePath = Path.of(fnm);
            if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
                throw new IOException("Invalid file " + fnm);
            }

            bout.write(fnm.length());
//            bout.write(sep);
            bout.write(fnm);
//            bout.write(sep);
            bout.write(Files.size(filePath));
//            bout.write(sep);

            copy(fnm, bout);

//            if (!fnm.equals(fnms[fnms.length - 1])) {
//                bout.write(sep);
//            }
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
        args = new String[] { "testArchive.zl", "test1.txt", "test2.txt" };
        if (args.length < 2) {
            throw new RuntimeException("Usage: java SchubsArc <archive_name>.zl <[file1 file2 ...]>");
        }

//        if (Files.exists(Path.of(args[0]))) {
//            throw new FileAlreadyExistsException("Archive already exists: " + args[0]);
//        }
        for (String fnm : Arrays.copyOfRange(args, 1, args.length)) {
            if (!Files.exists(Path.of(fnm))) {
                throw new FileNotFoundException("File not found: " + fnm);
            }
            if (Files.isDirectory(Path.of(fnm))) {
                throw new IOException(fnm + " is a directory. Use glob instead: " + fnm +
                        File.separator + "*<extension>");
            }
        }

        new SchubsArc().compress(args[0], Arrays.stream(args).skip(1).toArray(String[]::new));
    }
}