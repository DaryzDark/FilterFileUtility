package org.cft;

import org.junit.jupiter.api.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DataWriterTest {

    private Path tempDir;

    @BeforeAll
    void setup() throws IOException {
        tempDir = Files.createTempDirectory("datawritertest");
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
    void testWriteSingleType() throws IOException {
        DataWriter writer = new DataWriter(tempDir, "pre_", false);
        writer.writeToFile("123", DataType.INTEGER);
        writer.close();

        Path intFile = tempDir.resolve("pre_integers.txt");
        assertTrue(Files.exists(intFile));

        String content = Files.readString(intFile, StandardCharsets.UTF_8).trim();
        assertEquals("123", content);
    }

    @Test
    void testWriteMultipleTypesAppendMode() throws IOException {
        DataWriter writer = new DataWriter(tempDir, "", true);
        writer.writeToFile("foo", DataType.STRING);
        writer.writeToFile("bar", DataType.STRING);
        writer.close();

        Path strFile = tempDir.resolve("strings.txt");
        assertTrue(Files.exists(strFile));

        try (BufferedReader reader = Files.newBufferedReader(strFile, StandardCharsets.UTF_8)) {
            assertEquals("foo", reader.readLine());
            assertEquals("bar", reader.readLine());
            assertNull(reader.readLine());
        }
    }

    @Test
    void testOverwriteMode() throws IOException {
        DataWriter writer1 = new DataWriter(tempDir, "", false);
        writer1.writeToFile("first", DataType.FLOAT);
        writer1.close();

        DataWriter writer2 = new DataWriter(tempDir, "", false);
        writer2.writeToFile("second", DataType.FLOAT);
        writer2.close();

        Path floatFile = tempDir.resolve("floats.txt");
        assertTrue(Files.exists(floatFile));

        String content = Files.readString(floatFile, StandardCharsets.UTF_8).trim();
        assertEquals("second", content);
    }
}
