package org.example.service;

import io.vertx.core.Future;
import org.example.config.MyBatisUltil;
import org.example.database.mapper.CategoriesMapper;
import org.example.database.mapper.PackagesMapper;
import org.example.database.model.Categories;
import org.example.database.model.CategoryWithPackages;
import org.example.database.model.Packages;
import org.example.search.CategoryRediSearch;
import org.example.service.cache.CacheService;

import java.util.Collections;
import java.util.List;

public class CategoriesService {
    private final CacheService cacheService;
    private final CategoriesMapper categoriesMapper;
    private final PackagesMapper packagesMapper;
    private final org.apache.ibatis.session.SqlSession sqlSession;
    private final CategoryRediSearch categoryRediSearch;

    public CategoriesService(CacheService cacheService, CategoryRediSearch categoryRediSearch) {
        this.cacheService = cacheService;
        this.sqlSession = MyBatisUltil.getSqlSessionFactory().openSession();
        this.categoriesMapper = sqlSession.getMapper(CategoriesMapper.class);
        this.packagesMapper = sqlSession.getMapper(PackagesMapper.class);
        this.categoryRediSearch = categoryRediSearch;
    }

    public Future<List<Categories>> getAllCategories() {
        // Just change this a little
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
//        List<Categories> categories = categoriesMapper.getAllCategories();
//        return Future.succeededFuture(categories != null ? categories : Collections.emptyList());
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

    public Future<List<Categories>> searchCategories(String name, String url, String locale, Integer order, Integer packageCount) {
        List<Categories> result = null;
        String searchValue = null;

        //Xác định giá trị tìm kiếm ưu tiên
        if (name != null && !name.isEmpty()) {
            searchValue = name;
        } else if (url != null && !url.isEmpty()) {
            searchValue = url;
        } else if (locale != null && !locale.isEmpty()) {
            searchValue = locale;
        } else if (order != null && order > 0) {
            searchValue = String.valueOf(order);
        } else if (packageCount != null && packageCount > 0) {
            searchValue = String.valueOf(packageCount);
        }

        //find if in redis has data looking for
        if (searchValue != null) {
            result = categoryRediSearch.getAllCategoriesByName(searchValue);
            if (result != null && !result.isEmpty()) {
                return Future.succeededFuture(result);
            }
        }

        //Fallback DB
        if (name != null && !name.isEmpty()) {
            List<Categories> categories = categoriesMapper.getCategoriesByName(name);
            if (categories != null && !categories.isEmpty()) {
                return Future.succeededFuture(categories);
            }
        } else if (url != null && !url.isEmpty()) {
            List<Categories> categories = categoriesMapper.getCategoriesByUrl(url);
            if (categories != null && !categories.isEmpty()) {
                return Future.succeededFuture(categories);
            }
        } else if (locale != null && !locale.isEmpty()) {
            List<Categories> categories = categoriesMapper.getCategoriesByLocale(locale);
            if (categories != null && !categories.isEmpty()) {
                return Future.succeededFuture(categories);
            }
        } else if (order != null && order > 0) {
            List<Categories> categories = categoriesMapper.getCategoriesByOrder(order);
            if (categories != null && !categories.isEmpty()) {
                return Future.succeededFuture(categories);
            }
        } else if (packageCount != null && packageCount > 0) {
            List<Categories> categories = categoriesMapper.getCategoriesByPackageCount(packageCount);
            if (categories != null && !categories.isEmpty()) {
                return Future.succeededFuture(categories);
            }
        }
        return Future.succeededFuture(Collections.emptyList());
    }

    // Get child package
    public Future<CategoryWithPackages> getCategoriesWithPackage(int id) {
        try {
            Categories categoriesData = categoriesMapper.getCategoriesById(id);
            if (categoriesData == null) {
                return Future.succeededFuture(null);
            }
            List<Packages> packages = packagesMapper.getPackagesByCategoryIds(id);
            CategoryWithPackages result = new CategoryWithPackages(categoriesData, packages);
            return  Future.succeededFuture(result);
        } catch (Exception e) {
            return Future.failedFuture("Failed to get category with packages: " + e.getMessage());
        }
    }
    public Future<Void> setCategory(Categories category) {
        if (category == null) {
            return Future.failedFuture("Invalid category data");
        }

        // Validate required fields
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            return Future.failedFuture("Category name is required");
        }
        if (category.getUrl() == null || category.getUrl().trim().isEmpty()) {
            return Future.failedFuture("Category URL is required");
        }
        if (category.getLocale() == null || category.getLocale().trim().isEmpty()) {
            return Future.failedFuture("Locale is required");
        }
        if (category.getUrl() == null || category.getUrl().trim().isEmpty()) {
            return Future.failedFuture("Category URL is required");
        }

        try {
            // Check if category exists
            Categories existingCategory = categoriesMapper.getCategoriesById(category.getId());
            if (existingCategory != null) {
                // Update existing category
                categoriesMapper.updateCategories(category);
            } else {
                // Insert new category
                categoriesMapper.insertCategories(category);
            }
            sqlSession.commit(); // Commit the transaction

            //Update cache
            return cacheService.setCategories(category);
        } catch (Exception e) {
            sqlSession.rollback(); // Rollback on error
            return Future.failedFuture("Failed to set category: " + e.getMessage());
        }
    }

    public Future<Void> deleteCategory(int id) {
        try {
            // Check if category exists
            Categories category = categoriesMapper.getCategoriesById(id);
            if (category == null) {
                return Future.failedFuture("Category not found with ID: " + id);
            }

            // Delete from database
            categoriesMapper.deleteCategories(id);
            System.out.println("Category deleted with ID: " + id);
            sqlSession.commit(); // Commit the transaction

            // Delete from cache
            return cacheService.deleteCategories(id);
        } catch (Exception e) {
            sqlSession.rollback(); // Rollback on error
            return Future.failedFuture("Failed to delete category with ID: " + e.getMessage());
        }
    }

    public void close() {
        if (sqlSession != null) {
            sqlSession.close();
        }
    }
}
