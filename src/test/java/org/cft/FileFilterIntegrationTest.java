package org.cft;

import org.junit.jupiter.api.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Comparator;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FileFilterIntegrationTest {

    private Path tempDir;

    @BeforeAll
    void setup() throws IOException {
        tempDir = Files.createTempDirectory("filterapptest");
        Path inputFile1 = tempDir.resolve("in1.txt");
        Path inputFile2 = tempDir.resolve("in2.txt");
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
}
