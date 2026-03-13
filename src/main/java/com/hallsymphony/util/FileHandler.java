package com.hallsymphony.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FileHandler {
    public static List<String> readFromFile(Path path) throws IOException {
        return Files.readAllLines(path);
    }

    public static void writeToFile(Path path, List<String> lines) throws IOException {
        Files.write(path, lines);
    }

    public static void updateRecord(Path path, int lineIndex, String newLine) throws IOException {
        List<String> lines = readFromFile(path);
        if (lineIndex >= 0 && lineIndex < lines.size()) {
            lines.set(lineIndex, newLine);
            writeToFile(path, lines);
        }
    }

    public static void deleteRecord(Path path, int lineIndex) throws IOException {
        List<String> lines = readFromFile(path);
        if (lineIndex >= 0 && lineIndex < lines.size()) {
            lines.remove(lineIndex);
            writeToFile(path, lines);
        }
    }
}
