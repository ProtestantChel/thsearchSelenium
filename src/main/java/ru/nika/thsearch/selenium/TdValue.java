package ru.nika.thsearch.selenium;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author Marat Sadretdinov
 */
@Component
public class TdValue {
    private String number;
    private String pOl;
    private String rate;
    private String search_col;
    private Map<String, List<RowsLast>> rowsLasts;

    public Map<String, List<RowsLast>> getRowsLasts() {
        return rowsLasts;
    }

    public void setRowsLasts(Map<String, List<RowsLast>> rowsLasts) {
        this.rowsLasts = rowsLasts;
    }

    public String getSearch_col() {
        return search_col;
    }

    public void setSearch_col(String search_col) {
        this.search_col = search_col;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getpOl() {
        return pOl;
    }

    public void setpOl(String pOl) {
        this.pOl = pOl;
    }
}
