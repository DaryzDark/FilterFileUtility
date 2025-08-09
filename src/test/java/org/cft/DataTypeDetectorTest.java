package org.cft;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DataTypeDetectorTest {

    @Test
    void testDetermineIntegerValid() {
        assertEquals(DataType.INTEGER, DataTypeDetector.determine("0"));
        assertEquals(DataType.INTEGER, DataTypeDetector.determine("+123"));
        assertEquals(DataType.INTEGER, DataTypeDetector.determine("-456"));
        assertEquals(DataType.INTEGER, DataTypeDetector.determine("7890"));
    }

    @Test
    void testDetermineIntegerOverflow() {
        String huge = "9223372036854775808";
        assertEquals(DataType.STRING, DataTypeDetector.determine(huge));
    }

    @Test
    void testDetermineFloatValid() {
        assertEquals(DataType.FLOAT, DataTypeDetector.determine("0.0"));
        assertEquals(DataType.FLOAT, DataTypeDetector.determine(".5"));
        assertEquals(DataType.FLOAT, DataTypeDetector.determine("123."));
        assertEquals(DataType.FLOAT, DataTypeDetector.determine("-0.001"));
        assertEquals(DataType.FLOAT, DataTypeDetector.determine("1e3"));
        assertEquals(DataType.FLOAT, DataTypeDetector.determine("-2.5E-4"));
    }

    @Test
    void testDetermineFloatInvalid() {
        assertEquals(DataType.STRING, DataTypeDetector.determine("1.2.3"));
        assertEquals(DataType.STRING, DataTypeDetector.determine("e10"));
        assertEquals(DataType.STRING, DataTypeDetector.determine("-."));
    }

    @Test
    void testDetermineString() {
        assertEquals(DataType.STRING, DataTypeDetector.determine("hello"));
        assertEquals(DataType.STRING, DataTypeDetector.determine("123abc"));
        assertEquals(DataType.STRING, DataTypeDetector.determine(""));
    }
}
