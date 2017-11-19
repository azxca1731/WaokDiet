package org.androidtown.dietapp.DTO;

import android.support.annotation.NonNull;

/**
 * Created by latitude7275 on 2017-09-14.
 */

public class FoodItem implements Comparable<FoodItem>{
    private String category;
    private String name;
    private int calorie;
    private int fat;
    private int carbohydrate;
    private int protein;
    private String uid;
    private int frequency;
    private String key;
    private String barcode;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public FoodItem() {
        setFrequency(0);
        key = "1";
    }

    public FoodItem(String uid,String category, String name,int calorie, int carbohydrate, int protein, int fat) {
        this.uid=uid;
        this.category = category;
        this.name = name;
        this.fat = fat;
        this.carbohydrate = carbohydrate;
        this.protein = protein;
        this.calorie = calorie;
        key="1";
        barcode="";
    }

    @Override
    public int compareTo(@NonNull FoodItem o) {
        return o.getName().compareTo(this.name);
    }

    //getter setter start

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public int getCalorie() {
        return calorie;
    }

    public void setCalorie(int calorie) {
        this.calorie = calorie;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFat() {
        return fat;
    }

    public void setFat(int fat) {
        this.fat = fat;
    }

    public int getCarbohydrate() {
        return carbohydrate;
    }

    public void setCarbohydrate(int carbohydrate) {
        this.carbohydrate = carbohydrate;
    }

    public int getProtein() {
        return protein;
    }

    public void setProtein(int protein) {
        this.protein = protein;
    }

    public String getUid()
    {
        return uid;
    }

    public void setUid(String uid)
    {
        this.uid=uid;
    }

    public void setFrequency(int Frequency){ this.frequency = Frequency; }

    public int getFrequency(){ return frequency; }

    public void plusFrequency(){ setFrequency(getFrequency()+1);}

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    //getter setter end

}
