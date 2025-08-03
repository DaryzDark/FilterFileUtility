package org.cft;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Callable;

@Command(name = "filter",
        mixinStandardHelpOptions = true,
        version = "1.0",
        description = "Утилита для фильтрации содержимого файлов по типам данных")
public class FilterApp implements Callable<Integer> {

    @Parameters(description = "Входные файлы для обработки")
    private List<String> inputFiles;

    @Option(names = {"-o", "--output"},
            description = "Путь для выходных файлов (по умолчанию: текущая папка)")
    private String outputPath = ".";

    @Option(names = {"-p", "--prefix"},
            description = "Префикс имен выходных файлов")
    private String prefix = "";

    @Option(names = {"-a", "--append"},
            description = "Режим добавления в существующие файлы")
    private boolean appendMode = false;

    @Option(names = {"-s", "--short"},
            description = "Short stats")
    private boolean shortStats = false;

    @Option(names = {"-f", "--full"},
            description = "Full stats")
    private boolean fullStats = false;

    @Override
    public Integer call() throws Exception {
        try {
            // Проверяем входные параметры
            if (inputFiles == null || inputFiles.isEmpty()) {
                System.err.println("Error: No input files specified");
                return 1;
            }

            // TO DO: Add try with resources
            FileFilter filter = new FileFilter();
            filter.setOutputPath(Paths.get(outputPath));
            filter.setPrefix(prefix);
            filter.setAppendMode(appendMode);

            // Обрабатываем файлы
            for (String inputFile : inputFiles) {
                try {
                    filter.processFile(Paths.get(inputFile));
                } catch (IOException e) {
                    System.err.println("Error while processing file:" + inputFile + ": " + e.getMessage());
                    // Продолжаем обработку других файлов
                }
            }

            // Выводим статистику
            Statistics stats = filter.getStatistics();
            if (shortStats) {
                stats.printShortStatistics();
            } else if (fullStats) {
                stats.printFullStatistics();
            }
            filter.close();
            return 0;

        } catch (Exception e) {
            System.err.println("Critical Error" + e.getMessage());
            return 1;
        }
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new FilterApp()).execute(args);
        System.exit(exitCode);
    }
}
