package com.tata.flux.wpms;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString
@Data
public class Post {
    @JsonProperty
    private Integer id;
    @JsonProperty
    private Title title;
    @JsonProperty
    private Integer author;
    @JsonProperty
    private LocalDateTime date;
    @JsonProperty
    private String link;
    @JsonProperty
    private Content content;
}
@Data
class Title {
    @JsonProperty
    private String rendered;
}

@Data
class Content {
    @JsonProperty
    private String rendered;
}