package com.example.bestquotes.VeriablesClasses;

import com.google.firebase.Timestamp;

public class QuotesModel {
    String quote_auther, quote_by_user, quote_category, quote_content, quote_id;
    Timestamp  quote_date;
    long quote_like;

    public QuotesModel() {
    }

    public QuotesModel(String quote_content) {
        this.quote_content = quote_content;
    }

    public QuotesModel(String quote_auther, String quote_by_user, String quote_category, String quote_content, String quote_id, Timestamp quote_date, long quote_like) {
        this.quote_auther = quote_auther;
        this.quote_by_user = quote_by_user;
        this.quote_category = quote_category;
        this.quote_content = quote_content;
        this.quote_id = quote_id;
        this.quote_date = quote_date;
        this.quote_like = quote_like;
    }

    public String getQuote_auther() {
        return quote_auther;
    }

    public void setQuote_auther(String quote_auther) {
        this.quote_auther = quote_auther;
    }

    public String getQuote_by_user() {
        return quote_by_user;
    }

    public void setQuote_by_user(String quote_by_user) {
        this.quote_by_user = quote_by_user;
    }

    public String getQuote_category() {
        return quote_category;
    }

    public void setQuote_category(String quote_category) {
        this.quote_category = quote_category;
    }

    public String getQuote_content() {
        return quote_content;
    }

    public void setQuote_content(String quote_content) {
        this.quote_content = quote_content;
    }

    public String getQuote_id() {
        return quote_id;
    }

    public void setQuote_id(String quote_id) {
        this.quote_id = quote_id;
    }

    public Timestamp getQuote_date() {
        return quote_date;
    }

    public void setQuote_date(Timestamp quote_date) {
        this.quote_date = quote_date;
    }

    public long getQuote_like() {
        return quote_like;
    }

    public void setQuote_like(long quote_like) {
        this.quote_like = quote_like;
    }
}
