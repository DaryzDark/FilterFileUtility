package org.cft.processors;

import org.cft.DataType;
import org.cft.DataWriter;

import java.math.BigInteger;

public class IntegerProcessor implements LineProcessor {

    private final DataWriter writer;
    private long count = 0;
    private BigInteger min = null;
    private BigInteger max = null;
    private BigInteger sum = BigInteger.ZERO;

    public IntegerProcessor(DataWriter writer) {
        this.writer = writer;
    }

    @Override
    public boolean canProcess(String line) {
        return line.matches("^[+-]?\\d+$");
    }



    @Override
    public void process(String line) {
        BigInteger value = new BigInteger(line);

        if (min == null || value.compareTo(min) < 0) min = value;
        if (max == null || value.compareTo(max) > 0) max = value;
        sum = sum.add(value);
        count++;
            writer.writeToFile(line, DataType.INTEGER);
    }

    @Override
    public void printShortStatistics() {
        System.out.printf("Integers: %d%n", count);

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
}
