package ru.nika.thsearch.selenium;

import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import ru.nika.thsearch.DAO.DataCard;
import ru.nika.thsearch.DAO.SQLDataCardDAO;
import ru.nika.thsearch.config.PublicCard;
import ru.nika.thsearch.driver.ChromeDriverInitConfig;

import java.util.*;
import java.util.concurrent.TimeUnit;


/**
 * @author Marat Sadretdinov
 */
@Component
@PropertySource("file:setting.properties")
public class MainSelenium implements Runnable{

    @Value("${site}")
    private String url;
    @Value("${user}")
    private String user;
    @Value("${password}")
    private String password;

    @Value("${mail.smtp.host}")
    private String mailHost;
    @Value("${mail.from.Email}")
    private String mailFromEmail;
    @Value("${mail.to.Email}")
    private String mailToEmail;
    @Value("${mail.smtp.user}")
    private String mailUser;
    @Value("${mail.smtp.password}")
    private String mailPassword;
    @Value("${mail.smtp.port}")
    private String mailPort;
    @Value("${mail.smtp.auth")
    private String mailSmtpAuth;

    final static Logger logger = LoggerFactory.getLogger(MainSelenium.class);

    @Autowired
    private TdValue tdValue;
    @Autowired
    private PublicCard publicCard;
    @Autowired
    private SQLDataCardDAO sqlDataCardDAO;

    private volatile boolean mFinish = false;

    private byte count = 0;

    private boolean sea = false;

    public boolean isSea() {
        return sea;
    }

    public void setSea(boolean sea) {
        this.sea = sea;
    }

    private Map<String,String> getProps(){
        Map<String,String> map = new HashMap<>();
        map.put("mailHost", mailHost);
        map.put("mailFromEmail", mailFromEmail);
        map.put("mailToEmail", mailToEmail);
        map.put("mailUser", mailUser);
        map.put("mailPassword", mailPassword);
        map.put("mailPort", mailPort);
        map.put("mailSmtpAuth", mailSmtpAuth);
        return map;
    }

