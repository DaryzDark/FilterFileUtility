package org.cft.processors;

public interface LineProcessor {
    boolean canProcess(String line);
    void process(String line);
    void printShortStatistics();
    void printFullStatistics();
}
