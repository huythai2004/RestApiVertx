package org.example.database.model;

public class Stickers {
    private int id;
    private String url;
    private int packageId;
    private int order, viewCount;
    private Long createdDate;
    private String emojis;
    private boolean isPremium;

    public Stickers(Integer id, String url, Integer packageId, Integer order, Integer viewCount, Long createdDate, String emojis, Integer isPremium) {
    }
    public Stickers(int id, String url, int packageId, int order, int viewCount, Long createdDate, String emojis, boolean isPremium) {
        this.id = id;
        this.url = url;
        this.packageId = packageId;
        this.order = order;
        this.viewCount = viewCount;
        this.createdDate = createdDate;
        this.emojis = emojis;
        this.isPremium = isPremium;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public void setPremium(boolean premium) {
        isPremium = premium;
    }

    public String getEmojis() {
        return emojis;
    }

    public void setEmojis(String emojis) {
        this.emojis = emojis;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getPackageId() {
        return packageId;
    }

    public void setPackageId(int packageId) {
        this.packageId = packageId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Long createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        return "stickers{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", packageId=" + packageId +
                ", order=" + order +
                ", viewCount=" + viewCount +
                ", createdDate=" + createdDate +
                ", emojis='" + emojis + '\'' +
                ", isPremium=" + isPremium +
                '}';
    }
}
