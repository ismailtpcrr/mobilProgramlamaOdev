package com.example.mobilprogramlamaodev;

public class Asset {
    private String name;
    private String symbol;
    private double price;
    private double change;

    public Asset(String name, String symbol, double price, double change) {
        this.name = name;
        this.symbol = symbol;
        this.price = price;
        this.change = change;
    }

    public String getName() { return name; }
    public String getSymbol() { return symbol; }
    public double getPrice() { return price; }
    public double getChange() { return change; }

    public void setPrice(double price) { this.price = price; }
    public void setChange(double change) { this.change = change; }
}