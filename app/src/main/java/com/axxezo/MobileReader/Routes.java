package com.axxezo.MobileReader;

/**
 * Created by axxezo on 15/11/2016.
 */

public class Routes {

    private int ID;
    private String name;

    public Routes(int id_route,String Name) {
        ID=id_route;
        name=Name;
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
    public String toString(){
        return "ID:"+ID+","+"Nombre:"+name;
    }
}
