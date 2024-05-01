# ShrinkWrap

## Design
[algorithm theory and trade-offs]

## Tests
[information about the tests that prove and illustrate everything]
Tests comprehensively cover data compression algorithm implementations and their respective packages. The general strategy is to test various file contents and length, compressing and decompressing to ensure that the algorithms are deterministic and compressed data is fully retrievable. Tests also cover edge cases that result from improper or unexpected commands. Coverage is measured using the Jacoco plugin.
[Running tests and generating reports](#test-instructions)

## Installation
[CLI instructions for installation]

## Test Instructions
[instructions on how to run the tests]
maven command: mvn test
Jacoco test coverage report: mvn test jacoco:report
The Jacoco plugin generates a test coverage report located: src/target/site/jacoco/index.html

## Run Examples
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
java SchubsH <filename> [<filename2>]... | <glob pattern>
this will compress the given file <filename> or globbed files, producing Huffman-encoded <filename>.hh for each file in the original file(s)' directory

java SchubsL <filename> [<filename2>]... | <glob pattern>
this will compress the given file <filename> or globbed files, producing LZW-encoded <filename>.ll for each file in the original file(s)' directory

To uncompress: java Deschubs <filename>.hh|ll | <glob pattern>*.hh|*.ll

### Archive CLI
Tar CLI: java SchubsArc <archive-name> [<archive2-name>]... | <glob pattern>
Untar CLI. java Deschubs <archive>.zl|.zh | <glob pattern>*.zl|*.zh