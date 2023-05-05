package com.tata.flux.controller;
import com.tata.flux.wpms.PostEntity;
import com.tata.flux.wpms.PostRequest;
import com.tata.flux.wpms.WordPressService; 
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
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
       return wordPressService.readheader("x-wp-totalpages",uri);
    }

    @PostMapping ("/posts")
    public Flux<PostEntity> getPosts(@RequestBody PostRequest postrequest) {
        return wordPressService.getPostsBy(postrequest);
    }
}