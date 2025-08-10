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
        if (INTEGER_PATTERN.matcher(line).matches()) { return DataType.INTEGER;}
        if (FLOAT_PATTERN.matcher(line).matches()) {return DataType.FLOAT;}
        return DataType.STRING;
    }

}
