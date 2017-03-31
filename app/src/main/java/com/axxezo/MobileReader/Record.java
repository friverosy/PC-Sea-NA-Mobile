package com.axxezo.MobileReader;

import java.util.Date;

/**
 * Created by Cristtopher Quintana on 12-08-16.
 */
public class Record {

    int id;
    String datetime;
    String person_document;
    String person_name;
    String origin;
    String destination;
    String port_registry;
    String reason;
    int ticket;
    int input;
    int sync;
    int permitted;


    //Constructors
    public Record(){

    }

    public Record(Integer id, String person_document, String person_name, String origin, String destination, String port_registry, Integer input, Integer sync, Integer permitted){

        this.id = id;
        this.person_document = person_document;
        this.person_name = person_name;
        this.origin=origin;
        this.destination=destination;
        this.port_registry = port_registry;
        this.input = input;
        this.sync = sync;
        this.permitted = permitted;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getPerson_document() {
        return person_document;
    }

    public void setPerson_document(String person_document) {
        this.person_document = person_document;
    }

    public String getPerson_name() {
        return person_name;
    }

    public void setPerson_name(String person_name) {
        this.person_name = person_name;
    }

    public String getPort_registry() {
        return port_registry;
    }

    public void setPort_registry(String port_registry) {
        this.port_registry = port_registry;
    }

    public int getInput() {
        return input;
    }

    public void setInput(int input) {
        this.input = input;
    }

    public int getSync() {
        return sync;
    }

    public void setSync(int sync) {
        this.sync = sync;
    }

    public int getPermitted() {
        return permitted;
    }

    public void setPermitted(int permitted) {
        this.permitted = permitted;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public int getTicket() {
        return ticket;
    }

    public void setTicket(int ticket) {
        this.ticket = ticket;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}