package org.example.search;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.database.model.Categories;
import org.example.storage.impl.CategoryStorageImpl;
import redis.clients.jedis.search.Document;
import redis.clients.jedis.search.Schema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoyRediSearch extends AbstractRedisSearch<Categories> {
    private static final Logger LOG = LogManager.getLogger(CategoyRediSearch.class);
    private static final String REDIS_INDEX_CATEGORIES_PREFIX = "idx:categories";

    public CategoyRediSearch() {
        super(REDIS_INDEX_CATEGORIES_PREFIX);
    }

    @Override
    protected String getKeyPrefix() {
        return REDIS_INDEX_CATEGORIES_PREFIX;
    }

    @Override
    protected Schema getSchema() {
        return new Schema()
                .addNumericField("id")
                .addTextField("name", 1.0)
                .addTagField("url")
                .addTagField("locale")
                .addNumericField("order")
                .addNumericField("isDisplayed")
                .addNumericField("packageCount")
                .addNumericField("createdDate");
    }

    @Override
    protected List<Categories> buildData(List<Document> documents) {
        List<Categories> categoriesList = new ArrayList<>();
        for (Document document : documents) {
            try {
                Categories category = new Categories();
                category.setId(Integer.parseInt(document.get("id").toString()));
                category.setName(document.get("name").toString());
                category.setUrl(document.get("url").toString());
                category.setLocale(document.get("locale").toString());
                category.setOrder(Integer.parseInt(document.get("order").toString()));
                category.setDisplayed(Integer.parseInt(document.get("isDisplayed").toString()) == 1);
                category.setPackageCount(Integer.parseInt(document.get("packageCount").toString()));
                category.setCreatedDate(Long.parseLong(document.get("createdDate").toString()));
                categoriesList.add(category);
            } catch (Exception e) {
                LOG.error("Error building Category from document: " + e.getMessage());
            }
        }
        return categoriesList;
    }

    @Override
    public int initAllIndexing() {
        int inserted = 0;
        List<Categories> categories = CategoryStorageImpl.getAllCategories(0, 1000);
        while (categories != null && !categories.isEmpty()) {
            inserted += categories.size();
            this.indexData(categories);

            int nextOffset = inserted;
            categories = CategoryStorageImpl.getAllCategories(nextOffset, 1000);
        }
        LOG.info("Inserted {} categories", inserted);
        return inserted;
    }

    private void indexData(List<Categories> categories) {
        for (Categories category : categories) {
            Map<String, Object> fields = new HashMap<>();
            fields.put("id", category.getId());
            fields.put("name", category.getName());
            fields.put("url", category.getUrl());
            fields.put("locale", category.getLocale());
            fields.put("order", category.getOrder());
            fields.put("isDisplayed", category.getIsDisplayed() ? 1 : 0);
            fields.put("packageCount", category.getPackageCount());
            fields.put("createdDate", category.getCreatedDate());
            index(String.valueOf(category.getId()), fields);
        }
    }

    public  List<Categories> getAllCategoriesByName(String searchValue) {
        if (searchValue == null || searchValue.isEmpty()) {
            return new ArrayList<>();
        }
        //Create query to find parameters
        String query = String.format("@name:*%s* | @url:*%s* | @locale:{%s} ",
                searchValue, searchValue, searchValue);

        //Update find each field numeric if searchValue is number
        try {
            int numericValue = Integer.parseInt(searchValue);
            query += String.format("| @order:[%d %d] | @isDisplayed:[%d %d] | @packageCount:[%d %d]", numericValue, numericValue, numericValue);
        } catch (NumberFormatException e) {
            throw new NumberFormatException(e.getMessage());
        }

        LOG.info("Query getAllCategoriesByName: {}", query);
        return search(query, 0,1000,"order",true);
    }
}

