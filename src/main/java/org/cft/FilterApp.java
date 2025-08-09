package org.cft;


import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Callable;

@Command(name = "filter",
        mixinStandardHelpOptions = true,
        version = "1.0",
        description = "Utility for filtering file contents by data type")
public class FilterApp implements Callable<Integer> {

    @Parameters(description = "Input files for processing")
    private List<String> inputFiles;

    @Option(names = {"-o", "--output"},
            description = "Path for output data (default: current directory)")
    private String outputPath = ".";

    @Option(names = {"-p", "--prefix"},
            description = "Prefix name for output files")
    private String prefix = "";

    @Option(names = {"-a", "--append"},
            description = "Append mode")
    private boolean appendMode = false;

    @Option(names = {"-s", "--short"},
            description = "Short stats")
    private boolean shortStats = false;

    @Option(names = {"-f", "--full"},
            description = "Full stats")
    private boolean fullStats = false;

    @Override
    public Integer call() {
        if (inputFiles == null || inputFiles.isEmpty()) {
            System.err.println("Error: No input files specified");
            return 1;
        }

        try (FileFilter filter = new FileFilter(Path.of(outputPath), prefix, appendMode)) {

            for (String inputFile : inputFiles) {
                try {
                    filter.processFile(Paths.get(inputFile));
                } catch (IOException e) {
                    System.err.println("Error processing file " + inputFile + ": " + e.getMessage());
                }
            }

            Statistics stats = filter.getStatistics();
            if (shortStats && fullStats) {
                System.err.println("Warning: both -s (short) and -f (full) specified; defaulting to full statistics.");
                stats.printFullStatistics();
            } else if (fullStats) {
                stats.printFullStatistics();
            } else if (shortStats) {
                stats.printShortStatistics();
            }

            return 0;
        } catch (InvalidPathException e) {
            System.err.println("Invalid output path: " + outputPath);
            return 2;
        } catch (IOException e) {
            System.err.println("Critical I/O error: " + e.getMessage());
            return 3;
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            return 4;
        }
    }


    public static void main(String[] args) {
        int exitCode = new CommandLine(new FilterApp()).execute(args);
        System.exit(exitCode);
    }
}
