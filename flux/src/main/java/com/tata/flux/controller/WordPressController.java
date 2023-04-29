package com.tata.flux.controller;

import com.tata.flux.model.SitiosWeb;
import com.tata.flux.wpms.Post;
import com.tata.flux.wpms.PostRequest;
import com.tata.flux.wpms.WordPressService;
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
        return wordPressService.getPosts(uri);
    }

    @PostMapping ("/posts")
    public Flux<Post> getPosts(@RequestBody PostRequest postrequest) {
        return wordPressService.getPostsBy(postrequest);
    }
}

