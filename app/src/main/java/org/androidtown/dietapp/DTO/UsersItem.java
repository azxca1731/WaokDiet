package org.androidtown.dietapp.DTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by azxca on 2017-09-19.
 */

public class UsersItem {
    private String email;
    private String UID;
    private String name;
    private int age;
    private int weight;
    private int basicCalorie;
    private String gender;


    public UsersItem() {
    }
    public UsersItem(String email, String UID, String name, int age, int weight, int basicCalorie, String gender) {
        this.email = email;
        this.UID = UID;
        this.name = name;
        this.age = age;
        this.weight = weight;
        this.basicCalorie = basicCalorie;
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getBasicCalorie() {
        return basicCalorie;
    }

    public void setBasicCalorie(int basicCalorie) {
        this.basicCalorie = basicCalorie;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

}
