package ru.nika.thsearch.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import ru.nika.thsearch.selenium.MainSelenium;

/**
 * @author Marat Sadretdinov
 */



public class StartCheck {

    private String start = "true";

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }
    @Async
    public void start(MainSelenium mainSelenium){
        try {
            mainSelenium.runParse();
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
    }
}
