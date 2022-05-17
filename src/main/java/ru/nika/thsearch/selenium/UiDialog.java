package ru.nika.thsearch.selenium;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import org.slf4j.Logger;
import reactor.core.publisher.Mono;
import ru.nika.thsearch.DAO.DataCard;
import ru.nika.thsearch.DAO.Errors;
import ru.nika.thsearch.DAO.SQLDataCardDAO;
import ru.nika.thsearch.mail.EmailSend;

/**
 * @author Marat Sadretdinov
 */

public class UiDialog {


    public boolean setUiDialog(ChromeDriver driver, WebDriverWait webDriverWait, TdValue tdValue, Map<String, String> map, Logger logger, SQLDataCardDAO sqlDataCardDAO, Map<String,String> mapMail) throws NoSuchElementException, StaleElementReferenceException, TimeoutException {
        WebElement uiDialog = webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@aria-labelledby='ui-dialog-title-dogovor' and contains(@style,'display: block;')]")));
        String car = map.get("car").toUpperCase().contains("СВОЯ") ? map.get("car").split(" ")[1].trim() : map.get("car").trim();
        String dov = Integer.parseInt(map.get("id")) < 10 ? "0" + map.get("id"): map.get("id");
        String date = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        String send = "";
        WebElement aweb = uiDialog.findElement(By.id("aweb"));
        aweb.sendKeys(car);
        try {
           WebElement li = webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//ul[contains(@class,'ui-autocomplete') and contains(@style,'display: block;')]//li[contains(@class,'ui-menu-item')][1]")));
           li.click();
        } catch (org.openqa.selenium.TimeoutException timeoutException) {
           ((JavascriptExecutor) driver).executeScript("arguments[0].click()", uiDialog.findElement(By.xpath("//a[@role='button']")));
           driver.navigate().refresh();
           try {
             Thread.sleep(1000);
           } catch (InterruptedException e) {
           }
           ((JavascriptExecutor) driver).executeScript("$(\"#chkcnt\").remove();");
           ((JavascriptExecutor) driver).executeScript("$(\"#chk\").remove();");
           String error = "Не удалось найти на сайте данную машину. Проверьте правильность заполнения поля \"Машина\". Отредактируйте данную заявку. (номер машины может быть указан верно, просто на сайте transport.tn.ru не всегда срабатывает авто подстановка машины.)";
//            errorsList(Integer.parseInt(map.get("id")),error);
            return false;
        }
        if(!map.get("car").toUpperCase().contains("СВОЯ")) {
            try {
                WebElement clickFraht = webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[contains(@class,'ui-button')]//span[text()='Фрахт']")));
                ((JavascriptExecutor) driver).executeScript("arguments[0].click()", clickFraht);
            } catch (TimeoutException | NoSuchElementException e) {
                logger.info("Поле №" + map.get("id") + " !!!Я не тыкнул на ФРАХ что-то не так!!!");
                try {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click()", driver.findElement(By.xpath("//div[@aria-labelledby='ui-dialog-title-c_price' and contains(@style,'display: block;')]//a[@role='button']")));
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click()", driver.findElement(By.xpath("//div[@aria-labelledby='ui-dialog-title-dogovor' and contains(@style,'display: block;')]//a[@role='button']")));
                } catch (StaleElementReferenceException | JavascriptException | NoSuchElementException exception) {

                }
                driver.navigate().refresh();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException interruptedException) {
                }
                ((JavascriptExecutor) driver).executeScript("$(\"#chkcnt\").remove();");
                ((JavascriptExecutor) driver).executeScript("$(\"#chk\").remove();");
                return false;
            }
            if (map.get("amount").equals("0") || map.get("amount").equals("")) {
                map.replace("amount", Integer.toString(Integer.parseInt(tdValue.getRate()) / 100 * 75));
                logger.info((new Date()) + map.get("amount"));
            }
            try {
                WebElement uiDialogNDS = driver.findElement(By.xpath("//div[@aria-labelledby='ui-dialog-title-c_price' and contains(@style,'display: block;')]"));
                webDriverWait.until(ExpectedConditions.visibilityOf(uiDialogNDS));
                WebElement c_amount = driver.findElement(By.id("c_amount"));
                ((JavascriptExecutor) driver).executeScript("arguments[0].value = ''", c_amount);
                c_amount.sendKeys(map.get("amount"));
                ((JavascriptExecutor) driver).executeScript("SetCPrice(null)");
                ((JavascriptExecutor) driver).executeScript("arguments[0].querySelector('.ui-button-text').click()", uiDialogNDS);
            } catch (IndexOutOfBoundsException | StaleElementReferenceException | JavascriptException | NoSuchElementException exception) {
                logger.info("Поле №" + map.get("id") + "Сбой в работе сайта. Не правильно выставил сумму");
                driver.navigate().refresh();
                return false;
            }
            try {
                driver.switchTo().alert();
                String msg = driver.switchTo().alert().getText();
//            errorsList(Integer.parseInt(map.get("id")),msg);
                driver.switchTo().alert().accept();
                driver.navigate().refresh();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
                try {
                    ((JavascriptExecutor) driver).executeScript("$(\"#chkcnt\").remove();");
                    ((JavascriptExecutor) driver).executeScript("$(\"#chk\").remove();");
                } catch (JavascriptException ex) {
                    logger.info((new Date()) + " #chkcnt not found");
                }
                logger.info("Поле №" + map.get("id") + "В алерте ошибка: " + msg);
                return false;
            } catch (NoAlertPresentException | UnhandledAlertException ex) {

            }
        }
        try {
            WebDriverWait webWait = new WebDriverWait(driver, 2);
            WebElement JClose = webWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[contains(@class,'jGrowl-notification')]//div[@class='close']")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click()",JClose);
        }catch (Exception exp){

        }
        try{
            WebElement accept = webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@aria-labelledby='ui-dialog-title-dogovor' and contains(@style,'display: block;')]//span[text()='Подтвердить']/parent::button")));
            accept.sendKeys("");
            ((JavascriptExecutor) driver).executeScript("arguments[0].click()",accept);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try{
            driver.switchTo().alert().accept();
            logger.info("Поле №" + map.get("id") + "В алерте accept ошибка");
        }catch (NoAlertPresentException ex){

        }
        WebElement dlgdoveren = webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@aria-labelledby='ui-dialog-title-dlgdoveren' and contains(@style,'display: block;')]")));
        WebElement dovnumfield = dlgdoveren.findElement(By.id("dovnumfield"));
        dovnumfield.sendKeys(dov);
        WebElement dovdatefield = dlgdoveren.findElement(By.id("dovdatefield"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].value = '" + date + "'",dovdatefield);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e1) {
        }
        ((JavascriptExecutor) driver).executeScript("arguments[0].click()",driver.findElement(By.xpath("//div[@aria-labelledby='ui-dialog-title-dlgdoveren' and contains(@style,'display: block;')]//span[text()='ОК']")));
        try {
            Thread.sleep(100);
        } catch (InterruptedException e2) {
        }
        if(map.get("chk_apply").equals("1558"))
        {
            logger.info("Применить ОК");
            WebElement dlgterminalCancel = webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@aria-labelledby='ui-dialog-title-dlgterminal' and contains(@style,'display: block;')]//span[text()='Отмена']")));
