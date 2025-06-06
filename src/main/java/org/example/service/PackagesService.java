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

    public PackagesService(CacheService cacheService) {
        this.cacheService = cacheService;
        this.packagesMapper = MyBatisUltil.getSqlSessionFactory().openSession().getMapper(PackagesMapper.class);
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
                    Packages pkg = packagesMapper.getPackageById(id);
                    if (pkg != null) {
                        return cacheService.setPackage(pkg)
                                .map(v -> pkg);
                    }
                    return Future.succeededFuture(null);
                });
    }

    public Future<Void> setPackage(Packages pkg) {
        if (pkg == null) {
            return Future.failedFuture("Invalid package data");
        }

        // Validate required fields
        if (pkg.getName() == null || pkg.getName().trim().isEmpty()) {
            return Future.failedFuture("Package name is required");
        }
        if (pkg.getCreatorName() == null || pkg.getCreatorName().trim().isEmpty()) {
            return Future.failedFuture("Creator name is required");
        }
        if (pkg.getLocale() == null || pkg.getLocale().trim().isEmpty()) {
            return Future.failedFuture("Locale is required");
        }
        if (pkg.getCategoryIds() == null || pkg.getCategoryIds().trim().isEmpty()) {
            return Future.failedFuture("Category IDs are required");
        }

        // Save to database first
        packagesMapper.insertPackage(pkg);

        // Then update cache
        return cacheService.setPackage(pkg);
    }

    public Future<Void> deletePackage(int id) {
        // Check if package exists
        Packages pkg = packagesMapper.getPackageById(id);
        if (pkg == null) {
            return Future.failedFuture("Package not found with id: " + id);
        }

        // Delete from database
        packagesMapper.deletePackages(id);
        
        // Delete from cache
        return cacheService.deletePackages(id);
    }
}


