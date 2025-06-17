package org.example.database.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class Packages implements Serializable {
    private int id;
    private String name;
    private String creatorName;
    private int stickerCount;
    private String addWhatsApp;
    private String addTelegram;
    private int viewCount;
    private String categoryIds;
    private Long createdDate;
    private String locale;
    private int order;
    @JsonProperty("isDisplayed")
    private boolean isDisplayed;
    @JsonProperty("isPremium")
    private boolean isPremium;
    @JsonProperty("isAnimated")
    private boolean isAnimated;

    // Default constructor
    public Packages() {
    }

    // Constructor with fields
    public Packages(int id, String name, String creatorName, int stickerCount, String addWhatsApp,
                    String addTelegram, int viewCount, String categoryIds, boolean isDisplayed,
                    Long createdDate, String locale, int order, boolean isPremium, boolean isAnimated) {
        this.id = id;
        this.name = name;
        this.creatorName = creatorName;
        this.stickerCount = stickerCount;
        this.addWhatsApp = addWhatsApp;
        this.addTelegram = addTelegram;
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

    public String getAddWhatsApp() {
        return addWhatsApp;
    }

    public void setAddWhatsApp(String adWhatsApp) {
        this.addWhatsApp = adWhatsApp;
    }

    public String getAddTelegram() {
        return addTelegram;
    }

    public void setAddTelegram(String adTelegram) {
        this.addTelegram = adTelegram;
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

    public boolean getIsDisplayed() {
        return isDisplayed;
    }

    public void setIsDisplayed(boolean displayed) {
        isDisplayed = displayed;
    }

    public Long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Long createdDate) {
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

    public boolean getIsPremium() {
        return isPremium;
    }

    public void setIsPremium(boolean premium) {
        isPremium = premium;
    }

    public boolean getIsAnimated() {
        return isAnimated;
    }

    public void setIsAnimated(boolean animated) {
        isAnimated = animated;
    }

    @Override
    public String toString() {
        return "Packages{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", creatorName='" + creatorName + '\'' +
                ", stickerCount=" + stickerCount +
                ", addWhatsApp='" + addWhatsApp + '\'' +
                ", addTelegram='" + addTelegram + '\'' +
                ", viewCount=" + viewCount +
                ", categoryIds='" + categoryIds + '\'' +
                ", isDisplayed=" + isDisplayed +
                ", createdDate='" + createdDate + '\'' +
                ", locale='" + locale + '\'' +
                ", order=" + order +
                ",isPremium=" + isPremium +
                ", isAnimated=" + isAnimated +
                '}';
    }
} 