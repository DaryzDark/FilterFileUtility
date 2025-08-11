package org.cft.processors;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

public class FloatProcessor implements LineProcessor {
    private  BufferedWriter writer;
    Path outputPath;
    String prefix;
    boolean appendMode;
    private long count = 0;
    private BigDecimal min = null;
    private BigDecimal max = null;
    private BigDecimal sum = BigDecimal.ZERO;

    public FloatProcessor(Path outputPath, String prefix, boolean appendMode) {
        this.outputPath = outputPath;
        this.prefix = prefix;
        this.appendMode = appendMode;
    }

    private void initWriter() throws IOException {
        if (writer == null) {
            OpenOption[] opts = appendMode
                    ? new OpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.APPEND}
                    : new OpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING};
            writer = Files.newBufferedWriter(outputPath.resolve(prefix + "floats.txt"),
                    StandardCharsets.UTF_8, opts);
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
            initWriter();
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
        System.out.printf(" Min: %s%n", formatDecimal(min));
        System.out.printf(" Max: %s%n", formatDecimal(max));
        System.out.printf(" Sum: %s%n", formatDecimal(sum));
        System.out.printf(" Average: %s%n",
                count > 0 ? formatDecimal(sum.divide(BigDecimal.valueOf(count), RoundingMode.HALF_UP)) : "N/A");
    }

    private String formatDecimal(BigDecimal value) {
        int STAT_SCALE = 5;
        RoundingMode STAT_ROUNDING = RoundingMode.HALF_UP;

        if (value == null) return "N/A";
        return value.setScale(STAT_SCALE, STAT_ROUNDING).stripTrailingZeros().toPlainString();
    }

    @Override
    public void close() throws IOException {
        if (writer != null) {
            writer.close();
        }
    }
}
