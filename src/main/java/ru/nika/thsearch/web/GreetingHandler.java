package ru.nika.thsearch.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.nika.thsearch.DAO.DataCard;
import ru.nika.thsearch.DAO.SQLDataCardDAO;
import ru.nika.thsearch.config.StartCheck;
import ru.nika.thsearch.driver.ChromeDriverInitConfig;
import ru.nika.thsearch.selenium.MainSelenium;

import java.io.IOException;
import java.util.*;

/**
 * @author Marat Sadretdinov
 */
@Component

public class GreetingHandler {
    @Autowired
    private SQLDataCardDAO sqlDataCardDAO;

    @Autowired
    private MainSelenium mainSelenium;

    public Mono<ServerResponse> index(ServerRequest request) {
        return ServerResponse
                .ok()
                .render("index", Map.of("rows", sqlDataCardDAO.getTableIndex()));

    }

    public Mono<ServerResponse> rows(ServerRequest request){
        return ServerResponse
                .ok()
                .header("Access-Control-Allow-Origin", "*")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(sqlDataCardDAO.getTableIndex()));
    }

    public Mono<ServerResponse> responsePost (ServerRequest request){
        Mono <String> stringMono = request.bodyToMono(String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(JsonReadFeature.ALLOW_LEADING_ZEROS_FOR_NUMBERS.mappedFeature());

         return ServerResponse
                 .ok()
                 .header("Access-Control-Allow-Origin", "*")
                 .contentType(MediaType.APPLICATION_JSON)
                 .body(stringMono
                         .log()
                         .map(e -> {
                             try {
                                 return objectMapper.readValue(e, DataCard.class);
                             } catch (JsonProcessingException jsonProcessingException) {
                                 jsonProcessingException.printStackTrace();
                             }
                             return null;
                         })
                         .doOnNext(sqlDataCardDAO::insert),DataCard.class);
    }

    public Mono<ServerResponse> responsePut(ServerRequest request){
        Mono <String> stringMono = request.bodyToMono(String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(JsonReadFeature.ALLOW_LEADING_ZEROS_FOR_NUMBERS.mappedFeature());

        return ServerResponse
                .ok()
                .header("Access-Control-Allow-Origin", "*")
                .contentType(MediaType.APPLICATION_JSON)
                .body(stringMono
                        .log()
                        .map(e -> {
                            try {
                                return objectMapper.readValue(e, DataCard.class);
                            } catch (JsonProcessingException jsonProcessingException) {
                                jsonProcessingException.printStackTrace();
                            }
                            return null;
                        })
                        .doOnNext(sqlDataCardDAO::update),DataCard.class);
    }

    public Mono<ServerResponse> responseDelete(ServerRequest request){
        Mono <String> stringMono = request.bodyToMono(String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(JsonReadFeature.ALLOW_LEADING_ZEROS_FOR_NUMBERS.mappedFeature());

        return ServerResponse
                .ok()
                .header("Access-Control-Allow-Origin", "*")
                .contentType(MediaType.APPLICATION_JSON)
                .body(stringMono
                        .log()
                        .map(e -> {
                            try {
                                return objectMapper.readValue(e, DataCard.class);
                            } catch (JsonProcessingException jsonProcessingException) {
                                jsonProcessingException.printStackTrace();
                            }
                            return null;
                        })
                        .doOnNext(sqlDataCardDAO::delete),DataCard.class);
    }

    public Mono<ServerResponse> responseChk (ServerRequest request){
        Mono<String> stringMono = request.bodyToMono(String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(JsonReadFeature.ALLOW_LEADING_ZEROS_FOR_NUMBERS.mappedFeature());
        return ServerResponse
                .ok()
                .header("Access-Control-Allow-Origin", "*")
                .contentType(MediaType.APPLICATION_JSON)
                .body(stringMono
                        .log()
                        .map(e -> {
                            try {
                                Map<String, String> map = new HashMap<>();
                                map.put("id",objectMapper.readTree(e).get("id").asText());
                                map.put("chk_search", objectMapper.readTree(e).get("chk_search").asText());
                                map.put("chk_apply", objectMapper.readTree(e).get("chk_apply").asText());
                                return map;
                            } catch (JsonProcessingException jsonProcessingException) {
                                jsonProcessingException.printStackTrace();
                            }
                            return null;
                        })
                        .doOnNext(sqlDataCardDAO::updateApply),DataCard.class);
    }

    public Mono<ServerResponse> responseJSON (ServerRequest request){
        Mono<DataCard> dataCardMono = request.bodyToMono(DataCard.class);
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(dataCardMono.doOnNext(sqlDataCardDAO::insert),DataCard.class);
    }


    public Mono<ServerResponse> startSearch(ServerRequest request){
        Thread seleniumThready = new Thread(mainSelenium);

        if(request.queryParam("start").get().equals("true")) {
            seleniumThready.setDaemon(true);
            seleniumThready.start();
        }
        else {
                mainSelenium.setSea(false);
        }
        return ServerResponse
                .ok()
                .header("Access-Control-Allow-Origin", "*")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue("OK"));
    }
    public Mono<ServerResponse> startChk(ServerRequest request){
        return ServerResponse
                .ok()
                .header("Access-Control-Allow-Origin", "*")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(mainSelenium.isSea()));
    }
}
