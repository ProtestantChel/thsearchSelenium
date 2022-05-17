package ru.nika.thsearch.selenium;

import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.nika.thsearch.DAO.SQLDataCardDAO;
import ru.nika.thsearch.mail.EmailSend;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Marat Sadretdinov
 */

public class SearchTR {


    private String dateConvert(String date){

        return date.replace("-","").replace(" ","").replace(":","");
    }

    private boolean checkString(String source, String dist){
        if (dist == null) dist = "";
        if (source == null) source = "";
        if(source.equals("") || dist.equals("")) return true;
        List<String> arrSource = new ArrayList<>(Arrays.asList(source.split("\\s*,\\s*")));
        List<String> arrDist = new ArrayList<>(Arrays.asList(dist.split("\\s*,\\s*")));
        arrDist = arrDist.stream().distinct().collect(Collectors.toList());
        Integer allCount = Math.toIntExact(Stream.concat(arrSource.stream(), arrDist.stream()).distinct().count());
        if((arrSource.size() >= arrDist.size() && allCount  > arrSource.size())
                || (arrSource.size() + arrDist.size()) == allCount )
            return true;
        else
            return false;
    }
    public int searchTR(ChromeDriver driver, Map<String, String> map, SQLDataCardDAO sqlDataCardDAO, TdValue tdValue, Logger logger, Map<String,String> mapMail, Map<String, Set<String>> setMap) throws NoSuchElementException, StaleElementReferenceException, TimeoutException {
        String car = map.get("car").toUpperCase().contains("СВОЯ") ? map.get("car").split(" ")[1].trim() : map.get("car").trim();
//        String stringTR = "//tr[not(./td[1]//img) and ./td[3][(substring(@title,1,1)!=\"К\") and (substring(@title,1,1)!=\"Ф\")] and ";
        String stringTR = "//tr[./td[3][(substring(@title,1,1)!=\"К\") and (substring(@title,1,1)!=\"Ф\")] and ";
        if (map.get("placeOfLoading") != null && !map.get("placeOfLoading").equals(""))
            stringTR = stringTR + "./td[@title=\"" + map.get("placeOfLoading") + "\"]";

        if (map.get("placeOfDelivery") != null && !map.get("placeOfDelivery").equals(""))
            stringTR = stringTR + " and ./td[@title=\"" + map.get("placeOfDelivery") + "\"]";

        if ((map.get("shipmentStart") != null && !map.get("shipmentStart").equals("")))
            stringTR = stringTR + " and number(concat(substring(./td[8]/@title,7,4),substring(./td[8]/@title,4,2),substring(./td[8]/@title,1,2)," +
                    "substring(./td[8]/@title,12,2),substring(./td[8]/@title,15,2)))>=" + dateConvert(map.get("shipmentStart"));

        if ((map.get("shipmentEnd") != null && !map.get("shipmentEnd").equals("")))
            stringTR = stringTR + " and number(concat(substring(./td[8]/@title,7,4),substring(./td[8]/@title,4,2),substring(./td[8]/@title,1,2)," +
                    "substring(./td[8]/@title,12,2),substring(./td[8]/@title,15,2)))<" + dateConvert(map.get("shipmentEnd"));

        if ((map.get("loading") != null && !map.get("loading").equals(""))) {
            if(checkString(map.get("loading"), driver.findElement(By.xpath(stringTR + "]//child::td[12]")).getText())
                && checkString(map.get("loading"), driver.findElement(By.xpath(stringTR + "]//child::td[13]")).getText()))
                stringTR = stringTR + "]";
            else stringTR = "";
        } else {
            if (map.get("onPlaceOfDelivery") != null && !map.get("onPlaceOfDelivery").equals(""))
                stringTR = stringTR + " and ./td[6][not(contains(\"" + map.get("onPlaceOfDelivery") + "\",@title))]";
            stringTR = stringTR + "]";
        }

        if (map.get("num") != null && !map.get("num").equals(""))
            stringTR = "//tr[./td[3][(substring(@title,1,1)!=\"К\") and (substring(@title,1,1)!=\"Ф\") and contains(@title,\"" + map.get("num") + "\")]]";


        if (!stringTR.equals("")){
            WebDriverWait webDriverWait = new WebDriverWait(driver, 20);
            try {
                try {
                    webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//table[contains(@class,'treeTable')]")));
                } catch (NoSuchElementException | StaleElementReferenceException | TimeoutException e){
                    driver.navigate().refresh();
                    try {
                        webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//table[contains(@class,'treeTable')]")));
                    }catch (NoSuchElementException | StaleElementReferenceException | TimeoutException ex){
                        ex.printStackTrace();
                        return 2;
                    }

                }
                WebElement trTable = driver.findElement(By.xpath(stringTR));
                try {
                    WebElement trDelivery = driver.findElement(By.xpath(stringTR + "/td[6]"));
                    tdValue.setpOl(trDelivery.getText());
                }catch (NoSuchElementException | StaleElementReferenceException e){

                }
                webDriverWait.until(ExpectedConditions.visibilityOf(trTable));
                if (map.get("chk_apply").equals("1558")) {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click()", trTable);
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click()", trTable);
                }
                //--- Сохранения знаяений полей теблицы для дальнешей обработки
                tdValue.setNumber(driver.findElement(By.xpath(stringTR + "//child::td[3]")).getText());
                tdValue.setRate(driver.findElement(By.xpath(stringTR + "//child::td[14]")).getText());
                if (map.get("chk_apply").equals("1559")) {
                    map.put("chk_search", "");
                    map.put("chk_apply", "1557");
                    sqlDataCardDAO.updateApply(map);
                }
                if (map.get("chk_apply").equals("1557")){
                    try {
                        List<WebElement> num = driver.findElements(By.xpath(stringTR + "/td[3]"));
                        List<WebElement> placeOfLoading = driver.findElements(By.xpath(stringTR + "/td[5]"));
                        List<WebElement> placeOfDelivery = driver.findElements(By.xpath(stringTR + "/td[6]"));
                        List<WebElement> loading = driver.findElements(By.xpath(stringTR + "/td[12]"));
                        List<WebElement> amount = driver.findElements(By.xpath(stringTR + "/td[14]"));
                        String send = "";
                        List<RowsLast> rowsLasts = new ArrayList<>();
                        for (int i = 0; i < num.size(); i++) {
                            if (!setMap.get(map.get("id")).contains(num.get(i).getText())) {
                                send = send + "Появилась заявка №" + num.get(i).getText() + " для машины " + car + ", место погрузки - " + placeOfLoading.get(i).getText() + ", место доставки - " + placeOfDelivery.get(i).getText() + ", cумма - " + amount.get(i).getText() + " (Поле №" + Integer.parseInt(map.get("id")) + ")." +  "\n";
                                setMap.get(map.get("id")).add(num.get(i).getText());
                                RowsLast rowsLast = new RowsLast();
                                rowsLast.setId(Integer.parseInt(map.get("id")));
                                rowsLast.setNumTask(num.get(i).getText());
                                rowsLast.setPlaceOfLoading(placeOfLoading.get(i).getText());
                                rowsLast.setPlaceOfDelivery(placeOfDelivery.get(i).getText());
                                rowsLast.setLoading(loading.get(i).getText());
                                rowsLast.setAmount(amount.get(i).getText());
                                rowsLasts.add(rowsLast);
                                if(i == num.size()-1){
                                    Map<String, List<RowsLast>> mapTd = new HashMap<>();
                                    mapTd.put(map.get("id"), rowsLasts);
                                    tdValue.setRowsLasts(mapTd);
                                    tdValue.getRowsLasts().values().stream()
                                            .map(e -> e.stream().map(ex -> ex.getPlaceOfDelivery()).reduce("", String::concat))
                                            .forEach(System.out::println);
                                    System.out.println(send);
//                                    EmailSend emailSend = new EmailSend();
//                                    emailSend.mailSend(send, logger, mapMail, "Найдены заявки");
                                }
                            }
                        }
                        setMap.put(map.get("id"),num.stream().map(WebElement::getText).collect(Collectors.toSet()));

                    }catch (NoSuchElementException | StaleElementReferenceException ex) {

                    }
                return 1;
                }
                    return 0;
            } catch (NoSuchElementException | StaleElementReferenceException e){
                try{
                    driver.findElement(By.xpath(stringTR));
                } catch (NoSuchElementException | StaleElementReferenceException ex) {
                    if (map.get("chk_apply").equals("1557")) {
                        map.put("chk_search", "");
                        map.put("chk_apply", "1559");
                        sqlDataCardDAO.updateApply(map);

                    }
                }
                return 1;
            }
        }
        else return 1;

    }


}
