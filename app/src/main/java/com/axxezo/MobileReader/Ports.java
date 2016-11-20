package com.axxezo.MobileReader;

/**
 * Created by axxezo on 17/11/2016.
 */

public class Ports {

    private int id;
    private String name;

    public Ports(int id_ruta, String nombre_ruta) {
        id=id_ruta;
        name=nombre_ruta;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
