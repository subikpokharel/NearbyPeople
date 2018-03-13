package com.csci515.subik.peoplenearby.parsing;

/**
 * Created by subik on 3/12/18.
 */

public class Customer {
    int cus_id, age;
    String name, gender, email;

    public Customer(int cus_id, int age, String name, String gender, String email) {
        this.cus_id = cus_id;
        this.age = age;
        this.name = name;
        this.gender = gender;
        this.email = email;
    }

    public int getCus_id() {
        return cus_id;
    }

    public void setCus_id(int cus_id) {
        this.cus_id = cus_id;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
