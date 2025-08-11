package org.cft.processors;

import java.io.Closeable;

public interface LineProcessor extends Closeable {
    boolean canProcess(String line);
    void process(String line);
    void printShortStatistics();
    void printFullStatistics();
}
