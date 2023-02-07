package com.tata.flux.wpms;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PostRequest {
    @JsonProperty
    private String baseUrl;
    @JsonProperty
    private Integer count;
}
