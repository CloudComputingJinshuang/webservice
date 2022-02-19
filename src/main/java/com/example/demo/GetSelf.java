package com.example.demo;

import java.util.Date;

public class GetSelf {
    private String id;
    private String username;
    private String first_name;
    private String last_name;
    private Date account_created = new Date();
    private Date account_updated = new Date();

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAccount_created(Date account_created) {
        this.account_created = account_created;
    }

    public void setAccount_updated(Date account_updated) {
        this.account_updated = account_updated;
    }

    private Date getAccount_updated() {
        return account_updated;
    }

    private Date getAccount_created() {
        return account_created;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getUsername() {
        return username;
    }

    public String getId() {
        return id;
    }
}
