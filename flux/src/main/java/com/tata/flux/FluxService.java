package com.tata.flux;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class FluxService {
    public Flux<FluxDataRecord> getAllRecords(DataSetRequest dataSetRequest)
    {
        List<FluxDataRecord> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(new FluxDataRecord(i,"item" + i));
        }
        return Flux.fromIterable(list);
    }


}
