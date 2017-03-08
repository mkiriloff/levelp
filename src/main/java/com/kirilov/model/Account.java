package com.kirilov.model;

import java.io.Serializable;

public class Account implements Serializable {

    private Integer id;
    private int balance;
    private String firstname;
    private String lastname;
//    private BigDecimal bigDecimal;

    public int getid() {
        return id;
    }

    public void setid(int id) {
        this.id = id;
    }

    public Account() {

    }

    public Account(String firstname, String lastname) {
        this.firstname = firstname;
        this.lastname = lastname;
    }

    public Account(int balance, String firstname, String lastname) {
        this.balance = balance;
        this.firstname = firstname;
        this.lastname = lastname;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
}
