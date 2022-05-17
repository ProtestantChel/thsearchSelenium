package ru.nika.thsearch.selenium;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Marat Sadretdinov
 */

public class LoginInSite {

    final static Logger logger = LoggerFactory.getLogger(LoginInSite.class);
    public LoginInSite() {

    }

    public void authorizationSite(ChromeDriver driver, WebDriverWait webDriverWait,String user, String password)
            throws NoSuchElementException, StaleElementReferenceException {

            WebElement userSend = driver.findElement(By.id("ctl00_Main_tbUserName"));
            userSend.sendKeys(user);
            WebElement passwordSend = driver.findElement(By.id("ctl00_Main_tbPassword"));
            passwordSend.sendKeys(password);
            WebElement clickLogin = driver.findElement(By.id("ctl00_Main_btnLogin"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click()", clickLogin);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException interruptedException) {
            }
        if(driver.getCurrentUrl().contains("login")) {
            try {
                WebElement jGrowlMessage = driver.findElement(By.xpath("//div[@class='jGrowl-message']"));
                webDriverWait.until(ExpectedConditions.visibilityOf(jGrowlMessage));
                if (jGrowlMessage.isDisplayed()) {
                    logger.info(jGrowlMessage.getText());
//                    tehDAO.insertQuery(599999995, jGrowlMessage.getText() + " Исправьте значения в файле setting.properties и перезагрузите приложение(службу)");
                    driver.close();
                    driver.quit();
                }
            } catch (NoSuchElementException ex) {
                ex.printStackTrace();
            }
        }
    }
}
