package com.tata.flux.controller;

import com.tata.flux.model.SitiosWeb;
import com.tata.flux.wpms.Post;
import com.tata.flux.wpms.PostEntity;
import com.tata.flux.wpms.PostRequest;
import com.tata.flux.wpms.WordPressService;
import io.netty.handler.codec.Headers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
public class WordPressController {
    private final WordPressService wordPressService;

    public WordPressController(WordPressService wordPressService) {
        this.wordPressService = wordPressService;
    }

    @GetMapping("/posts")
    public Flux<PostEntity> getPosts(@RequestParam String uri) {
        return wordPressService.getPosts(uri);
    }

    @GetMapping("/header")
    public Mono<Integer> header(@RequestParam String uri) {
       return wordPressService.header(uri);
    }

    @PostMapping ("/posts")
    public Flux<PostEntity> getPosts(@RequestBody PostRequest postrequest) {
        return wordPressService.getPostsBy(postrequest);
    }
}