package org.androidtown.dietapp.DTO;

/**
 * Created by latitude7275 on 2017-09-14.
 */

public class FoodItem{
    private String category;
    private String name;
    //표준 4개
    private int calorie;
    private int fat;
    private int carbohydrate;
    private int protein;
    //추가 5개
    private int sugar;
    private int natrium;
    private int cholesterol;
    private int saturatedFat;
    private int transFat;

    private String uid;

    private int frequency;
    private String key;
    private String barcode;


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
        barcode="0";
    }

    public FoodItem(String category, String name, int calorie, int fat, int carbohydrate, int protein, int sugar, int natrium, int cholesterol, int saturatedFat, int transFat, String uid) {
        this.category = category;
        this.name = name;
        this.calorie = calorie;
        this.fat = fat;
        this.carbohydrate = carbohydrate;
        this.protein = protein;
        this.sugar = sugar;
        this.natrium = natrium;
        this.cholesterol = cholesterol;
        this.saturatedFat = saturatedFat;
        this.transFat = transFat;
        this.uid = uid;
    }


    //getter & setter
    //TODO:나중에 불필요한 것들 삭제 요망
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCalorie() {
        return calorie;
    }

    public void setCalorie(int calorie) {
        this.calorie = calorie;
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

    public int getSugar() {
        return sugar;
    }

    public void setSugar(int sugar) {
        this.sugar = sugar;
    }

    public int getNatrium() {
        return natrium;
    }

    public void setNatrium(int natrium) {
        this.natrium = natrium;
    }

    public int getCholesterol() {
        return cholesterol;
    }

    public void setCholesterol(int cholesterol) {
        this.cholesterol = cholesterol;
    }

    public int getSaturatedFat() {
        return saturatedFat;
    }

    public void setSaturatedFat(int saturatedFat) {
        this.saturatedFat = saturatedFat;
    }

    public int getTransFat() {
        return transFat;
    }

    public void setTransFat(int transFat) {
        this.transFat = transFat;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void plusFrequency(){ setFrequency(getFrequency()+1);}

}
