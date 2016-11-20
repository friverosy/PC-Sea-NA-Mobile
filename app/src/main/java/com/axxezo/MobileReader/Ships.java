package com.axxezo.MobileReader;

/**
 * Created by axxezo on 17/11/2016.
 */
public class Ships {
    private int ID;
    private String Name;

    public Ships(int id_transporte, String nombre_transporte) {
        ID=id_transporte;
        Name=nombre_transporte;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}
