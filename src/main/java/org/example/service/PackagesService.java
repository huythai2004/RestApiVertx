package org.example.service;

import io.vertx.core.Future;
import org.example.config.MyBatisUltil;
import org.example.database.mapper.PackagesMapper;
import org.example.database.model.Packages;
import org.example.search.PackageRediSearch;
import org.example.service.cache.CacheService;

import java.util.Collections;
import java.util.List;

public class PackagesService {
    private final CacheService cacheService;
    private final PackagesMapper packagesMapper;
    private final org.apache.ibatis.session.SqlSession sqlSession;
    private final PackageRediSearch packageRediSearch;

    public PackagesService(CacheService cacheService, PackageRediSearch packageRediSearch) {
        this.cacheService = cacheService;
        this.sqlSession = MyBatisUltil.getSqlSessionFactory().openSession();
        this.packagesMapper = sqlSession.getMapper(PackagesMapper.class);
        this.packageRediSearch = packageRediSearch;
    }

    public Future<List<Packages>> getAllPackages() {
        return cacheService.getAllPackages()
                .compose(cachedPackages -> {
                    if (cachedPackages != null && !cachedPackages.isEmpty()) {
                        return Future.succeededFuture(cachedPackages);
                    }
                    // If not in cache, get from database
                    List<Packages> packages = packagesMapper.getAllPackages();
                    if (packages != null && !packages.isEmpty()) {
                        return cacheService.setAllPackages(packages)
                                .map(v -> packages);
                    }
                    return Future.succeededFuture(Collections.emptyList());
                });
    }

    public Future<Packages> getPackagesById(int id) {
        return cacheService.getPackageById(id)
                .compose(cached -> {
                    if (cached != null) {
                        return Future.succeededFuture(cached);
                    }
                    // If not in cache, get from database
                    Packages packages = packagesMapper.getPackageById(id);
                    if (packages != null) {
                        return cacheService.setPackage(packages)
                                .map(v -> packages);
                    }
                    return Future.succeededFuture(null);
                });
    }

    public Future<List<Packages>> searchPackages(String name, String creatorName, Integer stickerCount, String addWhatsApp,
                                                 String addTelegram, Integer viewCount, Integer categoryIds, String locale, Integer order) {
        List<Packages> result = null;
        String searchValue = null;

        // Confirm value to find first
        if (name != null && !name.isEmpty()) {
            searchValue = name;
        } else if (creatorName != null && !creatorName.isEmpty()) {
            searchValue = creatorName;
        } else if (stickerCount != null && stickerCount > 0) {
            searchValue = String.valueOf(stickerCount);
        } else if (addWhatsApp != null && !addWhatsApp.isEmpty()) {
            searchValue = addWhatsApp;
        } else if (addTelegram != null && !addTelegram.isEmpty()) {
            searchValue = addTelegram;
        } else if (viewCount != null && viewCount > 0) {
            searchValue = String.valueOf(viewCount);
        } else if (categoryIds != null && categoryIds > 0) {
            searchValue = String.valueOf(categoryIds);
        } else if (locale != null && !locale.isEmpty()) {
            searchValue = locale;
        } else if (order != null && order > 0) {
            searchValue = String.valueOf(order);
        }

        // Find in redis if has data
        if (searchValue != null) {
            result = packageRediSearch.getAllPackagesByName(searchValue);
            if (result != null && !result.isEmpty()) {
                return Future.succeededFuture(result);
            }
        }

        // Fallback DB - sử dụng tất cả các method có sẵn
        if (name != null && !name.isEmpty()) {
            List<Packages> packages = packagesMapper.getPackageByName(name);
            if (packages != null && !packages.isEmpty()) {
                return Future.succeededFuture(packages);
            }
        } else if (creatorName != null && !creatorName.isEmpty()) {
            List<Packages> packages = packagesMapper.getPackagesByCreatorName(creatorName);
            if (packages != null && !packages.isEmpty()) {
                return Future.succeededFuture(packages);
            }
        } else if (stickerCount != null && stickerCount > 0) {
            List<Packages> packages = packagesMapper.getPackagesByStickerCount(stickerCount);
            if (packages != null && !packages.isEmpty()) {
                return Future.succeededFuture(packages);
            }
        } else if (addWhatsApp != null && !addWhatsApp.isEmpty()) {
            List<Packages> packages = packagesMapper.getPackagesByAddWhatsApp(addWhatsApp);
            if (packages != null && !packages.isEmpty()) {
                return Future.succeededFuture(packages);
            }
        } else if (addTelegram != null && !addTelegram.isEmpty()) {
            List<Packages> packages = packagesMapper.getPackagesByAddTelegram(addTelegram);
            if (packages != null && !packages.isEmpty()) {
                return Future.succeededFuture(packages);
            }
        } else if (viewCount != null && viewCount > 0) {
            List<Packages> packages = packagesMapper.getPackagesByViewCount(viewCount);
            if (packages != null && !packages.isEmpty()) {
                return Future.succeededFuture(packages);
            }
        } else if (categoryIds != null && categoryIds > 0) {
            List<Packages> packages = packagesMapper.getPackagesByCategoryIds(categoryIds);
            if (packages != null && !packages.isEmpty()) {
                return Future.succeededFuture(packages);
            }
        } else if (locale != null && !locale.isEmpty()) {
            List<Packages> packages = packagesMapper.getPackagesByLocale(locale);
            if (packages != null && !packages.isEmpty()) {
                return Future.succeededFuture(packages);
            }
        } else if (order != null && order > 0) {
            List<Packages> packages = packagesMapper.getPackagesByOrder(order);
            if (packages != null && !packages.isEmpty()) {
                return Future.succeededFuture(packages);
            }
        }
        return Future.succeededFuture(Collections.emptyList());
    }

    public Future<Void> setPackage(Packages packages) {
        if (packages == null) {
            return Future.failedFuture("Invalid package data");
        }

        // Validate required fields
        if (packages.getName() == null || packages.getName().trim().isEmpty()) {
            return Future.failedFuture("Package name is required");
        }
        if (packages.getCreatorName() == null || packages.getCreatorName().trim().isEmpty()) {
            return Future.failedFuture("Creator name is required");
        }
        if (packages.getLocale() == null || packages.getLocale().trim().isEmpty()) {
            return Future.failedFuture("Locale is required");
        }
        if (packages.getCategoryIds() == null || packages.getCategoryIds().trim().isEmpty()) {
            return Future.failedFuture("Category IDs are required");
        }

        try {
            Packages existingPackage = packagesMapper.getPackageById(packages.getId());
            if (existingPackage != null) {
                packagesMapper.updatePackage(packages);
            } else {
                // Insert new package
                packagesMapper.insertPackage(packages);
            }
            sqlSession.commit();

            //Update cache
            return cacheService.setPackage(packages);
        } catch (Exception e) {
            sqlSession.rollback();
            return Future.failedFuture("Failed to set package: " + e.getMessage());
        }
    }

    public Future<Void> deletePackage(int id) {
        // Check if package exists
        try {
            Packages packages = packagesMapper.getPackageById(id);
            if (packages == null) {
                return Future.failedFuture("Package not found with ID: " + id);
            }
            // Delete from database
            packagesMapper.deletePackages(id);
            System.out.println("Package deleted with ID: " + id);
            sqlSession.commit();

            //delete from cached
            return cacheService.deletePackages(id);
        } catch (Exception e) {
            sqlSession.rollback();
            return Future.failedFuture("Failed to delete package: " + e.getMessage());
        }
    }
    public  void close() {
        if (sqlSession != null) {
            sqlSession.close();
        }
    }
}


