package org.example.storage.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.database.model.Categories;
import org.example.storage.CategoryStorage;

import java.util.ArrayList;
import java.util.List;

public class CategoryStorageImpl implements CategoryStorage {
    private static final Logger LOG = LogManager.getLogger(CategoryStorage.class);
    private static final String SELECT_FIELDS = "Select * From categories";
    private static final String SELECT_ALL = SELECT_FIELDS + "Where isShow = 1 ORDER BY `order` ASC LIMIT %s, %s";

    public static List<Categories> getAllCategories (int offset, int limit) {
        List<Categories> categoriesList = new ArrayList<>();
        try {
            String sql = String.format(SELECT_ALL,offset,limit);
            LOG.info("found {} packages for indexing", categoriesList.size());
        } catch (Exception e) {
            LOG.error("Error at getting all categories: {}", e.getMessage());
        }
        return categoriesList;
    }
}
