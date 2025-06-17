package org.example.database.model;

import java.io.Serializable;

public class Stickers implements Serializable {
    private int id;
    private String url;
    private int packageId;
    private String locale;
    private int order;
    private int viewCount;
    private Long createdDate;
    private String emojis;
    private boolean isPremium;

    // Default constructor
    public Stickers() {
    }

    // Constructor with fields
    public Stickers(int id, String url, int packageId, String locale, int order,
                   int viewCount, Long createdDate, String emojis, boolean isPremium) {
        this.id = id;
        this.url = url;
        this.packageId = packageId;
        this.locale = locale;
        this.order = order;
        this.viewCount = viewCount;
        this.createdDate = createdDate;
        this.emojis = emojis;
        this.isPremium = isPremium;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getPackageId() {
        return packageId;
    }

    public void setPackageId(int packageId) {
        this.packageId = packageId;
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

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public Long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Long createdDate) {
        this.createdDate = createdDate;
    }

    public String getEmojis() {
        return emojis;
    }

    public void setEmojis(String emojis) {
        this.emojis = emojis;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public void setPremium(boolean premium) {
        isPremium = premium;
    }

    @Override
    public String toString() {
        return "Stickers{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", packageId=" + packageId +
                ", locale='" + locale + '\'' +
                ", order=" + order +
                ", viewCount=" + viewCount +
                ", createdDate='" + createdDate + '\'' +
                ", emojis='" + emojis + '\'' +
                ", getIsPremium=" + isPremium +
                '}';
    }
} 