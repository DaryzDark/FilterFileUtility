package org.cft.processors;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

public class FloatProcessor implements LineProcessor {
    private final BufferedWriter writer;
    private long count = 0;
    private BigDecimal min = null;
    private BigDecimal max = null;
    private BigDecimal sum = BigDecimal.ZERO;

    public FloatProcessor(Path outputPath, String prefix, boolean appendMode) {
        try {
            OpenOption[] opts = appendMode
                    ? new OpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.APPEND}
                    : new OpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING};
            Path outputFile = outputPath.resolve(prefix + "floats.txt");
            this.writer = Files.newBufferedWriter(outputFile, StandardCharsets.UTF_8, opts);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to create writer for floats file", e);
        }
    }

    @Override
    public boolean canProcess(String line) {
        return line.matches("^[+-]?(" +
                "\\d+\\.\\d*" +
                "|" +
                "\\d*\\.\\d+" +
                "|" +
                "\\d+(?:\\.\\d*)?[eE][+-]?\\d+" +
                ")$");
    }

    @Override
    public void process(String line) {
        try {
            BigDecimal value = new BigDecimal(line);
            if (min == null || value.compareTo(min) < 0) min = value;
            if (max == null || value.compareTo(max) > 0) max = value;
            sum = sum.add(value);
            count++;

            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            throw new UncheckedIOException("Failed writing to floats file", e);
        }
    }

    @Override
    public void printShortStatistics() {
        if (count > 0) System.out.printf("Floats: %d%n", count);
    }

    @Override
    public void printFullStatistics() {
        System.out.println("FLOAT statistics:");
        System.out.printf(" Count: %d%n", count);
        System.out.printf(" Min: %s%n", min);
        System.out.printf(" Max: %s%n", max);
        System.out.printf(" Sum: %s%n", sum);
        System.out.printf(" Average: %s%n",
                count > 0 ? sum.divide(BigDecimal.valueOf(count), RoundingMode.HALF_UP) : "N/A");
    }

    @Override
    public void close() throws IOException {
        if (writer != null) {
            writer.close();
        }
    }
}
