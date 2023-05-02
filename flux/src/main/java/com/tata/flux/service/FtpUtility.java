package com.tata.flux.service;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;
import reactor.core.scheduler.Schedulers;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Data
@Service
@Slf4j
public class FtpUtility {
    private List<String> fileNames = new ArrayList<>();


    public void compress(String fileName,String ext) throws Exception {
        File file = new File(fileName + ext);
        if(file.exists())
        {
            log.info("{} start zipped {} ", hour(),fileName + ".zip");
            File zip = writeToFileZipWithName(fileName,file);
            fileNames.add(zip.getName());
            log.info("{} end zipped {} ", hour(),zip.getName());
        }
    }

    private String hour() {
        return LocalDateTime.now().toLocalTime().toString().substring(0, 8);
    }
    public File writeToFile(List<String> list, String fileName, String header) throws IOException {
        list.add(0, header);
        return writeToFile(list, fileName);
    }
    public File writeToFile(List<String> list, String fileName) throws IOException {
        Path inputFile = Paths.get(fileName);
        Files.write(inputFile, list, StandardCharsets.UTF_8).toFile();
        return inputFile.toFile();
    }

    public File writeToFileZipWithName(String fileName, File file) throws Exception {
        Path outputFile = Paths.get(fileName + ".zip");
        try (ZipOutputStream outputStream = new ZipOutputStream(new FileOutputStream(outputFile.toFile()));) {
            ZipEntry zipEntry = new ZipEntry(file.toString());
            outputStream.putNextEntry(zipEntry);
            Files.copy(file.toPath(), outputStream);
            outputStream.closeEntry();
            return outputFile.toFile();
        }
    }

    private void writer(Flux<String> flux) {
        Path outFile = Path.of("output.txt");
        flux.flatMap(data -> {
            return Flux.using(() -> Files.newBufferedWriter(outFile, StandardOpenOption.APPEND),
                    writer -> {
                        try {
                            writer.write(data);
                            writer.newLine();
                        } catch (IOException e) {
                            return Flux.error(new RuntimeException(e));
                        }

                        return Flux.just(data);
                    },
                    writer -> {
                        try {
                            writer.flush();
                            writer.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }).onErrorMap(e -> new RuntimeException("Error al escribir en el archivo", e)).subscribe();
    }

    public Flux<String> build_(Flux<String> flux, String fileName, String header, String ext) throws IOException {
        File file = header == null ? new File(fileName+ext) :
                writeToFile(Arrays.asList(header), fileName + ext);
        try (FileWriter writer = new FileWriter(file, true);
             BufferedWriter buffer = new BufferedWriter(writer, 8192 * 10)) {
            return flux.doOnNext(line -> {
                        try {
                            buffer.write(line + System.lineSeparator());
                        } catch (IOException e) {

                        }
                    })
                    .doOnComplete(()-> {
                        fileNames.add(file.getName());
                    });
        }
    }

    public Flux<String> build(Flux<String> flux, String fileName, String header, String ext) throws Exception {
        File file = new File(fileName + ext);
        FileWriter writer = new FileWriter(file, true);
        int bufferSize = 8192;
        BufferedWriter buffer = new BufferedWriter(writer,bufferSize);
        log.info("{} start build {} ", hour(),fileName);
        return flux.doOnNext(line -> {
            try {
                buffer.write(line);
                buffer.newLine();
            } catch (Exception e) {
            }
        }).doOnComplete(() -> {
            try {
                log.info("{} end build {} ", hour(),fileName);
                fileNames.add(file.getName());
                if (buffer != null) buffer.close();
                if (writer != null) writer.close();
                // compress(".dat");

            } catch (Exception e) {
            }
        });
    }

}

