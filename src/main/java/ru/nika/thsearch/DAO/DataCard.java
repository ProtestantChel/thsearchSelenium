package ru.nika.thsearch.DAO;

import org.springframework.util.MultiValueMap;
import ru.nika.thsearch.selenium.RowsLast;
import ru.nika.thsearch.selenium.TdValue;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Marat Sadretdinov
 */

public class DataCard {
    private Integer id;
    private String car;
    private String numTask;
    private String placeOfLoading;
    private String placeOfDelivery;
    private String shipmentStart;
    private String shipmentEnd;
    private String loading;
    private String amount;
    private String onPlaceOfDelivery;
    private String num;
    private Integer chk_apply;
    private Integer col_check;
    private Integer chk_search;
    private Map<String, List<RowsLast>> rowsLasts;

    public Map<String, List<RowsLast>> getRowsLasts() {
        return rowsLasts;
    }

    public void setRowsLasts(Map<String, List<RowsLast>> rowsLasts) {
        this.rowsLasts = rowsLasts;
    }

    public DataCard(MultiValueMap<String, String> valueMap) {
        this.car = valueMap.getFirst("Car");
        this.placeOfLoading = valueMap.getFirst("PlaceOfLoading");
        this.placeOfDelivery = valueMap.getFirst("PlaceOfDelivery");
        this.shipmentStart = valueMap.getFirst("ShipmentStart");
        this.shipmentEnd = valueMap.getFirst("ShipmentEnd");
        this.loading = valueMap.getFirst("Loading");
        this.amount = valueMap.getFirst("Amount");
    }

    public Integer getId() {
        return id;
    }

    public DataCard(){

    }

    public DataCard(Integer id, String numTask) {
        this.id = id;
        this.numTask = numTask;
    }

    public DataCard(String car, String placeOfLoading, String placeOfDelivery, String shipmentStart, String shipmentEnd, String loading, String amount, String onPlaceOfDelivery, String num) {
        this.car = car;
        this.placeOfLoading = placeOfLoading;
        this.placeOfDelivery = placeOfDelivery;
        this.onPlaceOfDelivery = onPlaceOfDelivery;
        this.shipmentStart = shipmentStart;
        this.shipmentEnd = shipmentEnd;
        this.loading = loading;
        this.amount = amount;
        this.num = num;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCar() {
        return car;
    }

    public void setCar(String car) {
        this.car = car;
    }

    public String getNumTask() {
        return numTask;
    }

    public void setNumTask(String numTask) {
        this.numTask = numTask;
    }

    public String getPlaceOfLoading() {
        return placeOfLoading;
    }

    public void setPlaceOfLoading(String placeOfLoading) {
        this.placeOfLoading = placeOfLoading;
    }

    public String getPlaceOfDelivery() {
        return placeOfDelivery;
    }

    public void setPlaceOfDelivery(String placeOfDelivery) {
        this.placeOfDelivery = placeOfDelivery;
    }

    public String getShipmentStart() {
        return shipmentStart;
    }

    public void setShipmentStart(String shipmentStart) {
        this.shipmentStart = shipmentStart;
    }

    public String getShipmentEnd() {
        return shipmentEnd;
    }

    public void setShipmentEnd(String shipmentEnd) {
        this.shipmentEnd = shipmentEnd;
    }

    public String getLoading() {
        return loading;
    }

    public void setLoading(String loading) {
        this.loading = loading;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public Integer getChk_apply() {
        return chk_apply;
    }

    public void setChk_apply(Integer chk_apply) {
        this.chk_apply = chk_apply;
    }

    public Integer getCol_check() {
        return col_check;
    }

    public void setCol_check(Integer col_check) {
        this.col_check = col_check;
    }

    public Integer getChk_search() {
        return chk_search;
    }

    public String getOnPlaceOfDelivery() {
        return onPlaceOfDelivery;
    }

    public void setOnPlaceOfDelivery(String onPlaceOfDelivery) {
        this.onPlaceOfDelivery = onPlaceOfDelivery;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public void setChk_search(Integer chk_search) {
        this.chk_search = chk_search;
    }
    public void outPrint(){
        System.out.println(getCar() + getPlaceOfLoading());
    }

    public int length(){
        int j = 0;
        if (!this.placeOfDelivery.equals("")) j++;
        if (!this.onPlaceOfDelivery.equals("")) j++;
        if (!this.shipmentEnd.equals("")) j++;
        if (!this.loading.equals("")) j++;
        return j;
    }
    public List<String> listChk(){
        List<String> list = new ArrayList<>();
        if (!this.placeOfDelivery.equals("")) list.add("placeOfDelivery");
        if (!this.onPlaceOfDelivery.equals("")) list.add("onPlaceOfDelivery");
        if (!this.shipmentEnd.equals("")) list.add("shipmentEnd");
        if (!this.loading.equals("")) list.add("loading");
        return list;
    }

}
