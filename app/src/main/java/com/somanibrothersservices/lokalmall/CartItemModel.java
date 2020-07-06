package com.somanibrothersservices.lokalmall;

import java.util.ArrayList;
import java.util.List;

public class CartItemModel {
    public static final int CART_ITEM = 0;
    public static final int TOTAL_AMOUNT = 1;

    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    /* **************************CART ITEM************************** */
    private String productID ;
    private String productImage;
    private String productTitle;
    private String productPrice;
    private String prevPrice;
    private Long productQuantity , maxQuantity , stockQuantity ;
    private boolean inStock ;
    private List<String > qtyIDs ;
    private boolean qtyError ;
    private String discountedPrice ;
    private boolean cod ;

    public CartItemModel(boolean cod , int type, String productID, String productImage, String productTitle, String productPrice, String prevPrice, Long productQuantity , Long maxQuantity ,Long stockQuantity , boolean inStock ) {
        this.type = type;
        this.productID = productID;
        this.productImage = productImage;
        this.productTitle = productTitle;
        this.productPrice = productPrice;
        this.prevPrice = prevPrice;
        this.productQuantity = productQuantity;
        this.maxQuantity = maxQuantity ;
        this.stockQuantity = stockQuantity ;
        this.inStock = inStock ;
        qtyIDs = new ArrayList<>() ;
        qtyError = false ;
        this.cod = cod ;
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

    public Long getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(Long productQuantity) {
        this.productQuantity = productQuantity;
    }

    public boolean getInStock() {
        return inStock;
    }

    public void setInStock(Boolean inStock) {
        this.inStock = inStock;
    }

    public Long getMaxQuantity() {
        return maxQuantity;
    }

    public void setMaxQuantity(Long maxQuantity) {
        this.maxQuantity = maxQuantity;
    }

    public List<String> getQtyIDs() {
        return qtyIDs;
    }

    public void setQtyIDs(List<String> qtyIDs) {
        this.qtyIDs = qtyIDs;
    }

    public Long getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Long stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public boolean isQtyError() {
        return qtyError;
    }

    public void setQtyError(boolean qtyError) {
        this.qtyError = qtyError;
    }

    public String getDiscountedPrice() {
        return discountedPrice;
    }

    public void setDiscountedPrice(String discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

    public boolean isCod() {
        return cod;
    }

    public void setCod(boolean cod) {
        this.cod = cod;
    }
    /* **************************CART ITEM************************** */


    /* **************************CART TOTAL************************** */

    private int totalItems , totalItemPrice , totalAmount , savedAmount ;
    private String deliveryPrice ;
    public CartItemModel(int type) {
        this.type = type;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public int getTotalItemPrice() {
        return totalItemPrice;
    }

    public void setTotalItemPrice(int totalItemPrice) {
        this.totalItemPrice = totalItemPrice;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getSavedAmount() {
        return savedAmount;
    }

    public void setSavedAmount(int savedAmount) {
        this.savedAmount = savedAmount;
    }

    public String getDeliveryPrice() {
        return deliveryPrice;
    }

    public void setDeliveryPrice(String deliveryPrice) {
        this.deliveryPrice = deliveryPrice;
    }

    /* **************************CART TOTAL************************** */
}
