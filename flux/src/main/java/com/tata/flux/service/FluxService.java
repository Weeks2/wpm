package com.tata.flux.service;

import com.tata.flux.model.DataSetRequest;
import com.tata.flux.model.FluxDataRecord;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

@Service
public class FluxService {
    public Flux<FluxDataRecord> getAllRecords(DataSetRequest dataSetRequest)
    {
        List<FluxDataRecord> list = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            list.add(new FluxDataRecord(i,"item" + i));
        }
        return Flux.fromIterable(list);
    }


}
