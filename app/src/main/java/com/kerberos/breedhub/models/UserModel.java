package com.kerberos.breedhub.models;

import org.json.JSONException;
import org.json.JSONObject;

public class UserModel {
    private String id, photo, userid, name, email, phone, status, password;
    private int pets;
    private boolean allowPhone, allowEmail, vet;

    public boolean isVet() {
        return vet;
    }

    public void setVet(boolean vet) {
        this.vet = vet;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPhoto() {
        return photo;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public int getPets() {
        return pets;
    }

    public void setPets(int pets) {
        this.pets = pets;
    }

    public boolean isAllowEmail() {
        return allowEmail;
    }

    public void setAllowEmail(boolean allowEmail) {
        this.allowEmail = allowEmail;
    }

    public boolean isAllowPhone() {
        return allowPhone;
    }

    public void setAllowPhone(boolean allowPhone) {
        this.allowPhone = allowPhone;
    }

    @Override
    public String toString() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id",id);
            jsonObject.put("vet",vet);
            jsonObject.put("photo",photo);
            jsonObject.put("userid",userid);
            jsonObject.put("name",name);
            jsonObject.put("email",email);
            jsonObject.put("phone",phone);
            jsonObject.put("status",status);
            jsonObject.put("password",password);
            jsonObject.put("pets",pets);
            jsonObject.put("allowphone",allowPhone);
            jsonObject.put("allowemail",allowEmail);

            return jsonObject.toString(0);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
