package com.tata.flux.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SiteInfo {
    private int id;
    private String baseUri;
    private int perPage;
    private int page;
}