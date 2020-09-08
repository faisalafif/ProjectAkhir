package com.faislll.projectakhir.model;

public class Comment {
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getMrcomment() {
        return mrcomment;
    }

    public void setMrcomment(String mrcomment) {
        this.mrcomment = mrcomment;
    }

    private String mrcomment;

    public Comment() {
    }

    public Comment(String text, String mrcomment) {
        this.text = text;
        this.mrcomment = mrcomment;
    }
}
