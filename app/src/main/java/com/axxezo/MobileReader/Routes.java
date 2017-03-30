package com.axxezo.MobileReader;

/**
 * Created by axxezo on 15/11/2016.
 */

public class Routes {

    private int ID;
    private String name;
    private String sailing_date;

    public Routes(int ID, String name, String sailing_date) {
        this.ID = ID;
        this.name = name;
        this.sailing_date = sailing_date;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSailing_date() {
        return sailing_date;
    }

    public void setSailing_date(String sailing_date) {
        this.sailing_date = sailing_date;
    }

    public String toString(){
        return "ID:"+ID+","+"Nombre:"+name;
    }
}
