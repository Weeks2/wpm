package com.tata.flux.service;

import com.tata.flux.model.SiteInfo;
import reactor.core.publisher.Flux;

public class SitesService {
    public static Flux<String> getAllSites() {
        return Flux.just("https://cambiodemichoacan.com.mx",
                "https://despertardelacosta.net",
                "https://abcdezihuatanejo.com",
                "http://enfoqueinformativo.mx",
                "https://digitalguerrero.com.mx",
                "https://suracapulco.mx",
                "https://novedadesaca.mx",
                "https://guerrero.quadratin.com.mx",
                "https://www.quadratin.com.mx",
                "http://www.rafaganoticiera.com",
                "https://zihuaboletinnoticias.com",
                "https://yavas.mx/");
    }
    public static Flux<String> getAllSitesWithDefault() {
        return getAllSites().map(site -> buildUri(site,100,1) );
    }
    public static Flux<SiteInfo> getAllSitesInfo() {
        return getAllSites().map(site -> SiteInfo.builder().id(1).baseUri(site).perPage(100).page(1).build());
    }
    public static String buildUri(String baseUri, int per_page, int page) {
        if(per_page==0) per_page = 100;
        if(page==0) page = 1;
        return String.format("%s/wp-json/wp/v2/posts?per_page=%s&page=%s", baseUri, per_page,page);
    }
}
