package com.somanibrothersservices.lokalmall;

import java.util.ArrayList;

public class WishlistModel {
    private String productID ;
    private String productImage;
    private String productTitle;
    private String rating;
    private String productPrice;
    private String prevPrice;
    private boolean COD , inStock;
    private ArrayList<String> tags ;

    public WishlistModel(String productID , String productImage, String productTitle, String rating, String productPrice, String prevPrice, boolean COD , boolean inStock) {
        this.productID = productID ;
        this.productImage = productImage;
        this.productTitle = productTitle;
        this.rating = rating;
        this.productPrice = productPrice;
        this.prevPrice = prevPrice;
        this.COD = COD;
        this.inStock = inStock ;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getPrevPrice() {
        return prevPrice;
    }

    public void setPrevPrice(String prevPrice) {
        this.prevPrice = prevPrice;
    }

    public boolean isCOD() {
        return COD;
    }

    public void setCOD(boolean COD) {
        this.COD = COD;
    }

    public boolean isInStock() {
        return inStock;
    }

    public void setInStock(boolean inStock) {
        this.inStock = inStock;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

}
