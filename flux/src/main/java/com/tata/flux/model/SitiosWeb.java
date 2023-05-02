package com.tata.flux.model;

import org.springframework.beans.factory.annotation.Value;

import java.util.Arrays;

public enum SitiosWeb {
    CAMBIO_DE_MICHOACAN("1","https://cambiodemichoacan.com.mx/"),
    DESPERTAR_DE_LA_COSTA("2","https://despertardelacosta.net/"),
    ABC_DE_ZIHUATANEJO("3","https://abcdezihuatanejo.com/"),
    ENFOQUE_INFORMATIVO("4","http://enfoqueinformativo.mx/"),
    DIGITAL_GUERRERO("5","https://digitalguerrero.com.mx/"),
    SUR_ACAPULCO("6","https://suracapulco.mx/"),
    NOVEDADES_ACA("7","https://novedadesaca.mx/"),
    GUERRERO_QUADRATIN("8","https://guerrero.quadratin.com.mx/"),
    QUADRATIN("9","https://www.quadratin.com.mx/"),
    RAFA_GANO_NOTICIERA("10","http://www.rafaganoticiera.com/"),
    ZIHUABOLETIN_NOTICIAS("11","https://zihuaboletinnoticias.com/"),
    YAVAS("12","https://yavas.mx/");
    private String number;
    private String letter;

    @Value("${wordpress.api_page}")
    private  String WP_API_;

    private SitiosWeb(String number,String letter) {
        this.number = number;
        this.letter = letter;
    }

    public String getLetter() {
        return letter + "wp-json/wp/v2/posts?per_page=100&page=1";
    }

    public String getNumber() {
        return number;
    }

    public static String getLetter(String number) {
        return (Arrays.stream(SitiosWeb.values())
                .filter(e -> e.getNumber().equals(number)).map(SitiosWeb::getLetter)
                .findFirst().orElse(""));
    }
}
