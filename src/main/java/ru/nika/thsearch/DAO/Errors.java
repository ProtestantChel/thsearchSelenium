package ru.nika.thsearch.DAO;

/**
 * @author Marat Sadretdinov
 */
public class Errors {
    private Integer id;
    private String error;

    public Errors(){

    }
    public Errors(Integer id, String error) {
        this.id = id;
        this.error = error;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
