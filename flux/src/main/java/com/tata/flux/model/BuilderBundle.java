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
        var bufferSize = 8192;
        this.file = file;
        this.writer = new FileWriter(this.file, true);
        this.buffer = new BufferedWriter(this.writer,bufferSize);
    }

    public BuilderBundle(BufferedWriter buffer) throws IOException {
        this.buffer = buffer;
    }

    public BuilderBundle(FileWriter writer) throws IOException {
        this.writer = writer;
    }

    public void close() throws IOException {
        if (buffer != null) buffer.close();
        if (writer != null) writer.close();
    }

    public String getFileName() {
        return file.getName();
    }
}