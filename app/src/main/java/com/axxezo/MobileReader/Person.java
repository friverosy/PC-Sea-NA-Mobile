package com.axxezo.MobileReader;

/**
 * Created by Cristtopher on 13-10-16.
 */
public class Person {
    String document;
    String name;
    String nationality;
    int age;

    public Person() {

    }

    public Person(String document, String name, String nationality, Integer age){
        this.document = document;
        this.name = name;
        this.nationality = nationality;
        this.age = age;
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
}