package org.cft;

public class Statistics {

    // Счетчики элементов
    private long integerCount = 0;
    private long floatCount = 0;
    private long stringCount = 0;

    // Статистика для целых чисел
    private long integerMin = Long.MAX_VALUE;
    private long integerMax = Long.MIN_VALUE;
    private long integerSum = 0;

    // Статистика для вещественных чисел
    private double floatMin = Double.MAX_VALUE;
    private double floatMax = -Double.MAX_VALUE;
    private double floatSum = 0.0;

    // Статистика для строк
    private int minStringLength = Integer.MAX_VALUE;
    private int maxStringLength = Integer.MIN_VALUE;

    public void addInteger(long value) {
        integerCount++;
        integerSum += value;

        if (value < integerMin) {
            integerMin = value;
        }
        if (value > integerMax) {
            integerMax = value;
        }
    }

    public void addFloat(double value) {
        floatCount++;
        floatSum += value;

        if (value < floatMin) {
            floatMin = value;
        }
        if (value > floatMax) {
            floatMax = value;
        }
    }

    public void addString(String value) {
        stringCount++;
        int length = value.length();

        if (length < minStringLength) {
            minStringLength = length;
        }
        if (length > maxStringLength) {
            maxStringLength = length;
        }
    }

    public void printShortStatistics() {
        System.out.println("Short statistics:");
        if (integerCount > 0) {
            System.out.println("Integers: " + integerCount);
        }
        if (floatCount > 0) {
            System.out.println("Floats: " + floatCount);
        }
        if (stringCount > 0) {
            System.out.println("Strings: " + stringCount);
        }
    }

    public void printFullStatistics() {
        System.out.println("Full statistics:");

        if (integerCount > 0) {
            System.out.println("Integers:");
            System.out.println("  Count: " + integerCount);
            System.out.println("  Minimum: " + integerMin);
            System.out.println("  Maximum: " + integerMax);
            System.out.println("  Sum: " + integerSum);
            System.out.println("  Average: " + (double) integerSum / integerCount);
        }

        if (floatCount > 0) {
            System.out.println("Floats:");
            System.out.println("  Count: " + floatCount);
            System.out.println("  Minimum: " + floatMin);
            System.out.println("  Maximum: " + floatMax);
            System.out.println("  Sum: " + floatSum);
            System.out.println("  Average: " + floatSum / floatCount);
        }

        if (stringCount > 0) {
            System.out.println("Strings:");
            System.out.println("  Count: " + stringCount);
            System.out.println("  Min length: " + minStringLength);
            System.out.println("  Max length: " + maxStringLength);
        }
    }


    // Геттеры для проверки наличия данных
    public long getIntegerCount() { return integerCount; }
    public long getFloatCount() { return floatCount; }
    public long getStringCount() { return stringCount; }
}