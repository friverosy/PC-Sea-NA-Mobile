package com.axxezo.MobileReader;

/**
 * Created by Cristtopher on 13-10-16.
 */
public class Person {
    int id;
    String fullname;
    String rut;
    String company;
    String cellphone;
    String workphone;
    String homephone;
    String email;
    String code;

    //Constructors
    public Person(){

    }

    public Person(String fullname, String rut, String cellphone, String company, String workphone, String homephone, String email, String code){

        this.fullname = fullname;
        this.rut = rut;
        this.company = company;
        this.cellphone = cellphone;
        this.workphone = workphone;
        this.homephone = homephone;
        this.email = email;
        this.code = code;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getRut() {
        return rut;
    }

    public void setRut(String rut) {
        this.rut = rut;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    public String getWorkphone() {
        return workphone;
    }

    public void setWorkphone(String workphone) {
        this.workphone = workphone;
    }

    public String getHomephone() {
        return homephone;
    }

    public void setHomephone(String homephone) {
        this.homephone = homephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}