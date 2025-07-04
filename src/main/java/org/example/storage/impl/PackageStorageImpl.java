package org.example.storage.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.database.model.Packages;
import org.example.storage.PackageStorage;

import java.util.ArrayList;
import java.util.List;

public class PackageStorageImpl implements PackageStorage {
    private static final Logger LOG = LogManager.getLogger(PackageStorageImpl.class);
    private static final String SELECT_FIELDS = "Select * From packages";
    private static final String SELECT_ALL = SELECT_FIELDS + "Where isShow = 1 ORDER BY `order` ASC LIMIT %s, %s";

    public static List<Packages> getAllPackage(int offset, int limit) {
        List<Packages> packagesList  = new ArrayList<>();
        try {
            String sql = String.format(SELECT_ALL, offset, limit);
            LOG.info("found {} package for indexing", packagesList.size());
        } catch (Exception e) {
            LOG.error("Error at getting all packages: {}", e.getMessage());
        }
        return  packagesList;
    }

}
