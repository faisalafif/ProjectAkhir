package com.faislll.projectakhir.model;

public class UserData {
    private String id_user;
    private String username;
    private String fullname;
    private String bio;
    private String imageUrl;

    public UserData() {
    }

    public UserData(String id_user, String username, String fullname, String bio, String imageUrl) {
        this.id_user = id_user;
        this.username = username;
        this.fullname = fullname;
        this.bio = bio;
        this.imageUrl = imageUrl;
    }

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
