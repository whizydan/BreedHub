package com.kerberos.breedhub.models;

public class ChatListModel {
    private String id, name, image, time, lastMessage;

    public ChatListModel(String id, String name, String image , String time, String lastMessage){
        this.id = id;
        this.name = name;
        this.image = image;
        this.time = time;
        this.lastMessage = lastMessage;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
