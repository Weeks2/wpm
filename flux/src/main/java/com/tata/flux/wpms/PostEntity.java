package com.tata.flux.wpms;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@Builder
public class PostEntity {
    private Integer id;
    private String title;
    private Integer author;
    private LocalDateTime date;
    private String link;
    private String content;

    public static PostEntity parsePost(Post post) {
        return PostEntity.builder()
                .id(post.getId())
                .title(post.getTitle().getRendered())
                .author(post.getAuthor())
                .date(post.getDate())
                .link(post.getLink())
                .content(post.getContent().getRendered())
                .build();
    }
}
