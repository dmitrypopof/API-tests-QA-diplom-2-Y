package ru.yandex.praktikum.model;

import java.util.ArrayList;

public class OrderStellar {
    private ArrayList<String> ingredients;

    public OrderStellar(ArrayList<String> ingredients) {
        this.ingredients = ingredients;
    }

    public ArrayList<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(ArrayList<String> ingredients) {
        this.ingredients = ingredients;
    }
}
