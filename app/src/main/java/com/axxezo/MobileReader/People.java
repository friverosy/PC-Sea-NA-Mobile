package com.axxezo.MobileReader;

/**
 * Created by axxezo on 17/11/2016.
 */

public class People {
    private String document;
    private String name;
    private String nationality;
    private int age;
    private String mongo_documentID;

    public People() {
        //default constructor
    }
    public People(String document, String name, String nationality, int age, String mongo_documentID) {
        this.document = document;
        this.name = name;
        this.nationality = nationality;
        this.age = age;
        this.mongo_documentID=mongo_documentID;
    }
    //use this constructor in cards list


    public People(String document, String name, String nationality) {
        this.document = document;
        this.name = name;
        this.nationality = nationality;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getMongo_documentID() {
        return mongo_documentID;
    }

    public void setMongo_documentID(String mongo_documentID) {
        this.mongo_documentID = mongo_documentID;
    }

    @Override
    public String toString() {
        return "People{" +
                "document='" + document + '\'' +
                ", name='" + name + '\'' +
                ", nationality='" + nationality + '\'' +
                ", age=" + age +
                '}';
    }
}
