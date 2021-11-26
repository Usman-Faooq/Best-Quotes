package com.example.bestquotes.VeriablesClasses;

public class CategoryVeriables {
    String category, category_img;

    public CategoryVeriables() {
    }

    public CategoryVeriables(String category, String category_img) {
        this.category = category;
        this.category_img = category_img;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategory_img() {
        return category_img;
    }

    public void setCategory_img(String category_img) {
        this.category_img = category_img;
    }
}
