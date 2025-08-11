package org.cft;

import org.cft.processors.FloatProcessor;
import org.cft.processors.IntegerProcessor;
import org.cft.processors.LineProcessor;
import org.cft.processors.StringProcessor;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;

public class FileFilter implements AutoCloseable {

    private final List<LineProcessor> processors;
    private final DataWriter writer;

    public FileFilter(Path outputPath, String prefix, boolean appendMode) {
        this.writer = new DataWriter(outputPath, prefix, appendMode);
        this.processors = List.of(
                new IntegerProcessor(writer),
                new FloatProcessor(writer),
                new StringProcessor(writer)
        );
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
                processLine(line.strip());
            }
        }
    }

    private void processLine(String line) {
        if (line.isEmpty()) return;
        try {
            for (LineProcessor p : processors) {
                if (p.canProcess(line)) {
                    p.process(line);
                    break;
                }
            }
        } catch (UncheckedIOException e) {
            System.err.println("I/O error processing line: '" + line + "'. Details: " + e.getMessage());
        } catch (RuntimeException e) {
            System.err.println("Processing error for line: '" + line + "'. Details: " + e.getMessage());
        }
    }

    public void printFullStatistics() {
        processors.forEach(LineProcessor::printFullStatistics);
    }

    public void printShortStatistics() {
        processors.forEach(LineProcessor::printShortStatistics);
    }


    @Override
    public void close() throws IOException { writer.close(); }
}
