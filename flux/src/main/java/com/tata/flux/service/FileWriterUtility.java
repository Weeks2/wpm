package com.tata.flux.service;

import com.tata.flux.model.BuilderBundle;
import reactor.core.publisher.Flux;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileWriterUtility {
    private List<String> fileList = new ArrayList<>();
    private List<String> zipList = new ArrayList<>();

    private File createFile(String header, String fileName, String ext) throws IOException {
        return header == null ? new File(fileName + ext) : writeToFile( fileName + ext, Arrays.asList(header));
    }

    public Flux<String> build(Flux<String> data, String fileName, String header, String ext)  {
        return Flux.using(() -> new BuilderBundle(createFile(fileName,header,ext)), resourceBundle -> {
            var buffer = resourceBundle.getBuffer();
            return data.doOnNext(
                    line -> {
                        try {
                            buffer.write(line);
                            buffer.newLine();
                        }
                        catch (Exception e) { Flux.error(e);}
            }).doOnComplete(() -> {
                try {
                    fileList.add(resourceBundle.getFileName());
                    zipList.add(fileName);
                }
                catch (Exception e) { Flux.error(e);}
            });
            }, resourceBundle -> {
                try { resourceBundle.close();}
                catch (IOException e) { Flux.error(e);}
        }).doFinally(signalType -> {});
    }
    private File writeToFile(String fileName,List<String> content) throws IOException {
        var inputFile = Paths.get(fileName);
        Files.write(inputFile, content, StandardCharsets.UTF_8);//.toFile();
        return inputFile.toFile();
    }

    private File fileToZip(String fileName, File file) throws Exception {
        var outputFile = Paths.get(fileName + ".zip");
        try (var outputStream = new ZipOutputStream(new FileOutputStream(outputFile.toFile()));) {
             var zipEntry = new ZipEntry(file.toString());
            outputStream.putNextEntry(zipEntry);
            Files.copy(file.toPath(), outputStream);
            outputStream.closeEntry();
            return outputFile.toFile();
        }
    }

    public void compress(String ext) {
        Flux.fromStream(zipList.stream()).map(fileName-> new File(fileName + ext))
                .filter(file->file.exists()).doOnNext(file -> {
                    try {
                        var zip = fileToZip(file.getName().replace(ext,""),file);
                        fileList.add(zip.getName());
                    }
                    catch (Exception e) {}
                }).doOnComplete(()-> zipList.clear()).subscribe();
    }
}
