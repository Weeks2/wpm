package com.tata.flux.wpms;
 
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class PostProcessor {
    private final WordPressService wordPressService;

    public PostProcessor(WordPressService wordPressService) {
        this.wordPressService = wordPressService;
    }


    public void processNewPosts() {
        wordPressService.getPosts("")
                .filter(post -> post.getDate()
                .isAfter(LocalDateTime.now()
                .minusMinutes(5))).subscribe();
    }

    private void processPost(Post post) {
        log.info(post.toString());
    }
}
