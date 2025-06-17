package org.example.service;

import io.vertx.core.Future;
import org.example.config.MyBatisUltil;
import org.example.database.mapper.PackagesMapper;
import org.example.database.model.Packages;
import org.example.service.cache.CacheService;

import java.util.Collections;
import java.util.List;

public class PackagesService {
    private final CacheService cacheService;
    private final PackagesMapper packagesMapper;
    private final org.apache.ibatis.session.SqlSession sqlSession;

    public PackagesService(CacheService cacheService) {
        this.cacheService = cacheService;
        this.sqlSession = MyBatisUltil.getSqlSessionFactory().openSession();
        this.packagesMapper = sqlSession.getMapper(PackagesMapper.class);
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

    public Future<Packages> getPackageById(int id) {
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
            if(existingPackage != null) {
                packagesMapper.updatePackage(packages);
            } else  {
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
            return cacheService.deletePackages(id);
        } catch (Exception e) {
            sqlSession.rollback();
            return Future.failedFuture("Failed to delete package: " + e.getMessage());
        }
    }
}


