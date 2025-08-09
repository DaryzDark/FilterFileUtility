package org.cft;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.EnumMap;
import java.util.Map;

public class DataWriter implements AutoCloseable{

    private final Path outputPath;
    private final String prefix;
    private final boolean appendMode;

    private final Map<DataType, BufferedWriter> writers = new EnumMap<>(DataType.class);

    public DataWriter(Path outputPath, String prefix, boolean appendMode) {
        this.outputPath = outputPath;
        this.prefix = prefix;
        this.appendMode = appendMode;
    }


    public void writeToFile(String content, DataType type) throws IOException {
        BufferedWriter w = writers.computeIfAbsent(type, this::getWriter);
        try {
            w.write(content);
            w.newLine();
        } catch (IOException e) {
            throw new IOException("Failed writing to " + type + " file: " + e.getMessage(), e);
        }
    }

    private BufferedWriter getWriter(DataType type){
        try {
            OpenOption[] opts = appendMode
                    ? new OpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.APPEND}
                    : new OpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING};
            Path outputFile =  outputPath.resolve(prefix + type.name().toLowerCase() + "s.txt");
            return Files.newBufferedWriter(outputFile, StandardCharsets.UTF_8, opts);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void close() throws IOException {
        IOException firstEx = null;
        for (BufferedWriter w : writers.values()) {
            try { w.close(); } catch (IOException e) {
                if (firstEx == null) firstEx = e;
                else firstEx.addSuppressed(e);
            }
        }
        if (firstEx != null) throw firstEx;
    }
}
