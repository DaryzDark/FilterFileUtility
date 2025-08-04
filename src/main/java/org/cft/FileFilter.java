package org.cft;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.*;
import java.util.regex.Pattern;

public class FileFilter implements AutoCloseable {

    private static final Logger logger = LoggerFactory.getLogger(FileFilter.class);

    private static final Pattern INTEGER_PATTERN = Pattern.compile("^[+-]?\\d+$");
    private static final Pattern FLOAT_PATTERN = Pattern.compile("^[+-]?(?:\\d*\\.\\d+|\\d+\\.\\d*)(?:[eE][+-]?\\d+)?$");

    private Path outputPath = Paths.get(".");
    private String prefix = "";
    private boolean appendMode = false;
    private final Statistics statistics = new Statistics();

    private BufferedWriter integerWriter;
    private BufferedWriter floatWriter;
    private BufferedWriter stringWriter;

    public void setOutputPath(Path outputPath) {
        this.outputPath = outputPath;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setAppendMode(boolean appendMode) {
        this.appendMode = appendMode;
    }

    public Statistics getStatistics() {
        return statistics;
    }

    public void processFile(Path inputFile) throws IOException {
        if (!Files.exists(inputFile)) {
            logger.error("File not found: {}", inputFile);
            throw new FileNotFoundException("File not found: " + inputFile);
        }

        if (!Files.isReadable(inputFile)) {
            logger.error("File is not available for read: {}", inputFile);
            throw new IOException("File is not available for read: " + inputFile);
        }

        logger.info("Processing file {}", inputFile);
        try (BufferedReader reader = Files.newBufferedReader(inputFile)) {
            String line;
            while ((line = reader.readLine()) != null) {
                processLine(line.trim());
            }
        }
    }

    private void processLine(String line) throws IOException {
        if (line.isEmpty()) {
            return;
        }

        DataType type = determineDataType(line);

        switch (type) {
            case INTEGER:
                statistics.addInteger(Long.parseLong(line));
                writeToFile(line, DataType.INTEGER);
                break;

            case FLOAT:
                statistics.addFloat(Double.parseDouble(line));
                writeToFile(line, DataType.FLOAT);
                break;

            case STRING:
                statistics.addString(line);
                writeToFile(line, DataType.STRING);
                break;
        }
    }

    private DataType determineDataType(String line) {
        if (INTEGER_PATTERN.matcher(line).matches()) {
            try {
                Long.parseLong(line);
                return DataType.INTEGER;
            } catch (NumberFormatException e) {
                return DataType.STRING;
            }
        }

        if (FLOAT_PATTERN.matcher(line).matches()) {
            try {
                Double.parseDouble(line);
                return DataType.FLOAT;
            } catch (NumberFormatException e) {
                return DataType.STRING;
            }
        }

        return DataType.STRING;
    }

    private void writeToFile(String content, DataType type) throws IOException {
        BufferedWriter writer = getWriter(type);
        if (writer != null) {
            writer.write(content);
            writer.newLine();
            writer.flush();
            logger.debug("Wrote '{}' to {}", content, type);
        }
    }

    private BufferedWriter getWriter(DataType type) throws IOException {
        OpenOption[] options;
        if (appendMode) {
            options = new OpenOption[]{
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            };
        } else {
            options = new OpenOption[]{
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            };
        }

        return switch (type) {
            case INTEGER -> {
                if (integerWriter == null && statistics.getIntegerCount() > 0) {
                    Path outputFile = outputPath.resolve(prefix + "integers.txt");
                    integerWriter = Files.newBufferedWriter(outputFile, options);
                }
                yield integerWriter;
            }
            case FLOAT -> {
                if (floatWriter == null && statistics.getFloatCount() > 0) {
                    Path outputFile = outputPath.resolve(prefix + "floats.txt");
                    floatWriter = Files.newBufferedWriter(outputFile, options);
                }
                yield floatWriter;
            }
            case STRING -> {
                if (stringWriter == null && statistics.getStringCount() > 0) {
                    Path outputFile = outputPath.resolve(prefix + "strings.txt");
                    stringWriter = Files.newBufferedWriter(outputFile, options);
                }
                yield stringWriter;
            }
        };
    }

    @Override
    public void close() throws IOException {
        IOException exception = null;
        logger.info("Closing writers");
        if (integerWriter != null) {
            try {
                integerWriter.close();
                logger.debug("Closed integer writer");
            } catch (IOException e) {
                exception = e;
            }
        }
        if (floatWriter != null) {
            try {
                floatWriter.close();
                logger.debug("Closed float writer");
            } catch (IOException e) {
                if (exception == null) {
                    exception = e;
                } else {
                    exception.addSuppressed(e);
                }
            }
        }
        if (stringWriter != null) {
            try {
                stringWriter.close();
                logger.debug("Closed string writer");
            } catch (IOException e) {
                if (exception == null) {
                    exception = e;
                } else {
                    exception.addSuppressed(e);
                }
            }
        }

        if (exception != null) {
            throw exception;
        }
    }

}
