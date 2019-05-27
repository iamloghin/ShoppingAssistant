package com.example.shoppingassistant.Model;

public class Data {

    private ItemType type;
    private int amount;
    private String name;
    private String date;
    private String id;
    private Boolean checked;

    // need for FireBase.Database.Core
    public Data() {

    }

    public Data(ItemType type, int amount, String name, String date, String id, Boolean checked) {
        this.type = type;
        this.amount = amount;
        this.name = name;
        this.date = date;
        this.id = id;
        this.checked = checked;
    }

    public ItemType getType() {
        return type;
    }

    public void setType(ItemType type) {
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

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }
}
