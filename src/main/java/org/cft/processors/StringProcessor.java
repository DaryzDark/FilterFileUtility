package org.cft.processors;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

public class StringProcessor implements LineProcessor {
    private final BufferedWriter writer;
    private long count = 0;
    private int minLength = Integer.MAX_VALUE;
    private int maxLength = 0;

    public StringProcessor(Path outputPath, String prefix, boolean appendMode) {
        try {
            OpenOption[] opts = appendMode
                    ? new OpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.APPEND}
                    : new OpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING};
            Path outputFile = outputPath.resolve(prefix + "strings.txt");
            this.writer = Files.newBufferedWriter(outputFile, StandardCharsets.UTF_8, opts);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to create writer for strings file", e);
        }
    }

    @Override
    public boolean canProcess(String line) {
        return true;
    }

    @Override
    public void process(String line) {
        try {
            int len = line.length();
            if (len < minLength) minLength = len;
            if (len > maxLength) maxLength = len;
            count++;

            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            throw new UncheckedIOException("Failed writing to strings file", e);
        }
    }

    @Override
    public void printShortStatistics() {
        if (count > 0) System.out.printf("Strings: %d%n", count);
    }

    @Override
    public void printFullStatistics() {
        System.out.println("STRING statistics:");
        System.out.printf(" Count: %d%n", count);
        System.out.printf(" Min length: %d%n", count > 0 ? minLength : 0);
        System.out.printf(" Max length: %d%n", maxLength);
    }

    @Override
    public void close() throws IOException {
        if (writer != null) {
            writer.close();
        }
    }
}
