package com.tata.flux;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@AllArgsConstructor
@Data
@ToString
public class FluxDataRecord {
    @JsonProperty
    private Integer id;
    @JsonProperty
    private String name;
}
