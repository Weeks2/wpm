package com.tata.flux.wpms;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tata.flux.model.SitiosWeb;
import com.tata.flux.service.FtpUtility;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@Slf4j
@Data
@Service
public class WordPressService {
    private final WebClient webClient;
    private final FtpUtility ftpUtility;
    private static final String WORDPRESS_API_URL = "https://your-wordpress-site.com/wp-json/wp/v2/posts";
    public WordPressService(WebClient.Builder webClientBuilder, FtpUtility ftpUtility) {
        this.webClient = webClientBuilder.build();
        this.ftpUtility = ftpUtility;
    }
    public Flux<PostEntity> getPostsBy(PostRequest postRequest) {
        return getPosts(postRequest.getBaseUrl()).take(postRequest.getCount());
    }
    public Flux<PostEntity> getPosts(String site) {
        String uri = SitiosWeb.getLetter(site).replace("=100", "=1");
        log.info(uri);
        return webClient.get().uri(uri)
        .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(Post.class)
                .map(PostEntity::parsePost)
                .doOnNext(d-> {
                    log.info("{}",d.getTitle());
                })
                .onErrorContinue( (e,o) -> {
                    if (site.isBlank()) {
                        log.error("La URL de la base está en blanco");
                    }
                    log.error("Se ha producido un error {}", createUri(site));
                });
    }

    public Flux<Post> loadData(String site, int totalPages) {
        return Flux.range(1, totalPages).concatMap(i -> {
            return webClient.get()
                    .uri(createUri(site))
                    .retrieve()
                    .bodyToFlux(Post.class)
                    .doOnNext(d-> {
                        log.info("{}",d.getTitle());
                    });
        }).doOnComplete(() -> {
        });
    }

    private String createUri(String uriId) {
        return SitiosWeb.getLetter(uriId);
    }

    void init() {
        String headerUri = createUri("1").replace("PAGE","1");
        readheader("x-wp-totalpages",headerUri).map(totalPages->{
            Flux.range(1,totalPages)
                    .concatMap(page-> {
                        try {
                            String uri = createUri("1").replace("PAGE",page.toString());
                            Flux<String> posts = pullAll(uri).map(post-> post.getTitle().getRendered());
                            return ftpUtility.build(posts,"site_"+ page,"header",".txt");
                        } catch (Exception e) {
                            return Flux.empty();
                        }
                    }).subscribe();
            return Flux.empty();
        }).subscribe();
    }

    private String convertTitle(Post post)
    {
        return post.getTitle().getRendered();
    }

    void initPages() {
        Flux.fromStream(Arrays.stream(SitiosWeb.values())).concatMap(site->{
            String uri = site.getLetter() .replace("PAGE","1");
            log.info("site {}",uri);
            return Flux.empty();
        }).subscribe();
    }

    public Mono<Integer> readheader(String header,String uri) {
        return webClient.get().uri(uri).exchangeToMono(response -> response.toBodilessEntity())
                .map(entity -> entity.getHeaders().getFirst(header))
                .map(totalPagesStr -> totalPagesStr != null ? Integer.parseInt(totalPagesStr) : 0);
    }

    private Flux<Post> pullPosts(String uri) {
        log.info("site {}",uri);
        return webClient.get().uri(uri).retrieve().bodyToFlux(Post.class)
                .doOnNext(d-> log.info("{}",d.getTitle()));
    }

    /**
     * X-WP-Total: 17454, X-WP-TotalPages: 175
     */
    private Flux<Post> pullAll(String number) {
        Flux<Post> posts = readheader("x-wp-totalpages",SitiosWeb.getLetter(number)).flatMapMany(totalPages-> {
            return Flux.range(1,totalPages).concatMap(page-> {
                       return pullPosts(SitiosWeb.getLetterPage(number).replace("PAGE", page.toString()));
                    });
                });
        //posts.subscribe();
        return posts;
    }

    //@PostConstruct
    void init_() {
        Flux.fromStream(Arrays.stream(SitiosWeb.values()))
        .flatMap(site->{
                    try {
                        Flux<String> data = pullAll(site.getNumber()).map(this::convertTitle);
                        return ftpUtility.build(data,"site_complete"+site.getNumber(),"header",".txt");
                    }
                    catch (Exception e) {
                        return Flux.error(e);
                    }
        },5)
        .onErrorContinue((t,o) -> {
            log.error("{}", t.getMessage());
        }).subscribe();
    }
 public static void testApi() {
        WebClient webClient = WebClient.builder().baseUrl(WORDPRESS_API_URL).build();

        webClient.get().accept(MediaType.APPLICATION_JSON).retrieve()
        .bodyToFlux(JsonNode.class)
                .map(postNode -> {
                    String title = postNode.get("title").get("rendered").asText();
                    String author = postNode.get("author").asText();
                    String content = postNode.get("content").get("rendered").asText();
                    String date = postNode.get("date").asText();
                    // Agregue cualquier otra propiedad de la publicación que desee acceder aquí
                    return String.format("Title: %s, Author: %s, Content: %s, Date: %s", title, author, content, date);
                })
                .subscribe(System.out::println);
    }

    public static void testApi_(String[] args) {
        WebClient webClient = WebClient.builder().baseUrl(WORDPRESS_API_URL).build();

        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("title", "Title of the new post");
        data.add("content", "Content of the new post");
        data.add("status", "publish");

        webClient.post()
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(data))
                .header(HttpHeaders.AUTHORIZATION, "Bearer YOUR_ACCESS_TOKEN")
                .retrieve()
                .bodyToMono(JsonNode.class)
                .subscribe(response -> {
                    System.out.println("New post ID: " + response.get("id").asInt());
                });
            }

}
