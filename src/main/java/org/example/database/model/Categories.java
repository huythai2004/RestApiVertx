package org.example.database.model;

public class Categories {
    private int id;
    private String name;
    private String url;
    private String locale;
    private int order;
    private boolean isDisplayed;
    private String packageCount;
    private Long createdDate;

    public Categories(int id, Long createdDate, String packageCount, boolean isDisplayed, int order, String locale, String url, String name) {
        this.id = id;
        this.createdDate = createdDate;
        this.packageCount = packageCount;
        this.isDisplayed = isDisplayed;
        this.order = order;
        this.locale = locale;
        this.url = url;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Long createdDate) {
        this.createdDate = createdDate;
    }

    public boolean isDisplayed() {
        return isDisplayed;
    }

    public void setDisplayed(boolean displayed) {
        isDisplayed = displayed;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageCount() {
        return packageCount;
    }

    public void setPackageCount(String packageCount) {
        this.packageCount = packageCount;
    }

    @Override
    public String toString() {
        return "packages{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", locale='" + locale + '\'' +
                ", order=" + order +
                ", isDisplayed=" + isDisplayed +
                ", packageCount='" + packageCount + '\'' +
                ", createdDate=" + createdDate +
                '}';
    }
}
