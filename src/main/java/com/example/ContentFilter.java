package com.example;

import org.apache.commons.cli.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ContentFilter {
    private static final String INTEGER_FILE = "integer.txt";
    private static final String FLOAT_FILE = "floats.txt";
    private static final String STRING_FILE = "strings.txt";

    public static void main(String[] args) {
        try {
            ContentFilter filter = new ContentFilter();
            filter.run(args);
        } catch (Exception e) {
            System.err.println("Error during execution: " + e.getMessage());
            System.exit(1);
        }
    }

    public void run(String[] args) throws Exception {
        Options options = createOptions();
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println("Error parsing arguments :" + e.getMessage());
            printHelp(options);
            System.exit(1);
            return;
        }

        if (cmd.hasOption("h")) {
            printHelp(options);
            return;
        }

        String[] inputFiles = cmd.getArgs();
        if (inputFiles.length == 0) {
            System.err.println("No input files specified");
            printHelp(options);
            System.exit(1);
            return;
        }

        for (String inputFile : inputFiles) {
            Path inputPath = Paths.get(inputFile);
            if (!Files.exists(inputPath) || !Files.isRegularFile(inputPath)) {
                System.err.println("Input file does not exist or is not a regular file: " + inputFile);
                System.exit(1);
                return;
            }
        }

        String outputPath = cmd.getOptionValue("o", ".");
        String prefix = cmd.getOptionValue("p", "");
        boolean appendMode = cmd.hasOption("a");
        boolean fullStats = cmd.hasOption("f");
        boolean shortStats = cmd.hasOption("s");

        Path outputDir = Paths.get(outputPath);
        if (!Files.exists(outputDir)) {
            Files.createDirectories(outputDir);
        }

        FileHandler integerHandler = new FileHandler(outputDir.resolve(prefix + INTEGER_FILE).toString(), appendMode);
        FileHandler floatHandler = new FileHandler(outputDir.resolve(prefix + FLOAT_FILE).toString(), appendMode);
        FileHandler stringHandler = new FileHandler(outputDir.resolve(prefix + STRING_FILE).toString(), appendMode);

        NumberStatistics integerStats = new NumberStatistics();
        NumberStatistics floatStats = new NumberStatistics();
        StringStatistics stringStats = new StringStatistics();

        boolean hasIntegers = false;
        boolean hasFloats = false;
        boolean hasStrings = false;

        for (String inputFile : inputFiles) {
            try (BufferedReader reader = Files.newBufferedReader(Paths.get(inputFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String trimmedLine = line.trim();
                    if (trimmedLine.isEmpty()) {
                        continue;
                    }

                    if (isInteger(trimmedLine)) {
                        integerHandler.writeLine(trimmedLine);
                        integerStats.addValue(Long.parseLong(trimmedLine));
                        hasIntegers = true;
                    } else if (isFloat(trimmedLine)) {
                        floatHandler.writeLine(trimmedLine);
                        floatStats.addValue(Double.parseDouble(trimmedLine));
                        hasFloats = true;
                    } else {
                        stringHandler.writeLine(trimmedLine);
                        stringStats.addValue(trimmedLine);
                        hasStrings = true;
                    }
                }
            } catch (IOException e) {
                System.err.println("Error reading file " + inputFile + ": " + e.getMessage());
            }
        }

        integerHandler.close();
        floatHandler.close();
        stringHandler.close();

        if(!hasIntegers) {
            Files.deleteIfExists(Paths.get(outputDir.resolve(prefix + INTEGER_FILE).toString()));
        }
        if(!hasFloats) {
            Files.deleteIfExists(Paths.get(outputDir.resolve(prefix + FLOAT_FILE).toString()));
        }
        if(!hasStrings) {
            Files.deleteIfExists(Paths.get(outputDir.resolve(prefix + STRING_FILE).toString()));
        }

        if (shortStats || fullStats) {
            if (hasIntegers) {
                System.out.println("Integers:");
                if (shortStats) {
                    System.out.println("    Count: " + integerStats.getCount());
                }
                if (fullStats) {
                    System.out.println("    Count: " + integerStats.getCount());
                    if (integerStats.getCount() > 0) {
                        System.out.println("    Min: " + integerStats.getMin());
                        System.out.println("    Max: " + integerStats.getMax());
                        System.out.println("    Sum: " + integerStats.getSum());
                        System.out.println("    Average: " + integerStats.getAverage());
                    }
                }
            }

            if (hasFloats) {
                System.out.println("Integers:");
                if (shortStats) {
                    System.out.println("    Count: " + floatStats.getCount());
                }
                if (fullStats) {
                    System.out.println("    Count: " + floatStats.getCount());
                    if (floatStats.getCount() > 0) {
                        System.out.println("    Min: " + floatStats.getMin());
                        System.out.println("    Max: " + floatStats.getMax());
                        System.out.println("    Sum: " + floatStats.getSum());
                        System.out.println("    Average: " + floatStats.getAverage());
                    }
                }
            }

            if (hasStrings) {
                System.out.println("String:");
                if(shortStats) {
                    System.out.println("    Count: " + stringStats.getCount());
                }
                if (fullStats) {
                    System.out.println("    Count: " + stringStats.getCount());
                    if (stringStats.getCount() > 0) {
                        System.out.println("    Min length: " + stringStats.getMinLength());
                        System.out.println("    Max length: " + stringStats.getMaxLength());
                    }
                }
            }
        }
    }

    private Options createOptions() {
        Options options = new Options();

        Option outputOption = new Option("o", "output", true, "output path (default is current directory)");
        outputOption.setRequired(false);
        options.addOption(outputOption);

        Option prefixOption = new Option("p", "prefix", true, "prefix for output file names");
        prefixOption.setRequired(false);
        options.addOption(prefixOption);

        Option appendOption = new Option("a", "append", false, "append mode to existing files");
        options.addOption(appendOption);

        Option shortStatsOption = new Option("s", "stats", false, "output short statistics");
        options.addOption(shortStatsOption);

        Option fullStatsOption = new Option("f", "full-stats", false, "output full statistics");
        options.addOption(fullStatsOption);

        Option helpOption = new Option("h", "help", false, "help");
        options.addOption(helpOption);

        return options;
    }

    private void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java -jar content-filter.jar [options] input-files...", options);
    }

    private boolean isInteger(String s) {
        try {
            Long.parseLong(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isFloat(String s) {
        try {
            Double.parseDouble(s);
            return  s.contains(".") || s.toLowerCase().contains("e");
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
