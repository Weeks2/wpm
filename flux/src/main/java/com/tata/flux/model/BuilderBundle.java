package com.tata.flux.model;
import lombok.Data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

@Data
public  class BuilderBundle {
    private FileWriter writer;
    private BufferedWriter buffer;
    private File file;

    public BuilderBundle(File file) throws IOException {
        this.file = file;
        this.writer = new FileWriter(this.file, true);
        this.buffer = new BufferedWriter(this.writer);
    }

    public void close() throws IOException {
        if (buffer != null) buffer.close();
        if (writer != null) writer.close();
    }
    public String getFileName() {
        return file.getName();
    }
}