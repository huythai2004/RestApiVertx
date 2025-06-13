package org.example.database.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

public class Categories implements Serializable {
    private int id;
    private String name;
    private String url;
    private String locale;
    private int order;
    
    @JsonProperty("isDisplayed")
    private boolean isDisplayed;
    
    private int packageCount;
    private String createdDate;

    // Default constructor
    public Categories() {
    }

    // Constructor with fields
    public Categories(int id, String name, String url, String locale, int order, 
                     boolean isDisplayed, int packageCount, String createdDate) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.locale = locale;
        this.order = order;
        this.isDisplayed = isDisplayed;
        this.packageCount = packageCount;
        this.createdDate = createdDate;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @JsonProperty("isDisplayed")
    public boolean isDisplayed() {
        return isDisplayed;
    }

    @JsonProperty("isDisplayed")
    public void setDisplayed(boolean displayed) {
        isDisplayed = displayed;
    }

    public int getPackageCount() {
        return packageCount;
    }

    public void setPackageCount(int packageCount) {
        this.packageCount = packageCount;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        return "Categories{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", locale='" + locale + '\'' +
                ", order=" + order +
                ", isDisplayed=" + isDisplayed +
                ", packageCount=" + packageCount +
                ", createdDate='" + createdDate + '\'' +
                '}';
    }
} 