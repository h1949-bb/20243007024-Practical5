package com.example.practicalfive;

import org.junit.Test;
import java.util.Locale;
import static org.junit.Assert.*;

public class TextProcessorTest {
    private final TextProcessor processor = new TextProcessor();

    @Test public void acceptsSupportedNamesAndMixedCase() {
        assertEquals("Beijing", processor.processMunicipality("Beijing"));
        assertEquals("Beijing", processor.processMunicipality("beijing"));
        assertEquals("Beijing", processor.processMunicipality("BEIJING"));
        assertEquals("Shanghai", processor.processMunicipality("SHANGHAI"));
        assertEquals("Tianjin", processor.processMunicipality("tianjin"));
        assertEquals("Chongqing", processor.processMunicipality("ChOnGqInG"));
    }
    @Test public void trimsSurroundingWhitespace() {
        assertEquals("Beijing", processor.processMunicipality("  Beijing  "));
        assertEquals("Tianjin", processor.processMunicipality("\tTIANJIN\n"));
    }
    @Test public void rejectsAbsentAndUnsupportedInput() {
        for (String input : new String[] {null, "", "   ", "\t\n", "Guangzhou", "Bei jing", "Beijing1"}) {
            assertNull("Expected invalid input: " + input, processor.processMunicipality(input));
        }
    }
    @Test public void normalisationDoesNotDependOnDeviceLocale() {
        Locale previous = Locale.getDefault();
        try {
            Locale.setDefault(new Locale("tr", "TR"));
            assertEquals("Beijing", processor.processMunicipality("BEIJING"));
            assertEquals("Tianjin", processor.processMunicipality("TIANJIN"));
        } finally {
            Locale.setDefault(previous);
        }
    }
}
