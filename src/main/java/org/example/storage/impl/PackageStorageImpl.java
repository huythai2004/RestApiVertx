package org.example.storage.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.database.mapper.PackagesMapper;
import org.example.database.model.Packages;
import org.example.storage.PackageStorage;
import org.example.config.MyBatisUltil;

import java.util.ArrayList;
import java.util.List;

public class PackageStorageImpl implements PackageStorage {
    private static final Logger LOG = LogManager.getLogger(PackageStorageImpl.class);

    public static List<Packages> getAllPackage(int offset, int limit) {
        List<Packages> packagesList = new ArrayList<>();
        try {
            PackagesMapper packagesMapper = MyBatisUltil.getSqlSessionFactory().openSession().getMapper(PackagesMapper.class);
            packagesList = packagesMapper.getAllPackages();
            
            // Apply pagination manually since mapper doesn't support it
            int endIndex = Math.min(offset + limit, packagesList.size());
            if (offset < packagesList.size()) {
                packagesList = packagesList.subList(offset, endIndex);
            } else {
                packagesList = new ArrayList<>();
            }
            
            LOG.info("Found {} packages for indexing (offset: {}, limit: {})", packagesList.size(), offset, limit);
        } catch (Exception e) {
            LOG.error("Error at getting all packages: {}", e.getMessage(), e);
        }
        return packagesList;
    }
}
