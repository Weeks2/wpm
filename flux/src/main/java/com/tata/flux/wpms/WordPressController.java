package com.tata.flux.wpms;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
public class WordPressController {
    private final WordPressService wordPressService;

    public WordPressController(WordPressService wordPressService) {
        this.wordPressService = wordPressService;
    }

    @GetMapping("/posts")
    public Flux<Post> getPosts(@RequestParam String uri) {
        return wordPressService.getPosts(uri).take(3);
    }

    @PostMapping ("/posts")
    public Flux<Post> getPosts(@RequestBody PostRequest postrequest) {
        return wordPressService.getPosts(postrequest.getBaseUrl()).take(postrequest.getCount());
    }
}

