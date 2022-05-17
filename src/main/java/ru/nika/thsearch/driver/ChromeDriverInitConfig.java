package ru.nika.thsearch.driver;

import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 * @author Marat Sadretdinov
 */

public class ChromeDriverInitConfig {
    public ChromeDriver getDriver(){
        System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "\\chromedriver\\chromedriver.exe");
        System.setProperty("webdriver.chrome.whitelistedIps", "127.0.0.1");
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--start-maximized");
//        chromeOptions.addArguments("--headless");
        return new ChromeDriver(chromeOptions);
    }

}