    public void runParse() throws InterruptedException, SessionNotCreatedException  {
            publicCard.setDataCards(sqlDataCardDAO.getTableSearch());
            LoginInSite loginInSite = new LoginInSite();
            UiDialog uiDialog = new UiDialog();
            SearchTR searchTR = new SearchTR();
            ChromeDriver driver = new ChromeDriverInitConfig().getDriver();
            logger.info("Запуск поиска");
            driver.manage().timeouts().pageLoadTimeout(20, TimeUnit.SECONDS);
            WebDriverWait webDriverWait = new WebDriverWait(driver, 10);
            //------ Шаг 1. Вход в систему
            try {
                driver.get(url);
                loginInSite.authorizationSite(driver, webDriverWait, user, password);
            } catch (NoSuchElementException | StaleElementReferenceException | TimeoutException exceptionLogin) {
                logger.info("Неудалось загрузить страницу входа в систему");
                driver.close();
                driver.quit();
                Thread.sleep(5000);
                count++;
                if(count < 10) {
                    runParse();
                }
                else {
                    count = 0;
                    logger.info("После 10 попыток входа в систему поиск заявок остановлен");
                    return;
                }
            }

            //------ Шаг 2. Остановка таймера обновления таблицы ------
            timerOff(driver);

            //------ Шаг 3. Поиск строки
        Map<String, Set<String>> setMap = new HashMap<>();
        Set<String> set = new HashSet<>();

        while (true) {
            if (!isSea()){
                driver.close();
                driver.quit();
                setSea(false);
                logger.info("Поиск заявок остановлен");
                return;
            }
            try {

                List <DataCard> dataCardList = sqlDataCardDAO.getTableSearch();

                for (DataCard dataCard : dataCardList){
                    if (!isSea()){
                        driver.close();
                        driver.quit();
                        setSea(false);
                        logger.info("Поиск заявок остановлен");
                        return;
                    }
                    Map<String, String> map = new HashMap<>();


                    map.put("id", Integer.toString(dataCard.getId()));
                    map.put("car", dataCard.getCar());
                    map.put("numTask", dataCard.getNumTask());
                    map.put("placeOfLoading", dataCard.getPlaceOfLoading());
                    map.put("placeOfDelivery", dataCard.getPlaceOfDelivery());
                    map.put("shipmentStart", dataCard.getShipmentStart());
                    map.put("ShipmentEnd", dataCard.getShipmentEnd());
                    map.put("loading", dataCard.getLoading());
                    map.put("amount", dataCard.getAmount());
                    map.put("num", dataCard.getNum());
                    map.put("onPlaceOfDelivery", dataCard.getOnPlaceOfDelivery());
                    map.put("chk_apply", Integer.toString(dataCard.getChk_apply()));
                    map.put("chk_check", Integer.toString(dataCard.getCol_check()));

                    setMap.computeIfAbsent(map.get("id"), k -> new HashSet<>());

                    try {
                        int intSearchTR = searchTR.searchTR(driver, map, sqlDataCardDAO, tdValue, logger, getProps(), setMap);
                        if(intSearchTR == 2) {
                            driver.close();
                            driver.quit();
                            setSea(false);
                            logger.info("Поиск заявок остановлен");
                        }
                        if(intSearchTR == 1) continue;
                        uiDialog.setUiDialog(driver, webDriverWait, tdValue, map, logger, sqlDataCardDAO, getProps());
                    } catch (NoSuchElementException | StaleElementReferenceException | TimeoutException e) {
                        Thread.sleep(5000);
                        ((JavascriptExecutor) driver).executeScript("Ajax()");
                        try {
                            driver.findElement(By.xpath("//div[contains(@class,'ui-dialog ui-widget ui-widget-content ui-corner-all ui-draggable') and contains(@style,'display: block')]"));
                            driver.navigate().refresh();
                        }catch (NoSuchElementException | StaleElementReferenceException | TimeoutException ex){

                        }
                    } catch (WebDriverException webDriverException){
                        setSea(false);
                    }
                }

//                while (dataCardIterator.hasNext()){
//                    if (!isSea()){
//                        driver.close();
//                        driver.quit();
//                        setSea(false);
//                        logger.info("Поиск заявок остановлен");
//                        return;
//                    }
//                    Map<String, String> map = new HashMap<>();
//                    DataCard dataCard = dataCardIterator.next();
//                    map.put("id", Integer.toString(dataCard.getId()));
//                    map.put("car", dataCard.getCar());
//                    map.put("numTask", dataCard.getNumTask());
//                    map.put("placeOfLoading", dataCard.getPlaceOfLoading());
//                    map.put("placeOfDelivery", dataCard.getPlaceOfDelivery());
//                    map.put("shipmentStart", dataCard.getShipmentStart());
//                    map.put("ShipmentEnd", dataCard.getShipmentEnd());
//                    map.put("loading", dataCard.getLoading());
//                    map.put("amount", dataCard.getAmount());
//                    map.put("num", dataCard.getNum());
//                    map.put("onPlaceOfDelivery", dataCard.getOnPlaceOfDelivery());
//                    map.put("chk_apply", Integer.toString(dataCard.getChk_apply()));
//                    map.put("chk_check", Integer.toString(dataCard.getCol_check()));
//                    try {
//                        if(!searchTR.searchTR(driver, map, sqlDataCardDAO, tdValue, logger, getProps())) continue;
//                        if(uiDialog.setUiDialog(driver, webDriverWait, tdValue, map, logger, sqlDataCardDAO, getProps()))
//                            dataCardIterator.remove();
//                    } catch (NoSuchElementException | StaleElementReferenceException | TimeoutException e) {
//                        Thread.sleep(5000);
//                        ((JavascriptExecutor) driver).executeScript("Ajax()");
//                        try {
//                            driver.findElement(By.xpath("//div[contains(@class,'ui-dialog ui-widget ui-widget-content ui-corner-all ui-draggable') and contains(@style,'display: block')]"));
//                            driver.navigate().refresh();
//                            e.printStackTrace();
//                        }catch (NoSuchElementException | StaleElementReferenceException | TimeoutException ex){
//
//                        }
//                    } catch (WebDriverException webDriverException){
//                        setSea(false);
//                    }
//
//                }
            }catch (NullPointerException pe){
                pe.printStackTrace();
            }

            //Перелистнуть страницу если есть.
            //.......
            try {
                WebDriverWait pagerWait = new WebDriverWait(driver, 3);
                WebElement pagerEnd = driver.findElement(By.xpath("//span[@class='pager end']"));
                pagerWait.until(ExpectedConditions.visibilityOf(pagerEnd));
                WebElement pagerNext = driver.findElement(By.xpath("//span[@class='pager next']"));
                pagerWait.until(ExpectedConditions.visibilityOf(pagerNext));
                ((JavascriptExecutor) driver).executeScript("arguments[0].click()", pagerNext);
                continue;

            } catch (NoSuchElementException | StaleElementReferenceException | TimeoutException ex) {

            }

            try {
                ((JavascriptExecutor) driver).executeScript("Ajax()");
            }catch (JavascriptException je) {
                try {
                    driver.navigate().refresh();
                } catch (Exception er){
                    logger.info("REFRESH ERROR");
                    er.printStackTrace();
                }

                je.printStackTrace();
            }
            timerOff(driver);

        }
    }
    private void timerOff(WebDriver driver){
        try {
            ((JavascriptExecutor) driver).executeScript("$(\"#chkcnt\").remove();");
            ((JavascriptExecutor) driver).executeScript("$(\"#chk\").remove();");
        } catch (org.openqa.selenium.NoSuchSessionException ex) {

        }
    }

    @Override
    public void run() {
                try {
                    setSea(true);
                    runParse();
                } catch (InterruptedException interruptedException) {
                    setSea(false);
                } catch (SessionNotCreatedException exception){
                    logger.info("Версия драйвера не соответствует браузеру chrome\nЗавершите все процессы chromedriver.exe и обновите драйвер");
                    setSea(false);
                }
    }
}
