package org.cft.processors;

import org.cft.DataType;
import org.cft.DataWriter;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class FloatProcessor implements LineProcessor {
    private final DataWriter writer;
    private long count = 0;
    private BigDecimal min = null;
    private BigDecimal max = null;
    private BigDecimal sum = BigDecimal.ZERO;

    public FloatProcessor(DataWriter writer) {
        this.writer = writer;
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
        BigDecimal value = new BigDecimal(line);
        if (min == null || value.compareTo(min) < 0) min = value;
        if (max == null || value.compareTo(max) > 0) max = value;
        sum = sum.add(value);
        count++;
        writer.writeToFile(line, DataType.FLOAT);
    }

    @Override
    public void printShortStatistics() {
        System.out.printf("Floats: %d%n", count);

    }

    @Override
    public void printFullStatistics() {
        System.out.println("INTEGER statistics:");
        System.out.printf(" Count: %d%n", count);
        System.out.printf(" Min: %s%n", min);
        System.out.printf(" Max: %s%n", max);
        System.out.printf(" Sum: %s%n", sum);
        System.out.printf(" Average: %s%n",
                count > 0 ? sum.divide(BigDecimal.valueOf(count), RoundingMode.HALF_UP) : "N/A");
    }
}
