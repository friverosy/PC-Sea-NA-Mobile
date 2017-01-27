package com.axxezo.MobileReader;

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
    String port_id;
    String ship_id;
    String sailing_hour;
    String reason;
    int ticket;
    int manifest_total;
    int manifest_landed;
    int manifest_embarked;
    int manifest_pending;
    int input;
    int sync;
    int permitted;

    //Constructors
    public Record(){

    }

    public Record(Integer id, String person_document, String person_name, String origin,String destination, String port_id, String ship_id, String sailing_hour, Integer input, Integer sync, Integer permitted){

        this.id = id;
        this.person_document = person_document;
        this.person_name = person_name;
        this.origin=origin;
        this.destination=destination;
        this.port_id = port_id;
        this.ship_id = ship_id;
        this.sailing_hour = sailing_hour;
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

    public String getPort_id() {
        return port_id;
    }

    public void setPort_id(String port_id) {
        this.port_id = port_id;
    }

    public String getShip_id() {
        return ship_id;
    }

    public void setShip_id(String ship_id) {
        this.ship_id = ship_id;
    }

    public String getSailing_hour() {
        return sailing_hour;
    }

    public void setSailing_hour(String sailing_hour) {
        this.sailing_hour = sailing_hour;
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
    public int getManifest_pending() {
        return manifest_pending;
    }

    public void setManifest_pending(int manifest_pending) {
        this.manifest_pending = manifest_pending;
    }

    public int getManifest_total() {
        return manifest_total;
    }

    public void setManifest_total(int manifest_total) {
        this.manifest_total = manifest_total;
    }

    public int getManifest_landed() {
        return manifest_landed;
    }

    public void setManifest_landed(int manifest_landed) {
        this.manifest_landed = manifest_landed;
    }

    public int getManifest_embarked() {
        return manifest_embarked;
    }

    public void setManifest_embarked(int manifest_embarked) {
        this.manifest_embarked = manifest_embarked;
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