package org.androidtown.dietapp.Menu;

import org.androidtown.dietapp.DTO.FoodItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by azxca on 2017-10-05.
 */

public class DataStructure {
    private static DataStructure dataStructure;
    private ArrayList<FoodItem> foodList;
    private ArrayList<FoodItem> searchList;
    private ArrayList<FoodItem> barcodeList;

    private DataStructure() {
        searchList=new ArrayList<FoodItem>();
    }

    public static DataStructure getInstance(){
        if(dataStructure==null)dataStructure=new DataStructure();
        return dataStructure;
    }

    public void setFoodList(ArrayList<FoodItem> foodList) {
        this.foodList = foodList;
    }

    public void setBarcodeList(ArrayList<FoodItem> barcodeList) {
        this.barcodeList = barcodeList;
    }

    public ArrayList<FoodItem> getFoodList() {
        return foodList;
    }

    public void sort(){
        Comparator<FoodItem> foodItemComparator=new Comparator<FoodItem>(){
            @Override
            public int compare(FoodItem o1, FoodItem o2) {
                return o1.getName().compareTo(o2.getName());
            }
        };
        FoodItem[] changedArray=foodList.toArray(new FoodItem[]{});
        TimSort.sort(changedArray,foodItemComparator);
        foodList=new ArrayList<>(Arrays.asList(changedArray));
    }

    public void barcodeSort(){
        if(barcodeList.size()<2){
            return;
        }
        Comparator<FoodItem> barcodeComparator=new Comparator<FoodItem>(){
            @Override
            public int compare(FoodItem o1, FoodItem o2) {
                return o1.getBarcode().compareTo(o2.getBarcode());
            }
        };
        FoodItem[] changedArray=foodList.toArray(new FoodItem[]{});
        TimSort.sort(changedArray,barcodeComparator);
        barcodeList=new ArrayList<>(Arrays.asList(changedArray));
    }

    public ArrayList<FoodItem> search(String searchedString){
        searchList.clear();
        for(FoodItem item : foodList){
            if(item.getName().contains(searchedString))searchList.add(item);
        }
        return searchList;
    }

    public FoodItem binarySearch(String searchedString) {
        if(barcodeList.size()<1){
            return null;
        }
        int firstIndex = barcodeList.size() - 1;
        int lastIndex  = 0;
        int middleIndex;

        while(firstIndex <= lastIndex) {
            middleIndex = (firstIndex + lastIndex) / 2;

            if(searchedString.equals(barcodeList.get(middleIndex).getBarcode())) {
                return barcodeList.get(middleIndex);
            }
            else {
                if(searchedString.compareTo(barcodeList.get(middleIndex).getBarcode()) < 0) {
                    lastIndex = middleIndex - 1;
                }
                else {
                    firstIndex = middleIndex + 1;
                }
            }
        }

        return null;
    }
}

