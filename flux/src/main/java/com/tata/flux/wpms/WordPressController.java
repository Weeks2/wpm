package com.tata.flux.wpms;

import com.tata.flux.SitiosWeb;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@Slf4j
public class WordPressController {
    private final WordPressService wordPressService;

    public WordPressController(WordPressService wordPressService) {
        this.wordPressService = wordPressService;
    }

    @GetMapping("/posts")
    public Flux<Post> getPosts(@RequestParam String uri) {
        log.info("uri {}",SitiosWeb.getLetter(uri));
        return wordPressService.getPosts(SitiosWeb.getLetter(uri)).take(3);
    }

    @PostMapping ("/posts")
    public Flux<Post> getPosts(@RequestBody PostRequest postrequest) {
        return wordPressService.getPosts(postrequest.getBaseUrl()).take(postrequest.getCount());
    }
}

