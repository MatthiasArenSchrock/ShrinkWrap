# ShrinkWrap

## Design
[algorithm theory and trade-offs]

## Tests
Tests comprehensively cover data compression algorithm implementations and their respective packages. The general strategy is to test various file contents and length, compressing and decompressing to ensure that the algorithms are deterministic and compressed data is fully retrievable. Tests also cover edge cases that result from improper or unexpected commands or existing files to prevent overwriting data. Coverage is measured using the Jacoco plugin.
[Running tests and generating reports](#test-instructions)

## Installation
[CLI instructions for installation]

## Test Instructions
maven command: `mvn test`
Jacoco test coverage report: `mvn test jacoco:report`
The Jacoco plugin generates a test coverage report located: src/target/site/jacoco/index.html

## Run Examples
<I>Note: that for any (de)compression algorithm, an exception is thrown if data is about to be overwritten. For example, if an archive that is going to be created already exists, the program will exit without overwriting the existing archive to prevent programatically and permanently deleting data. In these cases where the original file(s) are not important, they must be deleted or renamed beofre running the following commands.</I>

Glob syntax is used to specify sets of filenames with wildcard characters. For example:
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
<br>This will compress the given file <filename> or globbed files, producing Huffman-encoded <filename>.hh for each file in the original file(s)' directory

`java SchubsL <filename> [<filename2>]... | <glob pattern>`
<br>This will compress the given file <filename> or globbed files, producing LZW-encoded <filename>.ll for each file in the original file(s)' directory

To uncompress: `java Deschubs <filename>.hh|ll | <glob pattern>.hh|.ll`

### Archive CLI
Tar CLI: `java SchubsArc <archive-name>[.zl] <filename> [<filename2>]... | <glob pattern>`
<br>This will compress the files specified by name or globbed as an LZW compressed tar

Untar CLI: `java Deschubs <archive-name>.zl`
<br>This will decompress the LZW compressed tar into the original files, placing them in their respective, original directories (sub/nested directories will be created if they have been deleted between archiving and dearchiving)
