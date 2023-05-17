package org.example;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

public class MapToFile {
    private static final String INDENT = "  ";

    private static void writeMap(StringBuilder sb, Map<URL, Object> map, int indent) {
        for (Map.Entry<URL, Object> entry : map.entrySet()) {
            for (int i = 0; i < indent; i++) {
                sb.append(INDENT);
            }
            sb.append(entry.getKey().toString()).append("\n");
            if (entry.getValue() instanceof Map && !((Map)entry.getValue()).isEmpty()) {
                writeMap(sb, (Map<URL, Object>) entry.getValue(), indent + 1);
            }
        }
    }

    public static void writeMapToFile(Map<URL, Object> map, String fileName) {
        StringBuilder sb = new StringBuilder();
        writeMap(sb, map, 0);
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(sb.toString());
        } catch (IOException e) {
            System.err.println("Error writing to file " + fileName + ": " + e.getMessage());
        }
    }
}