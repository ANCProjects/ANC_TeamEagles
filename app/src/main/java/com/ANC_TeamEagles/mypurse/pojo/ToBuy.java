package com.ANC_TeamEagles.mypurse.pojo;

/**
 * Created by nezspencer on 8/18/17.
 */

public class ToBuy {

    private String itemName;
    private double price;
    private double whenToBuy;
    private int priority;
    private boolean bought;

    public ToBuy(String itemName, double price, double whenToBuy, int priority, boolean bought) {
        this.itemName = itemName;
        this.price = price;
        this.whenToBuy = whenToBuy;
        this.priority = priority;
        this.bought = bought;
    }

    public ToBuy() {
    }

    public String getItemName() {
        return itemName;
    }

    public double getPrice() {
        return price;
    }

    public double getWhenToBuy() {
        return whenToBuy;
    }

    public int getPriority() {
        return priority;
    }

    public boolean isBought() {
        return bought;
    }
}
