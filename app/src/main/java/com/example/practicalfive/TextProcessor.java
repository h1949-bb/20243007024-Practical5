package com.example.practicalfive;

import java.util.Locale;

public class TextProcessor {
    public String processMunicipality(String municipality) {
        if (municipality == null) {
            return null;
        }
        String cleaned = municipality.trim();
        if (cleaned.isEmpty()) {
            return null;
        }
        switch (cleaned.toLowerCase(Locale.ROOT)) {
            case "beijing":
                return "Beijing";
            case "shanghai":
                return "Shanghai";
            case "chongqing":
                return "Chongqing";
            case "tianjin":
                return "Tianjin";
            default:
                return null;
        }
    }
}
