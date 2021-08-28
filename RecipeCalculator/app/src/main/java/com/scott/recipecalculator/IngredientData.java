package com.scott.recipecalculator;

public class IngredientData {
    private String name;
    private float quantity;
    private String unit;        // should change this to a more relevant type

    public IngredientData (){
        name = "";
        quantity = 0;
        unit = "";
    }
    public IngredientData(String name, float quantity, String unit) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }

    public String getName() {
        return name;
    }

    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
