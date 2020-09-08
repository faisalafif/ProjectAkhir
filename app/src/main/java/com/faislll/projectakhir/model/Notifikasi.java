package com.faislll.projectakhir.model;

public class Notifikasi {
    private String id_user;
    private String text;

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getIdupload() {
        return idupload;
    }

    public void setIdupload(String idupload) {
        this.idupload = idupload;
    }

    public boolean isIspost() {
        return ispost;
    }

    public void setIspost(boolean ispost) {
        this.ispost = ispost;
    }

    private String idupload;

    public Notifikasi() {
    }

    private boolean ispost;

    public Notifikasi(String userid, String text, String idupload, boolean ispost) {
        this.id_user = userid;
        this.text = text;
        this.idupload = idupload;
        this.ispost = ispost;
    }


}
