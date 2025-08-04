package org.cft;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.*;

class FilterAppTest {

    @Test
    void whenNoArgs_thenExitCodeIsOne() {
        int code = new CommandLine(new FilterApp()).execute();
        assertThat(code).isEqualTo(1);
    }

    @Test
    void endToEnd_exampleUsage_shortStats(@TempDir Path dir) throws IOException {
        Path in = dir.resolve("in.txt");
        Files.writeString(in, "foo\n100\n3.14\n");
        int code = new CommandLine(new FilterApp())
                .setOut(new PrintWriter(System.out, true))
                .setErr(new PrintWriter(System.err, true))
                .execute(in.toString(), "-o", dir.toString(), "-s");
        assertThat(code).isZero();

        // Check files
        assertThat(Files.readAllLines(dir.resolve("strings.txt"))).containsExactly("foo");
        assertThat(Files.readAllLines(dir.resolve("integers.txt"))).containsExactly("100");
        assertThat(Files.readAllLines(dir.resolve("floats.txt"))).containsExactly("3.14");
    }

    @Test
    void endToEnd_exampleUsage_fullStats(@TempDir Path dir) throws IOException {
        Path in = dir.resolve("in2.txt");
        Files.writeString(in, "a\nb\n10\n20\n2.5\n5.5\n");
        int code = new CommandLine(new FilterApp())
                .setOut(new PrintWriter(System.out, true))
                .setErr(new PrintWriter(System.err, true))
                .execute(in.toString(), "-o", dir.toString(), "-p", "res_", "-f");
        assertThat(code).isZero();

        assertThat(Files.readAllLines(dir.resolve("res_strings.txt"))).containsExactly("a", "b");
        assertThat(Files.readAllLines(dir.resolve("res_integers.txt"))).containsExactly("10", "20");
        assertThat(Files.readAllLines(dir.resolve("res_floats.txt"))).containsExactly("2.5", "5.5");
    }
}
