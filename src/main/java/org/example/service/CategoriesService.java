package org.example.service;

import io.vertx.core.Future;
import org.example.config.MyBatisUltil;
import org.example.database.mapper.CategoriesMapper;
import org.example.database.model.Categories;
import org.example.service.cache.CacheService;
import java.util.Collections;
import java.util.List;

public class CategoriesService {
    private final CacheService cacheService;
    private final CategoriesMapper categoriesMapper;

    public CategoriesService(CacheService cacheService) {
        this.cacheService = cacheService;
        this.categoriesMapper = MyBatisUltil.getSqlSessionFactory().openSession().getMapper(CategoriesMapper.class);
    }

    public Future<List<Categories>> getAllCategories() {
        return cacheService.getAllCategories()
                .compose(cachedCategories -> {
                    if (cachedCategories != null && !cachedCategories.isEmpty()) {
                        return Future.succeededFuture(cachedCategories);
                    }
                    // If not in cache, get from database
                    List<Categories> categories = categoriesMapper.getAllCategories();
                    if (categories != null && !categories.isEmpty()) {
                        return cacheService.setAllCategories(categories)
                                .map(v -> categories);
                    }
                    return Future.succeededFuture(Collections.emptyList());
                });
    }

    public Future<Categories> getCategoryById(int id) {
        return cacheService.getCategoriesById(id)
                .compose(cached -> {
                    if (cached != null) {
                        return Future.succeededFuture(cached);
                    }
                    // If not in cache, get from database
                    Categories category = categoriesMapper.getCategoriesById(id);
                    if (category != null) {
                        return cacheService.setCategories(category)
                                .map(v -> category);
                    }
                    return Future.succeededFuture(null);
                });
    }

    public Future<Void> setCategory(Categories category) {
        if (category == null) {
            return Future.failedFuture("Invalid category data");
        }

        // Validate required fields
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            return Future.failedFuture("Category name is required");
        }
        if (category.getLocale() == null || category.getLocale().trim().isEmpty()) {
            return Future.failedFuture("Locale is required");
        }
        if (category.getUrl() == null || category.getUrl().trim().isEmpty()) {
            return Future.failedFuture("Category URL is required");
        }

        // Save to database first
        categoriesMapper.insertCategories(category);

        // Then update cache
        return cacheService.setCategories(category);
    }

    public Future<Void> deleteCategory(int id) {
        // Check if category exists
        Categories category = categoriesMapper.getCategoriesById(id);
        if (category == null) {
            return Future.failedFuture("Category not found with id: " + id);
        }

        // Delete from database
        categoriesMapper.deleteCategories(id);
        
        // Delete from cache
        return cacheService.deleteCategories(id);
    }
}
