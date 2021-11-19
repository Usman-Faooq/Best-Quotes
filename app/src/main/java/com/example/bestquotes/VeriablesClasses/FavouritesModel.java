package com.example.bestquotes.VeriablesClasses;

public class FavouritesModel {

    String QuoteID, UserID;

    public FavouritesModel() {
    }

    public FavouritesModel(String quoteID, String userID) {
        QuoteID = quoteID;
        UserID = userID;
    }

    public String getQuoteID() {
        return QuoteID;
    }

    public void setQuoteID(String quoteID) {
        QuoteID = quoteID;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }
}
