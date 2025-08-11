package org.cft.processors;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class IntegerProcessor implements LineProcessor {

    private final BufferedWriter writer;
    private long count = 0;
    private BigInteger min = null;
    private BigInteger max = null;
    private BigInteger sum = BigInteger.ZERO;

    public IntegerProcessor(Path outputPath, String prefix, boolean appendMode) {
        try {
            OpenOption[] opts = appendMode
                    ? new OpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.APPEND}
                    : new OpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING};
            Path outputFile = outputPath.resolve(prefix + "integers.txt");
            this.writer = Files.newBufferedWriter(outputFile, StandardCharsets.UTF_8, opts);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to create writer for integers file", e);
        }
    }

    @Override
    public boolean canProcess(String line) {
        return line.matches("^[+-]?\\d+$");
    }



    @Override
    public void process(String line) {
        try {
            BigInteger value = new BigInteger(line);
            if (min == null || value.compareTo(min) < 0) min = value;
            if (max == null || value.compareTo(max) > 0) max = value;
            sum = sum.add(value);
            count++;

            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            throw new UncheckedIOException("Failed writing to integers file", e);
        }
    }

    @Override
    public void printShortStatistics() {
        if (count > 0) System.out.printf("Integers: %d%n", count);

    }

    @Override
    public void printFullStatistics() {
        System.out.println("INTEGER statistics:");
        System.out.printf(" Count: %d%n", count);
        System.out.printf(" Min: %s%n", min);
        System.out.printf(" Max: %s%n", max);
        System.out.printf(" Sum: %s%n", sum);
        System.out.printf(" Average: %s%n",
                count > 0 ? sum.divide(BigInteger.valueOf(count)) : "N/A");
    }

    @Override
    public void close() throws IOException {
        if (writer != null) {
            writer.close();
        }
    }
}
