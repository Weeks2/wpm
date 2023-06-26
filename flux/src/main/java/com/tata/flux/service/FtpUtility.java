package com.tata.flux.service;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Service
@Slf4j
public class FtpUtility {
    private List<String> fileNames = new ArrayList<>();

    private String hour() {
        return LocalDateTime.now().toLocalTime().toString().substring(0, 8);
    }

    public Flux<String> build(Flux<String> flux, String fileName, String header, String ext) throws Exception {
        var file = new File(fileName + ext);
        var writer = new FileWriter(file, true);
        var bufferSize = 8192;
        var buffer = new BufferedWriter(writer,bufferSize);
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
            } catch (Exception e) {
            }
        });
    }

}

