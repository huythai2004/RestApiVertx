package org.example.database.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class PackageWithStickers {
    private int id;
    private String name;
    private String creatorName;
    private int stickerCount;
    private String addWhatsApp;
    private String addTelegram;
    private int viewCount;
    private String categoryIds;
    @JsonProperty("isDisplayed")
    private boolean isDisplayed;
    private Long createdDate;
    private int order;
    @JsonProperty("isPremium")
    private boolean isPremium;
    @JsonProperty("isAnimated")
    private boolean isAnimated;
    private List<Stickers> stickers;

    public PackageWithStickers(Packages packages, List<Stickers> stickers) {
        this.id = packages.getId();
        this.name = packages.getName();
        this.creatorName = packages.getCreatorName();
        this.stickerCount = packages.getStickerCount();
        this.addWhatsApp = packages.getAddWhatsApp();
        this.addTelegram = packages.getAddTelegram();
        this.viewCount = packages.getViewCount();
        this.categoryIds = packages.getCategoryIds();
        this.isDisplayed = packages.getIsDisplayed();
        this.createdDate = packages.getCreatedDate();
        this.order = packages.getOrder();
        this.isPremium = packages.getIsPremium();
        this.isAnimated = packages.getIsAnimated();
        this.stickers = stickers;
    }

    // Getters & setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCreatorName() { return creatorName; }
    public void setCreatorName(String creatorName) { this.creatorName = creatorName; }
    public int getStickerCount() { return stickerCount; }
    public void setStickerCount(int stickerCount) { this.stickerCount = stickerCount; }
    public String getAddTelegram() { return addTelegram; }
    public void setAddTelegram(String addTelegram) { this.addTelegram = addTelegram; }
    public String getAddWhatsApp() { return addWhatsApp; }
    public void setAddWhatsApp(String addWhatsApp) { this.addWhatsApp = addWhatsApp; }
    public int getViewCount() { return viewCount; }
    public void setViewCount(int viewCount) { this.viewCount = viewCount; }
    public String getCategoryIds() { return categoryIds; }
    public void setCategoryIds(String categoryIds) { this.categoryIds = categoryIds; }
    public boolean getIsDisplayed() { return isDisplayed; }
    public void setIsDisplayed(boolean isDisplayed) { this.isDisplayed = isDisplayed; }
    public Long getCreatedDate() { return createdDate; }
    public void setCreatedDate(Long createdDate) { this.createdDate = createdDate; }
    public int getOrder() { return order; }
    public void setOrder(int order) { this.order = order; }
    public boolean getIsPremium() { return isPremium; }
    public void setIsPremium(boolean isPremium) { this.isPremium = isPremium; }
    public boolean getIsAnimated() { return isAnimated; }
    public void setIsAnimated(boolean isAnimated) { this.isAnimated = isAnimated; }
    public List<Stickers> getStickers() { return stickers; }
    public void setStickers(List<Stickers> stickers) { this.stickers = stickers; }
}
