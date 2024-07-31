package com.kerberos.breedhub.models;

import java.util.List;

public class Locations {
    private String name, longitude, latitude, phone, description;
    private int image;
    private List<String> specialty;

    public  Locations(String name, String longitude, String latitude, int image, String phone, String description,List<String> specialty){
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
        this.image = image;
        this.specialty = specialty;
        this.phone = phone;
        this.description = description;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSpecialty(List<String> specialty) {
        this.specialty = specialty;
    }

    public List<String> getSpecialty() {
        return specialty;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public int getImage() {
        return image;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
}
