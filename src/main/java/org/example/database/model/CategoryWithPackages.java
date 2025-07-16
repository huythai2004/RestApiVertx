package org.example.database.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class CategoryWithPackages {
    private int id;
    private String name;
    private String url;
    private String locale;
    private int order;

    @JsonProperty("isDisplayed")
    private boolean isDisplayed;
    private int packageCount;
    private Long createdDate;
    private List<Packages> packages;

    public CategoryWithPackages (Categories categories, List<Packages> packages) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.locale = locale;
        this.order = order;
        this.isDisplayed = isDisplayed;
        this.packageCount = packageCount;
        this.createdDate = createdDate;
        this.packages = packages;
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

    public boolean isDisplayed() {
        return isDisplayed;
    }

    public void setDisplayed(boolean displayed) {
        isDisplayed = displayed;
    }

    public int getPackageCount() {
        return packageCount;
    }

    public void setPackageCount(int packageCount) {
        this.packageCount = packageCount;
    }

    public Long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Long createdDate) {
        this.createdDate = createdDate;
    }

    public List<Packages> getPackages() {
        return packages;
    }

    public void setPackages(List<Packages> packages) {
        this.packages = packages;
    }
}
