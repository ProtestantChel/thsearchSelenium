package ru.nika.thsearch.driver;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.io.File;


/**
 * @author Marat Sadretdinov
 */

public class FirefoxDriverInitConfig{
    public FirefoxDriver firefoxDriver(){
        System.setProperty("webdriver.gecko.driver", System.getProperty("user.dir")  + "\\firefoxdriver\\geckodriver.exe");
        System.setProperty("webdriver.gecko.whitelistedIps", "127.0.0.1");
        FirefoxBinary binary = new FirefoxBinary(new File(System.getProperty("user.dir") + "\\firefox\\firefox.exe"));
        FirefoxOptions firefoxOptions = new FirefoxOptions();
        firefoxOptions.addArguments("start-maximized");
        firefoxOptions.addArguments("headless");
        firefoxOptions.setBinary(binary);
//        firefoxOptions.setProfile(new FirefoxProfile(new File(System.getProperty("user.dir")+"\\firefox\\profile")));
        DesiredCapabilities firefoxCapabilities = new DesiredCapabilities(firefoxOptions);
        FirefoxDriver firefoxDriver = new FirefoxDriver(firefoxCapabilities);
        return firefoxDriver;
    }
}
