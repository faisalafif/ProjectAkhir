package com.faislll.projectakhir.model;

public class Postingan {
    private String idupload;
    private String gambar;
    private String pengapload;
    private String deskripsi;

    public Postingan(String idupload, String gambar, String pengapload, String deskripsi) {
        this.idupload = idupload;
        this.gambar = gambar;
        this.pengapload = pengapload;
        this.deskripsi = deskripsi;
    }

    public Postingan() {
    }


    public String getIdupload() {
        return idupload;
    }

    public void setIdupload(String idupload) {
        this.idupload = idupload;
    }

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }

    public String getPengapload() {
        return pengapload;
    }

    public void setPengapload(String pengapload) {
        this.pengapload = pengapload;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }
}
