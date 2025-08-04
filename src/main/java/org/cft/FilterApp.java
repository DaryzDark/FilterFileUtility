package org.cft;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Callable;

@Command(name = "filter",
        mixinStandardHelpOptions = true,
        version = "1.0",
        description = "Utility for filtering file contents by data type")
public class FilterApp implements Callable<Integer> {

    private static final Logger logger = LoggerFactory.getLogger(FilterApp.class);

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
            logger.error("No input files specified");
            System.err.println("Error: No input files specified");
            return 1;
        }

        try (FileFilter filter = new FileFilter()) {
            filter.setOutputPath(Paths.get(outputPath));
            filter.setPrefix(prefix);
            filter.setAppendMode(appendMode);

            for (String inputFile : inputFiles) {
                try {
                    logger.info("Starting processing of {}", inputFile);
                    filter.processFile(Paths.get(inputFile));
                } catch (IOException e) {
                    logger.warn("Error processing file {}: {}", inputFile, e.getMessage());
                    System.err.println("Error processing file " + inputFile + ": " + e.getMessage());
                }
            }

            Statistics stats = filter.getStatistics();
            if (shortStats) {
                logger.info("Printing short statistics");
                stats.printShortStatistics();
            } else if (fullStats) {
                logger.info("Printing full statistics");
                stats.printFullStatistics();
            }

            return 0;
        } catch (Exception e) {
            logger.error("Critical error: {}", e.getMessage(), e);
            System.err.println("Critical error: " + e.getMessage());
            return 1;
        }
    }


    public static void main(String[] args) {
        logger.info("CLI started with args {}", (Object) args);
        int exitCode = new CommandLine(new FilterApp()).execute(args);
        logger.info("CLI exited with code {}", exitCode);
        System.exit(exitCode);
    }
}
