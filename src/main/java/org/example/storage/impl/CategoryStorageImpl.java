package org.example.storage.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.database.mapper.CategoriesMapper;
import org.example.database.model.Categories;
import org.example.storage.CategoryStorage;
import org.example.config.MyBatisUltil;

import java.util.ArrayList;
import java.util.List;

public class CategoryStorageImpl implements CategoryStorage {
    private static final Logger LOG = LogManager.getLogger(CategoryStorageImpl.class);

    public static List<Categories> getAllCategories(int offset, int limit) {
        List<Categories> categoriesList = new ArrayList<>();
        try {
            LOG.info("Getting categories from database (offset: {}, limit: {})", offset, limit);
            CategoriesMapper categoriesMapper = MyBatisUltil.getSqlSessionFactory().openSession().getMapper(CategoriesMapper.class);
            categoriesList = categoriesMapper.getAllCategories();
            LOG.info("Raw categories from database: {}", categoriesList.size());
            
            // Apply pagination manually since mapper doesn't support it
            int endIndex = Math.min(offset + limit, categoriesList.size());
            if (offset < categoriesList.size()) {
                categoriesList = categoriesList.subList(offset, endIndex);
            } else {
                categoriesList = new ArrayList<>();
            }
            
            LOG.info("Found {} categories for indexing (offset: {}, limit: {})", categoriesList.size(), offset, limit);
        } catch (Exception e) {
            LOG.error("Error at getting all categories: {}", e.getMessage(), e);
        }
        return categoriesList;
    }
}
