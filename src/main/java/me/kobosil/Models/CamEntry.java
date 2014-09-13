package me.kobosil.Models;

import java.util.HashMap;

/**
 * Created by Roman on 13.09.2014.
 */
public class CamEntry {

    private long id = 0;
    private Double lat = 0D;
    private Double lon = 0D;
    private String name = "";
    private String operator = "";
    private String type = "";


    public CamEntry(long id, Double lat, Double lon, HashMap<String, String> tags){
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        if(tags.containsKey("name"))
            this.name = tags.get("name").replace("'", "");

        if(tags.containsKey("operator"))
            this.operator = tags.get("operator").replace("'", "");

        if(tags.containsKey("surveillance"))
            this.type = tags.get("surveillance").replace("'", "");
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "CamEntry{" +
                "id=" + id +
                ", lat=" + lat +
                ", lon=" + lon +
                ", name='" + name + '\'' +
                ", operator='" + operator + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
