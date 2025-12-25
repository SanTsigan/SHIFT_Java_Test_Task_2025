package com.example;

import java.io.*;

public class FileHandler {
    private PrintWriter writer;
    private final String filename;
    private final boolean append;

    public  FileHandler(String filename, boolean append) throws IOException {
        this.filename = filename;
        this.append = append;
        openWriter();
    }

    private void openWriter() throws IOException {
        java.io.File file = new java.io.File(filename);
        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        writer = new PrintWriter(new BufferedWriter(new FileWriter(filename, append)));
    }

    public void writeLine(String line) throws  IOException {
        if (writer == null) {
            openWriter();
        }
        writer.println(line);
    }

    public void close() {
        if (writer != null) {
            writer.close();
            writer = null;
        }
    }
}
