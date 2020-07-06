package com.somanibrothersservices.lokalmall;

import java.util.Date;

public class MyOrderItemModel {
    private String productId ;
    private String productImage;
    private String productTitle;
    private String orderStatus , address , prevPrice ;
    private Date orderedDate ,packedDate ,deliveredDate , cancelledDate ;
    private String discountedPrice ;
    private String fullName , orderId , paymentMethod , productPrice ;
    private Long productQuantity ;
    private String userId ;
    private int rating = 0 ;
    private String deliveryPrice ;
    private boolean cancellationRequested ;

    public MyOrderItemModel(String productId, String productImage, String productTitle, String orderStatus, String address, String prevPrice, Date orderedDate, Date packedDate, Date deliveredDate, Date cancelledDate, String discountedPrice, String fullName, String orderId, String paymentMethod, String productPrice, Long productQuantity, String userId , String deliveryPrice , boolean cancellationRequested) {
        this.productId = productId;
        this.productImage = productImage;
        this.productTitle = productTitle;
        this.orderStatus = orderStatus;
        this.address = address;
        this.prevPrice = prevPrice;
        this.orderedDate = orderedDate;
        this.packedDate = packedDate;
        this.deliveredDate = deliveredDate;
        this.cancelledDate = cancelledDate;
        this.discountedPrice = discountedPrice;
        this.fullName = fullName;
        this.orderId = orderId;
        this.paymentMethod = paymentMethod;
        this.productPrice = productPrice;
        this.productQuantity = productQuantity;
        this.userId = userId;
        this.deliveryPrice = deliveryPrice ;
        this.cancellationRequested = cancellationRequested ;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
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

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPrevPrice() {
        return prevPrice;
    }

    public void setPrevPrice(String prevPrice) {
        this.prevPrice = prevPrice;
    }

    public Date getOrderedDate() {
        return orderedDate;
    }

    public void setOrderedDate(Date orderedDate) {
        this.orderedDate = orderedDate;
    }

    public Date getPackedDate() {
        return packedDate;
    }

    public void setPackedDate(Date packedDate) {
        this.packedDate = packedDate;
    }

    public Date getDeliveredDate() {
        return deliveredDate;
    }

    public void setDeliveredDate(Date deliveredDate) {
        this.deliveredDate = deliveredDate;
    }

    public Date getCancelledDate() {
        return cancelledDate;
    }

    public void setCancelledDate(Date cancelledDate) {
        this.cancelledDate = cancelledDate;
    }

    public String getDiscountedPrice() {
        return discountedPrice;
    }

    public void setDiscountedPrice(String discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public Long getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(Long productQuantity) {
        this.productQuantity = productQuantity;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getDeliveryPrice() {
        return deliveryPrice;
    }

    public void setDeliveryPrice(String deliveryPrice) {
        this.deliveryPrice = deliveryPrice;
    }

    public boolean isCancellationRequested() {
        return cancellationRequested;
    }

    public void setCancellationRequested(boolean cancellationRequested) {
        this.cancellationRequested = cancellationRequested;
    }

}
