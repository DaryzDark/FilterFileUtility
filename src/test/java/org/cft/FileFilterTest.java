package org.cft;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class FileFilterTest {


    private FileFilter filter;

    @BeforeEach
    void setUp(@TempDir Path tempDir) {
        filter = new FileFilter();
        filter.setOutputPath(tempDir);
        filter.setPrefix("");
        filter.setAppendMode(false);
    }

    @Test
    void whenSingleIntegerLine_thenWritesToIntegersFile(@TempDir Path tempDir) throws IOException {
        Path input = tempDir.resolve("in.txt");
        Files.writeString(input, "42\n");
        filter.processFile(input);
        filter.close();

        Path ints = tempDir.resolve("integers.txt");
        assertThat(Files.exists(ints)).isTrue();
        List<String> lines = Files.readAllLines(ints);
        assertThat(lines).containsExactly("42");

        Statistics stats = filter.getStatistics();
        assertThat(stats.getIntegerCount()).isEqualTo(1);
        assertThat(stats.getFloatCount()).isZero();
        assertThat(stats.getStringCount()).isZero();
    }

    @Test
    void whenMixedLines_thenCategorizesCorrectly(@TempDir Path tempDir) throws IOException {
        Path input = tempDir.resolve("mixed.txt");
        String content = """
            hello
            123
            4.56
            world
            """;
        Files.writeString(input, content);
        filter.processFile(input);
        filter.close();

        assertThat(Files.readAllLines(tempDir.resolve("strings.txt")))
                .containsExactly("hello", "world");
        assertThat(Files.readAllLines(tempDir.resolve("integers.txt")))
                .containsExactly("123");
        assertThat(Files.readAllLines(tempDir.resolve("floats.txt")))
                .containsExactly("4.56");

        Statistics stats = filter.getStatistics();
        assertThat(stats.getStringCount()).isEqualTo(2);
        assertThat(stats.getIntegerCount()).isEqualTo(1);
        assertThat(stats.getFloatCount()).isEqualTo(1);
    }

    @Test
    void whenAppendMode_thenFilesAreAppended(@TempDir Path tempDir) throws IOException {
        filter.setAppendMode(true);
        Path in1 = tempDir.resolve("a.txt");
        Path in2 = tempDir.resolve("b.txt");
        Files.writeString(in1, "1\n");
        Files.writeString(in2, "2\n");
        filter.processFile(in1);
        filter.processFile(in2);
        filter.close();

        List<String> ints = Files.readAllLines(tempDir.resolve("integers.txt"));
        assertThat(ints).containsExactly("1", "2");
    }

    @Test
    void whenNoData_thenNoOutputFilesCreated(@TempDir Path tempDir) throws IOException {
        Path input = tempDir.resolve("empty.txt");
        Files.writeString(input, "\n\n");

        filter.setOutputPath(tempDir);
        filter.processFile(input);
        filter.close();

        assertThat(Files.exists(tempDir.resolve("integers.txt"))).isFalse();
        assertThat(Files.exists(tempDir.resolve("floats.txt"))).isFalse();
        assertThat(Files.exists(tempDir.resolve("strings.txt"))).isFalse();

        Statistics stats = filter.getStatistics();
        assertThat(stats.getIntegerCount()).isZero();
        assertThat(stats.getFloatCount()).isZero();
        assertThat(stats.getStringCount()).isZero();
    }

}