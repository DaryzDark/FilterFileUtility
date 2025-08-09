package org.cft;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

public class FileFilter implements AutoCloseable {

    private final Statistics statistics = new Statistics();
    private final DataWriter writer;

    public FileFilter(Path outputPath, String prefix, boolean appendMode) {
        this.writer = new DataWriter(outputPath, prefix, appendMode);
    }

    public void processFile(Path inputFile) throws IOException {
        if (!Files.exists(inputFile)) {
            throw new FileNotFoundException("File not found: " + inputFile);
        }

        if (!Files.isReadable(inputFile)) {
            throw new IOException("File is not available for read: " + inputFile);
        }

        try (BufferedReader reader = Files.newBufferedReader(inputFile, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                processLine(line.trim());
            }
        }
    }

    private void processLine(String line)  {
        if (line.isEmpty()) {
            return;
        }

        DataType type = DataTypeDetector.determine(line);

        try {
            switch (type) {
                case INTEGER -> {
                    statistics.addInteger(Long.parseLong(line));
                    writer.writeToFile(line, DataType.INTEGER);
                }
                case FLOAT -> {
                    statistics.addFloat(Double.parseDouble(line));
                    writer.writeToFile(line, DataType.FLOAT);
                }
                case STRING -> {
                    statistics.addString(line);
                    writer.writeToFile(line, DataType.STRING);
                }
            }
        } catch (NumberFormatException e) {
            System.err.println("Parse error:" + e.getMessage());
        } catch (UncheckedIOException e) {
            System.err.println("Write failure: " + e.getCause().getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }

    public Statistics getStatistics() {
        return statistics;
    }

    @Override
    public void close() throws IOException { writer.close(); }
}
