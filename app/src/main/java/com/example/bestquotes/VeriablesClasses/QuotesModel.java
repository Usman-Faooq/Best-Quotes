package com.example.bestquotes.VeriablesClasses;

import com.google.firebase.Timestamp;

public class QuotesModel {
    String quote_author, quote_by_user, quote_category, quote_content, quote_id;
    Timestamp  quote_date;
    long quote_like;

    public QuotesModel() {
    }

    public QuotesModel(String quote_content) {
        this.quote_content = quote_content;
    }

    public QuotesModel(String quote_author, String quote_by_user, String quote_category, String quote_content, String quote_id, Timestamp quote_date, long quote_like) {
        this.quote_author = quote_author;
        this.quote_by_user = quote_by_user;
        this.quote_category = quote_category;
        this.quote_content = quote_content;
        this.quote_id = quote_id;
        this.quote_date = quote_date;
        this.quote_like = quote_like;
    }

    public String getQuote_author() {
        return quote_author;
    }

    public void setQuote_author(String quote_author) {
        this.quote_author = quote_author;
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

    public Timestamp getQuote_date() {
        return quote_date;
    }

    public void setQuote_date(Timestamp quote_date) {
        this.quote_date = quote_date;
    }

    public String getQuote_id() {
        return quote_id;
    }

    public void setQuote_id(String quote_id) {
        this.quote_id = quote_id;
    }

    public long getQuote_like() {
        return quote_like;
    }

    public void setQuote_like(long quote_like) {
        this.quote_like = quote_like;
    }
}
