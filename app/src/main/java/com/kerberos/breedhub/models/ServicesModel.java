package com.kerberos.breedhub.models;

import com.kerberos.breedhub.R;

public class ServicesModel {
    private String name, color;

    private int image;

    public ServicesModel(String name, String color, int image){
        this.name = name;
        this.color = color;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public int getImage() {
        return image;
    }
}
