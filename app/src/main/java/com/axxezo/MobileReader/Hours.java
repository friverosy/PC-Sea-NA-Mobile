package com.axxezo.MobileReader;

/**
 * Created by axxezo on 17/11/2016.
 */
public class Hours {
    private int ID;
    private String Hora;
    public Hours( String hora) {
       Hora=hora;
    }
    public String getName() {
        return Hora;
    }

    public void setName(String hora) {
        Hora = hora;
    }
    public String toString(){
        return ID+","+Hora;}
}
