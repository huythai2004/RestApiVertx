package org.example.search;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.database.model.Packages;
import org.example.storage.impl.PackageStorageImpl;
import redis.clients.jedis.search.Document;
import redis.clients.jedis.search.Schema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PackageRediSearch extends AbstractRedisSearch<Packages> {
    private static final Logger LOG = LogManager.getLogger(PackageRediSearch.class);
    public static final String REDIS_INDEX_PACKAGES_PREFIX = "idx:packages";

    public PackageRediSearch() {
        super(REDIS_INDEX_PACKAGES_PREFIX);
    }

    @Override
    protected String getKeyPrefix() {
        return REDIS_INDEX_PACKAGES_PREFIX;
    }

    @Override
    protected Schema getSchema() {
        return new Schema()
                .addNumericField("id")
                .addTextField("name", 1.0)
                .addTextField("creatorName", 1.0)
                .addNumericField("stickerCount")
                .addNumericField("addWhatsApp")
                .addNumericField("addTelegram")
                .addNumericField("viewCount")
                .addTagField("categoryIds")
                .addNumericField("createdDate")
                .addTagField("locale")
                .addNumericField("order")
                .addNumericField("isDisplayed")
                .addNumericField("isPremium")
                .addNumericField("isAnimated");
    }

    @Override
    protected List<Packages> buildData(List<Document> documents) {
        List<Packages> packagesList = new ArrayList<>();
        for (Document document : documents) {
            try {
                Packages pkg = new Packages();
                pkg.setId(Integer.parseInt(document.get("id").toString()));
                pkg.setName(document.get("name").toString());
                pkg.setCreatorName(document.get("creatorName").toString());
                pkg.setStickerCount(Integer.parseInt(document.get("stickerCount").toString()));
                pkg.setAddWhatsApp(document.get("addWhatsApp").toString());
                pkg.setAddTelegram(document.get("addTelegram").toString());
                pkg.setViewCount(Integer.parseInt(document.get("viewCount").toString()));
                pkg.setCategoryIds(document.get("categoryIds").toString());
                pkg.setCreatedDate(Long.parseLong(document.get("createdDate").toString()));
                pkg.setLocale(document.get("locale").toString());
                pkg.setOrder(Integer.parseInt(document.get("order").toString()));
                pkg.setIsDisplayed(Integer.parseInt(document.get("isDisplayed").toString()) == 1);
                pkg.setIsPremium(Integer.parseInt(document.get("isPremium").toString()) == 1);
                pkg.setIsAnimated(Integer.parseInt(document.get("isAnimated").toString()) == 1);
                packagesList.add(pkg);
            } catch (Exception e) {
                LOG.error("Error building Package from document: " + e.getMessage());
            }
        }
        return packagesList;
    }

    @Override
    public int initAllIndexing() {
        int inserted = 0;
        List<Packages> packages = PackageStorageImpl.getAllPackage(0, 1000);
        while (packages != null && !packages.isEmpty()) {
            inserted += packages.size();
            this.indexData(packages);

            int nextOffet = inserted;
            packages = PackageStorageImpl.getAllPackage(nextOffet, 1000);
        }
        LOG.info("Inserted {} packages", inserted);
        return inserted;
    }

    public void indexData(List<Packages> packages) {
        for (Packages pkg : packages) {
            Map<String, Object> fields = new HashMap<>();
            fields.put("id", pkg.getId());
            fields.put("name", pkg.getName());
            fields.put("creatorName", pkg.getCreatorName());
            fields.put("stickerCount", pkg.getStickerCount());
            fields.put("addWhatsApp", pkg.getAddWhatsApp());
            fields.put("addTelegram", pkg.getAddTelegram());
            fields.put("viewCount", pkg.getViewCount());
            fields.put("categoryIds", pkg.getCategoryIds());
            fields.put("createdDate", pkg.getCreatedDate());
            fields.put("locale", pkg.getLocale());
            fields.put("order", pkg.getOrder());
            fields.put("isDisplayed", pkg.getIsDisplayed() ? 1 : 0);
            fields.put("isPremium", pkg.getIsPremium() ? 1 : 0);
            fields.put("isAnimated", pkg.getIsAnimated() ? 1 : 0);
            index(String.valueOf(pkg.getId()), fields);
        }
    }

    public List<Packages> getAllPackagesByName(String searchValue) {
        if (searchValue == null || searchValue.isEmpty()) {
            return new ArrayList<>();
        }

        // Create query to find parameters
        String query = String.format("@name:*%s* | @creatorName:*%s* | @addWhatsApp:*%s* | @addTelegram:*%s* | @categoryIds:{%s} | @locale:{%s}", 
                                   searchValue, searchValue, searchValue, searchValue, searchValue, searchValue);
        
        // update find each field numeric if searchValue is number
        try {
            int numericValue = Integer.parseInt(searchValue);
            query += String.format(" | @stickerCount:[%d %d] | @viewCount:[%d %d] | @order:[%d %d] | @isDisplayed:[%d %d] | @isPremium:[%d %d] | @isAnimated:[%d %d]", 
                                 numericValue, numericValue, numericValue, numericValue, numericValue, numericValue, 
                                 numericValue, numericValue, numericValue, numericValue, numericValue, numericValue);
        } catch (NumberFormatException e) {
            // searchValue không phải là số, bỏ qua các field numeric
        }

        LOG.info("Query getAllPackagesByName: {}", query);
        return search(query, 0, 1000, "order", true);
    }
}
