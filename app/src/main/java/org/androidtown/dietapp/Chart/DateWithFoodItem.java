package org.androidtown.dietapp.Chart;

import android.support.annotation.NonNull;

import org.androidtown.dietapp.DTO.FoodItem;

import java.util.ArrayList;

/**
 * Created by zidru on 2017-10-08.
 */

public class DateWithFoodItem implements Comparable<DateWithFoodItem>{
    String date;
    ArrayList<FoodItem> foods = new ArrayList<FoodItem>();

    public DateWithFoodItem(){
    }
    public DateWithFoodItem(ArrayList<FoodItem> foodItem, String Date){
        this.foods = foodItem;
        this.date = Date;
    }

    // basic utils
    public void add(FoodItem food) {
        foods.add(food);
    }
    public void clear(){
        foods.clear();
    }

    //getter and setter
    public int getCalories(){
        int total_calorie = 0;
        if(foods.isEmpty()){

        }else{
        for(int i=0; i<foods.size(); i++)
        {
            total_calorie += foods.get(i).getCalorie();
        }}
        return total_calorie;
    }
    public int getCarbo(){
        int total_carbo = 0;
        if(foods.isEmpty()){

        }else{
        for(int i=0; i<foods.size(); i++)
        {
            total_carbo += foods.get(i).getCarbohydrate();
        }}
        return total_carbo;
    }
    public int getFat(){
        int total_fat = 0;
        if(foods.isEmpty()){

        }else{
        for(int i=0; i<foods.size(); i++)
        {
            total_fat += foods.get(i).getFat();
        }}
        return total_fat;
    }
    public int getProtein(){
        int total_protein = 0;
        if(foods.isEmpty()){

        }else{
        for(int i=0; i<foods.size(); i++)
        {
            total_protein += foods.get(i).getProtein();
        }}
        return total_protein;
    }
    public void setFoods(ArrayList<FoodItem> foods) {
        this.foods = foods;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public ArrayList<FoodItem> getFoods() {
        return foods;
    }

    @Override
    public int compareTo(@NonNull DateWithFoodItem o) {
        return 0;
    }





}
