package com.mowares.massagerexpressclient.models;

import java.io.Serializable;

public class TypeInvoice implements Serializable {

    private String name;
    private double basePrice;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }
}
