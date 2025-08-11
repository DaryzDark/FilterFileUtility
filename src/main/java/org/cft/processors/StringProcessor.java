package org.cft.processors;

import org.cft.DataType;
import org.cft.DataWriter;

public class StringProcessor implements LineProcessor {
    private final DataWriter writer;
    private long count = 0;
    private int minLength = Integer.MAX_VALUE;
    private int maxLength = 0;

    public StringProcessor(DataWriter writer) {
        this.writer = writer;
    }

    @Override
    public boolean canProcess(String line) {
        return true;
    }

    @Override
    public void process(String line) {
        int len = line.length();
        if (len < minLength) minLength = len;
        if (len > maxLength) maxLength = len;
        count++;
        writer.writeToFile(line, DataType.STRING);
    }

    @Override
    public void printShortStatistics() {
        System.out.printf("Strings: %d%n", count);

    }

    @Override
    public void printFullStatistics() {
        System.out.println("STRING statistics:");
        System.out.printf(" Count: %d%n", count);
        System.out.printf(" Min length: %d%n", minLength);
        System.out.printf(" Max length: %d%n", maxLength);
    }
}

