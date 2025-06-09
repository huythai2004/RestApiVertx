package org.example.database.model;

import java.io.Serializable;

public class Packages implements Serializable {
    private int id;
    private String name;
    private String creatorName;
    private int stickerCount;
    private String adWhatsApp;
    private String adTelegram;
    private int viewCount;
    private String categoryIds;
    private boolean isDisplayed;
    private String createdDate;
    private String locale;
    private int order;
    private boolean isPremium;
    private boolean isAnimated;

    // Default constructor
    public Packages() {
    }

    // Constructor with fields
    public Packages(int id, String name, String creatorName, int stickerCount, String adWhatsApp,
                   String adTelegram, int viewCount, String categoryIds, boolean isDisplayed,
                   String createdDate, String locale, int order, boolean isPremium, boolean isAnimated) {
        this.id = id;
        this.name = name;
        this.creatorName = creatorName;
        this.stickerCount = stickerCount;
        this.adWhatsApp = adWhatsApp;
        this.adTelegram = adTelegram;
        this.viewCount = viewCount;
        this.categoryIds = categoryIds;
        this.isDisplayed = isDisplayed;
        this.createdDate = createdDate;
        this.locale = locale;
        this.order = order;
        this.isPremium = isPremium;
        this.isAnimated = isAnimated;
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

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public int getStickerCount() {
        return stickerCount;
    }

    public void setStickerCount(int stickerCount) {
        this.stickerCount = stickerCount;
    }

    public String getAdWhatsApp() {
        return adWhatsApp;
    }

    public void setAdWhatsApp(String adWhatsApp) {
        this.adWhatsApp = adWhatsApp;
    }

    public String getAdTelegram() {
        return adTelegram;
    }

    public void setAdTelegram(String adTelegram) {
        this.adTelegram = adTelegram;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public String getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(String categoryIds) {
        this.categoryIds = categoryIds;
    }

    public boolean isDisplayed() {
        return isDisplayed;
    }

    public void setDisplayed(boolean displayed) {
        isDisplayed = displayed;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
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

    public boolean isPremium() {
        return isPremium;
    }

    public void setPremium(boolean premium) {
        isPremium = premium;
    }

    public boolean isAnimated() {
        return isAnimated;
    }

    public void setAnimated(boolean animated) {
        isAnimated = animated;
    }

    @Override
    public String toString() {
        return "Packages{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", creatorName='" + creatorName + '\'' +
                ", stickerCount=" + stickerCount +
                ", adWhatsApp='" + adWhatsApp + '\'' +
                ", adTelegram='" + adTelegram + '\'' +
                ", viewCount=" + viewCount +
                ", categoryIds='" + categoryIds + '\'' +
                ", isDisplayed=" + isDisplayed +
                ", createdDate='" + createdDate + '\'' +
                ", locale='" + locale + '\'' +
                ", order=" + order +
                ", isPremium=" + isPremium +
                ", isAnimated=" + isAnimated +
                '}';
    }
} 