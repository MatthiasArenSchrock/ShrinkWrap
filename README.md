# ShrinkWrap - CS375 - Software Engineering II

## Design

### Huffman (SchubsH)
The algorithm uses a frequency-sorted binary tree to create an optimal prefix code. This prefix code assigns shorter codes to more frequently used characters and longer codes to less frequently used characters, which results in efficient compression for data where certain characters occur more often.

In the SchubsH implementation, the Huffman algorithm is applied to compress one or many files. The frequency of each byte in the file(s) is calculated and used to build a Huffman Tree. Each leaf node of the tree represents a byte and its frequency. The tree is then traversed to generate a unique binary code for each byte. These codes replace the original bytes in the file, resulting in a compressed version of the file.

The SchubsH class provides a compress method that takes a filename as input and generates a compressed file with the extension .hh. The original file is not modified during this process.  
#### Trade-offs
While Huffman encoding is efficient and effective for certain types of data, it does have some trade-offs:  
- Variable-length codes: Huffman encoding generates variable-length codes for different characters. While this is beneficial for compression, it can make the encoding and decoding processes more complex compared to fixed-length encoding methods.  
- Dependent on data distribution: The efficiency of Huffman encoding is highly dependent on the frequency distribution of characters in the data. If all characters occur with equal frequency, Huffman encoding does not provide any compression.  
- Overhead of storing the Huffman Tree: To decode the compressed data, the Huffman Tree (or some representation of it) must be stored along with the compressed data. This adds some overhead to the compressed file, decreasing compression ratio.  
- Not suitable for small files: The overhead of storing the Huffman Tree can outweigh the benefits of compression for small files. Huffman encoding is more suited to larger files with repetitive data.

### LZW (SchubsL)
LZW is a lossless, dictionary-based algorithm that creates a dictionary of phrases it has encountered and then outputs the dictionary index instead of the phrase.

In the SchubsL implementation, the LZW algorithm is applied to compress one or many files. The algorithm reads input characters, groups them into phrases, and encodes the phrases as fixed-length code words. The dictionary starts with individual characters and grows to include more complex phrases. 256 ASCII characters are used as the initial dictionary, with a maximum of 4096 codewords. A bit width of 12 is used to prevent overflow and issues with Java's use of two's complement signed values.

The SchubsL class provides a compress method that takes a filename as input and generates a compressed file with the extension .ll. The original file is not modified during this process.  
#### Trade-offs
The LZW algorithm has several trade-offs:  
- Memory usage: The LZW algorithm uses a dictionary that grows with the input, which can lead to high memory usage for large inputs or long-running compressions.  
- Compression ratio: The LZW algorithm can achieve high compression ratios on certain types of data, but it may not compress small or non-repetitive data well.  
- Complexity: The LZW algorithm is more complex than some other compression algorithms, such as Huffman coding. This can make it slower and more resource-intensive to encode and decode data.

### Archive - LZW Tape ARchive (SchubsArc)
The archive class uses the LZW algorithm to compress a tape archive. The tape archive (created in memory) is an initial collection of files and relevant data that is then compressed using LZW. The idea is to provide the longest possible string to LZW to maximize compression efficiency and effectiveness, allowing for more codewords to be generated and used.

LZW also has the advantage over huffman of not having to embed a trie at the head of the file. This is because the dictionary is built dynamically during compression and decompression, allowing for the dictionary to be built and used in a single pass. Ideally, the compressed archive will be smaller than the sum of the original files' sizes, but this is not necessarily guaranteed in some edge cases.

#### Trade-offs
The LZW algorithm used in SchubsArc has several trade-offs:  
- Memory usage: The LZW algorithm uses a dictionary that grows with the input, which can lead to high memory usage for large inputs or long-running compressions.  
- Compression ratio: The LZW algorithm can achieve high compression ratios on certain types of data, but it may not compress small or non-repetitive data well.  
- Complexity: The LZW algorithm is more complex than some other compression algorithms, such as Huffman coding. This can make it slower and more resource-intensive to encode and decode data.

## Tests
Tests comprehensively cover data compression algorithm implementations and the IO and data structure classes they use. The general strategy is to test various file contents and length, compressing and decompressing to ensure that the algorithms are deterministic and compressed data is fully retrievable. Tests also cover edge cases that result from improper or unexpected commands or existing files to prevent overwriting data. Coverage is measured using the Jacoco plugin.
See [Test Instructions](#test-instructions) for details on running tests and generating coverage reports.

## Installation
### Prerequisites
<pre>
.
├── pom.xml
├── src
│   ├── main...
    └── test...
└── target
    ├── classes
    │    ├── DataStructures
    │    │   ├── TST.class
    │    │   ├── TSTNode.class
    │    │   └── TrieNode.class
    │    ├── Deschubs.class
    │    ├── IO
    │    │   ├── Bin.class
    │    │   └── Bout.class
    │    ├── SchubsArc.class
    │    ├── SchubsH.class
    │    └── SchubsL.class
    ...
</pre>
### Run
Assuming Java JDK 20+ is installed and the project has been built using `mvn compile` or all individual class files have been compiled using `javac` and are structured properly:
<br>`java -cp [<relative path to project directory>/]target/classes SchubsH | SchubsL | SchubsArc | Deschubs <args>`
<br>[See Examples](#run-examples)
<br><I>Note: the class path is assumed in run examples</I>

## Test Instructions
maven command: `mvn test`
<br>Jacoco test coverage report: `mvn test jacoco:report`
<br>The Jacoco plugin generates a test coverage report located: src/target/site/jacoco/index.html

## Run Examples
<I>Note: that for any (de)compression algorithm, an exception is thrown if data is about to be overwritten. For example, if an archive that is going to be created already exists, the program will exit without overwriting the existing archive to prevent programatically and permanently deleting data. In these cases where the original file(s) are not important, they must be deleted or renamed before running the following commands.</I>

Glob syntax is used to specify sets of filenames with wildcard characters. For goals that support multiple files, glob patterns are used rather than specifying a directory. For example:
- `*` matches any sequence of non-separator characters
- `?` matches any single non-separator character
- `[...]` matches a range of characters

Here are some examples of glob patterns and their meanings:

| Glob Pattern | Meaning |
| ------------ | ------- |
| `*.txt`      | Matches any file with a `.txt` extension |
| `?lee.txt`   | Matches any 5-character filename ending with `lee.txt` (e.g., `blee.txt` and `Blee.txt`) |
| `[abc]*.txt` | Matches any `.txt` file starting with `a`, `b`, or `c` |

### File Compression CLI
`java SchubsH <filename> [<filename2>]... | <glob pattern>`
<br>This will compress the given file <filename> or globbed files, producing Huffman-encoded <filename>.hh for each file in the input files

`java SchubsL <filename> [<filename2>]... | <glob pattern>`
<br>This will compress the given file <filename> or globbed files, producing LZW-encoded <filename>.ll for each file in the input files

To uncompress: `java Deschubs <filename>.hh|ll | <glob pattern>.hh|.ll`

### Archive CLI
Tar CLI: `java SchubsArc <archive-name>[.zl] <filename> [<filename2>]... | <glob pattern>`
<br>This will compress the files specified by name or globbed as an LZW compressed tar

Untar CLI: `java Deschubs <archive-name>.zl`
<br>This will decompress the LZW compressed tar into the original files, placing them in their respective, original directories (sub/nested directories will be created if they have been deleted between archiving and dearchiving)
