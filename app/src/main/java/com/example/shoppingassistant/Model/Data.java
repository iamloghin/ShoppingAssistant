package com.example.shoppingassistant.Model;

public class Data {

    String type;
    int amount;
    String name;
    String date;
    String id;

    public Data() {

    }

    public Data(String type, int amount, String name, String date, String id) {
        this.type = type;
        this.amount = amount;
        this.name = name;
        this.date = date;
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
