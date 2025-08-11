package org.cft.processors;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

public class StringProcessor implements LineProcessor {

    private BufferedWriter writer;
    Path outputPath;
    String prefix;
    boolean appendMode;
    private long count = 0;
    private int minLength = Integer.MAX_VALUE;
    private int maxLength = 0;

    public StringProcessor(Path outputPath, String prefix, boolean appendMode) {
        this.outputPath = outputPath;
        this.prefix = prefix;
        this.appendMode = appendMode;
    }

    private void initWriter() throws IOException {
        if (writer == null) {
            OpenOption[] opts = appendMode
                    ? new OpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.APPEND}
                    : new OpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING};
            writer = Files.newBufferedWriter(outputPath.resolve(prefix + "strings.txt"),
                    StandardCharsets.UTF_8, opts);
        }
    }


    @Override
    public boolean canProcess(String line) {
        return true;
    }

    @Override
    public void process(String line) {
        try {
            initWriter();
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
