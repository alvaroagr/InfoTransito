package com.example.infotransito;

public class User {

    private String id;
    private String name;
    private String email;
    private String imgUrl;

    public User() {
    }

    public User(String id, String name, String email, String imgUrl) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.imgUrl = imgUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
