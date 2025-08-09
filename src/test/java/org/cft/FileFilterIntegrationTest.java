package org.cft;

import org.junit.jupiter.api.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FileFilterIntegrationTest {

    private Path tempDir;
    private Path inputFile1;
    private Path inputFile2;

    @BeforeAll
    void setup() throws IOException {
        tempDir = Files.createTempDirectory("filterapptest");
        inputFile1 = tempDir.resolve("in1.txt");
        inputFile2 = tempDir.resolve("in2.txt");
        Files.writeString(inputFile1, "100\nabc\n3.14\n", StandardCharsets.UTF_8);
        Files.writeString(inputFile2, "-5\nxyz\n2e2\n", StandardCharsets.UTF_8);
    }

    @AfterAll
    void cleanup() throws IOException {
        try (var stream = Files.walk(tempDir)) {
            stream.sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(file -> {
                        if (!file.delete()) {
                            file.deleteOnExit();
                        }
                    });
        }
    }

    @Test
    void testProcessFilesAndStats() throws IOException {
        try (FileFilter filter = new FileFilter(tempDir, "", false)) {
            filter.processFile(inputFile1);
            filter.processFile(inputFile2);

            Statistics stats = filter.getStatistics();
            assertEquals(2, stats.getIntegerCount());
            assertEquals(2, stats.getFloatCount());
            assertEquals(2, stats.getStringCount());
            assertEquals(-5, stats.getIntegerMin());
            assertEquals(100, stats.getIntegerMax());

            assertTrue(Files.exists(tempDir.resolve("integers.txt")));
            assertTrue(Files.exists(tempDir.resolve("floats.txt")));
            assertTrue(Files.exists(tempDir.resolve("strings.txt")));
        }
    }
}