//            WebElement dlgterminalCancel = webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@aria-labelledby='ui-dialog-title-dlgterminal' and contains(@style,'display: block;')]//span[text()='ОК']")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click()", dlgterminalCancel);
            send = "Создана заявка №" + tdValue.getNumber() + " для машины " + car + ", место погрузки - " + map.get("placeOfLoading") + ", место доставки - " + tdValue.getpOl() + ", cумма - " + tdValue.getRate() + " (Поле №" + Integer.parseInt(map.get("id"))+ ").";
            EmailSend emailSend = new EmailSend();
            emailSend.mailSend(send, logger, mapMail, "Созданы новые заявки");

        }
        ((JavascriptExecutor) driver).executeScript("arguments[0].click()", driver.findElement(By.xpath("//div[@aria-labelledby='ui-dialog-title-dogovor' and contains(@style,'display: block;')]//a[@role='button']")));
        try {
            Thread.sleep(200);
        } catch (InterruptedException e2) {
        }
        sqlDataCardDAO.insertSuccessCard(new DataCard(Integer.parseInt(map.get("id")) , tdValue.getNumber()));
        return true;
    }
//    private void errorsList(Integer id, String error){
//        Errors errors = new Errors(id, error);
//        Mono<Errors> errorsMono = Mono.just(errors);
//        errorsMono.subscribe(sqlDataCardDAO::insertErrorsCard);
//    }
    private void successCard(Integer id, String numTask){

//        sqlDataCardDAO.insertSuccessCard(dataCard);
    }
}
