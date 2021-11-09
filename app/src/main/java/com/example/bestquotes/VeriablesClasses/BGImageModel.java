package com.example.bestquotes.VeriablesClasses;

public class BGImageModel {
    String bg;
    int id;

    public BGImageModel() {
    }

    public BGImageModel(String bg, int id) {
        this.bg = bg;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBg() {
        return bg;
    }

    public void setBg(String bg) {
        this.bg = bg;
    }
}
