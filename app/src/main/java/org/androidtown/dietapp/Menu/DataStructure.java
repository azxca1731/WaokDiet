package org.androidtown.dietapp.Menu;

import org.androidtown.dietapp.DTO.FoodItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by azxca on 2017-10-05.
 */

public class DataStructure {
    private ArrayList<FoodItem> foodList;
    private ArrayList<FoodItem> searchList;
    private ArrayList<FoodItem> barcodeList;

    public DataStructure( ArrayList<FoodItem> foodList) {
        this.foodList = foodList;
        searchList=new ArrayList<FoodItem>();
        barcodeList=new ArrayList<FoodItem>();
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
        Comparator<FoodItem> barcodeComparator=new Comparator<FoodItem>(){
            @Override
            public int compare(FoodItem o1, FoodItem o2) {
                return o1.getBarcode().compareTo(o2.getBarcode());
            }
        };
        FoodItem[] changedArray=barcodeList.toArray(new FoodItem[]{});
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

    public int binarySearch(String searchedString) {
        FoodItem wantObject = new FoodItem();
        wantObject.setBarcode(searchedString);
        return Collections.binarySearch(barcodeList,wantObject);
    }

    public ArrayList<FoodItem> getBarcodeList() {
        return barcodeList;
    }
}

