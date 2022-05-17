package ru.nika.thsearch.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import ru.nika.thsearch.DAO.SQLDataCardDAO;
import ru.nika.thsearch.web.GreetingHandler;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

/**
 * @author Marat Sadretdinov
 */
@Configuration(proxyBeanMethods = false)
public class GreetingRouter {
    @Autowired
    private SQLDataCardDAO sqlDataCardDAO;
    @Bean
    public RouterFunction<ServerResponse> route(GreetingHandler greetingHandler) {

        return RouterFunctions.route()
                .GET("/", greetingHandler::index)
                .GET("/rows", greetingHandler::rows)
                .POST("/chk", accept(MediaType.APPLICATION_JSON), greetingHandler::responseChk)
                .POST("/form",accept(MediaType.APPLICATION_JSON), greetingHandler::responsePost)
                .POST("/update",accept(MediaType.APPLICATION_JSON), greetingHandler::responsePut)
                .POST("/delete",accept(MediaType.APPLICATION_JSON), greetingHandler::responseDelete)
                .POST("/json", greetingHandler::responseJSON)
                .GET("/start",accept(MediaType.APPLICATION_JSON), greetingHandler::startSearch)
                .GET("/chksearch",greetingHandler::startChk)
                .build();

    }
}
