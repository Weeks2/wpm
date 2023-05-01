package com.tata.flux.service;

import com.tata.flux.model.DataSetRequest;
import com.tata.flux.model.FluxDataRecord;
import com.tata.flux.wpms.Post;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.http.HttpHeaders;
import java.util.ArrayList;
import java.util.List;

@Service
public class FluxService {

    @Value("$wordpress.default")
    private String defaulSite;

    public Flux<FluxDataRecord> getAllRecords(DataSetRequest dataSetRequest)
    {
        List<FluxDataRecord> list = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            list.add(new FluxDataRecord(i,"item" + i));
        }
        return Flux.fromIterable(list);
    }

}
