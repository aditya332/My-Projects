package com.somanibrothersservices.lokalmall;

public class CategoryModel {
    private String categoryIconLink , categoryName ;

    public CategoryModel(String categoryIconLink, String categoryName) {
        this.categoryIconLink = categoryIconLink;
        this.categoryName = categoryName;
    }

    public String getCategoryIconLink() {
        return categoryIconLink;
    }

    public String getCategoryName() {
        return categoryName;
    }
}
