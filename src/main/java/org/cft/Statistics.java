package org.cft;

import java.util.EnumMap;
import java.util.Map;

public class Statistics {
    private final Map<DataType, Stat> stats = new EnumMap<>(DataType.class);

    public Statistics() {
        stats.put(DataType.INTEGER, new IntegerStat());
        stats.put(DataType.FLOAT, new FloatStat());
        stats.put(DataType.STRING, new StringStat());
    }

    public void add(DataType type, String rawValue) {
        stats.get(type).add(rawValue);
    }

    public void printShortStatistics() {
        System.out.println("Short statistics:");
        stats.forEach((type, stat) -> {
            if (stat.getCount() > 0) {
                System.out.printf("%s: %d%n", type, stat.getCount());
            }
        });
    }

    public void printFullStatistics() {
        System.out.println("Full statistics:");
        stats.forEach((type, stat) -> {
            if (stat.getCount() > 0) {
                System.out.println(type + ":");
                stat.printDetails();
            }
        });
    }

    private interface Stat {
        void add(String rawValue);
        long getCount();
        void printDetails();
    }

    private static class IntegerStat implements Stat {
        private long count;
        private long min = Long.MAX_VALUE;
        private long max = Long.MIN_VALUE;
        private long sum;

        @Override
        public void add(String rawValue) {
            try {
                addValue(Long.parseLong(rawValue));
            } catch (NumberFormatException e) {
                throw new NumberFormatException(rawValue);
            }
        }

        public void addValue(long value) {
            if (value < min) min = value;
            if (value > max) max = value;
            sum += value;
            count++;
        }

        @Override
        public long getCount() { return count; }

        @Override
        public void printDetails() {
            System.out.printf("  Count: %d%n", count);
            System.out.printf("  Minimum: %d%n", min);
            System.out.printf("  Maximum: %d%n", max);
            System.out.printf("  Sum: %d%n", sum);
            System.out.printf("  Average: %.5f%n", count > 0 ? (double) sum / count : 0);
        }
    }

    private static class FloatStat implements Stat {
        private long count;
        private double min = Double.MAX_VALUE;
        private double max = -Double.MAX_VALUE;
        private double sum;

        @Override
        public void add(String rawValue) {
            try {
                addValue(Double.parseDouble(rawValue));
            } catch (NumberFormatException e) {
                throw new NumberFormatException(rawValue);
            }
        }

        public void addValue(double value) {
            if (value < min) min = value;
            if (value > max) max = value;
            sum += value;
            count++;
        }

        @Override
        public long getCount() { return count; }

        @Override
        public void printDetails() {
            System.out.printf("  Count: %d%n", count);
            System.out.printf("  Minimum: %s%n", min);
            System.out.printf("  Maximum: %s%n", max);
            System.out.printf("  Sum: %s%n", sum);
            System.out.printf("  Average: %s%n", count > 0 ? sum / count : 0);
        }
    }

    private static class StringStat implements Stat {
        private long count;
        private int minLength = Integer.MAX_VALUE;
        private int maxLength = 0;

        @Override
        public void add(String rawValue) {
            int len = rawValue.length();
            if (len < minLength) minLength = len;
            if (len > maxLength) maxLength = len;
            count++;
        }

        @Override
        public long getCount() { return count; }

        @Override
        public void printDetails() {
            System.out.printf("  Count: %d%n", count);
            System.out.printf("  Min length: %d%n", minLength);
            System.out.printf("  Max length: %d%n", maxLength);
        }
    }
}
