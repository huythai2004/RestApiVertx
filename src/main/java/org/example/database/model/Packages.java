package org.example.database.model;

public class Packages {
    private int id;
    private String name;
    private String creatorName;
    private int stickerCount;
    private int adWhatapps, addTelegram;
    private int viewCount;
    private String categoryIds;
    private int isDisplayed;
    private Long createdDate;
    private String locale;
    private int order;
    private boolean isPremium, isAnimated;

    public Packages(Integer id, String name, String creatorName, Integer stickerCount, Integer addWhatsApp, Integer addTelegram, String categoryIds, Integer viewCount, Long createdDate, Integer isDisplayed, String locale, Integer order, Integer isPremium, Integer isAnimated) {
    }

    public Packages(String name, int id, String creatorName, int stickerCount, int adWhatapps, int addTelegram,
                    String categoryIds, int viewCount, Long createdDate, int isDisplayed, String locale, int order, boolean isPremium, boolean isAnimated) {
        this.name = name;
        this.id = id;
        this.creatorName = creatorName;
        this.stickerCount = stickerCount;
        this.adWhatapps = adWhatapps;
        this.addTelegram = addTelegram;
        this.categoryIds = categoryIds;
        this.viewCount = viewCount;
        this.createdDate = createdDate;
        this.isDisplayed = isDisplayed;
        this.locale = locale;
        this.order = order;
        this.isPremium = isPremium;
        this.isAnimated = isAnimated;
    }

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

    public int getAdWhatapps() {
        return adWhatapps;
    }

    public void setAdWhatapps(int adWhatapps) {
        this.adWhatapps = adWhatapps;
    }

    public int getStickerCount() {
        return stickerCount;
    }

    public void setStickerCount(int stickerCount) {
        this.stickerCount = stickerCount;
    }

    public int getAddTelegram() {
        return addTelegram;
    }

    public void setAddTelegram(int addTelegram) {
        this.addTelegram = addTelegram;
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

    public int getIsDisplayed() {
        return isDisplayed;
    }

    public void setIsDisplayed(int isDisplayed) {
        this.isDisplayed = isDisplayed;
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
        return "packages{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", creatorName='" + creatorName + '\'' +
                ", stickerCount=" + stickerCount +
                ", adWhatapps=" + adWhatapps +
                ", addTelegram=" + addTelegram +
                ", viewCount=" + viewCount +
                ", categoryIds='" + categoryIds + '\'' +
                ", isDisplayed=" + isDisplayed +
                ", createdDate=" + createdDate +
                ", locale='" + locale + '\'' +
                ", order=" + order +
                ", isPremium=" + isPremium +
                ", isAnimated=" + isAnimated +
                '}';
    }
}
