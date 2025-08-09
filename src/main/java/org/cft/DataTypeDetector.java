package org.cft;

import java.util.regex.Pattern;

public class DataTypeDetector {

    private static final Pattern INTEGER_PATTERN = Pattern.compile("^[+-]?\\d+$");
    private static final Pattern FLOAT_PATTERN = Pattern.compile(
            "^[+-]?(" +
                    "\\d+\\.\\d*" +
                    "|" +
                    "\\d*\\.\\d+" +
                    "|" +
                    "\\d+(?:\\.\\d*)?[eE][+-]?\\d+" +
                    ")$"
    );


    public static DataType determine(String line) {
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

}
