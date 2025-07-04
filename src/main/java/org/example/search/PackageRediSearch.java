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
        List<Packages> packages = new ArrayList<>();
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
                packages.add(pkg);
            } catch (Exception e) {
                LOG.error("Error building Package from document: " + e.getMessage());
            }
        }
        return packages;
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

    public List<Packages> getAllPackagesByName(String name) {
        List<String> conditions = new ArrayList<>();
        if (name != null && !name.isEmpty()) {
            conditions.add(String.format("@name:{%s}", name));
        }
        String query = String.join(" ", conditions);
        LOG.info("Query getAllPackageByName: {}", query);
        return search(query, 0, 1000, "order", true);
    }

    public List<Packages> getAllPackagesByCreatorName(String creatorName) {
        List<String> conditions = new ArrayList<>();
        if (creatorName != null && !creatorName.isEmpty()) {
            conditions.add(String.format("@creatorName:{%s}", creatorName));
        }
        String query = String.join(" ", conditions);
        LOG.info("Query getAllPackageByCreatorName: {}", query);
        return search(query, 0, 1000, "creatorName", true);
    }

    public List<Packages> getAllPackagesByStickerCount(int stickerCount) {
        String query = String.format("@stickerCount:[%d %d]", stickerCount, stickerCount);
        return search(query, 0, 1000, "order", true);
    }

    public List<Packages> getAllPackagesByAddWhatsApp(Boolean addWhatsApp) {
        List<String> conditions = new ArrayList<>();
        if (addWhatsApp != null) {
            conditions.add(String.format("addWhatsApp:%s", addWhatsApp ? "1" : "0"));
        }
        String query = String.join(" ", conditions);
        LOG.info("query getAllPackageByAddWhatsApp:{}", query);
        return search(query, 0, 1000, "order", true);
    }

    public List<Packages> getAllPackagesByAddTelegram(Boolean addTelegram) {
        List<String> conditions = new ArrayList<>();
        if (addTelegram != null) {
            conditions.add(String.format("addTelegram:%s", addTelegram ? "1" : "0"));
        }
        String query = String.join(" ", conditions);
        LOG.info("query getAllPackageByAddTelegram:{}", query);
        return search(query, 0, 1000, "order", true);
    }

    public List<Packages> getAllPackagesByViewCount(int viewCount) {
        String query = String.format("@viewCount:[%d %d]", viewCount, viewCount);
        return search(query, 0, 1000, "order", true);
    }

    public List<Packages> getAllPackagesByCategoryIds(int categoryId) {
        String query = String.format("@categoryIds:{%d}", categoryId);
        LOG.info("query getAllPackageByCategoryIds: {}", query);
        return search(query, 0, 1000, "order", true);
    }

    public List<Packages> getAllPackagesByLocale(String locale) {
        if (locale == null || locale.isEmpty()) {
            return new ArrayList<>();
        }
        String query = String.format("@locale:{%s}", locale);
        LOG.info("Query getAllPackagesByLocale: {}", query);
        return search(query, 0, 1000, "order", true);
    }

    public List<Packages> getAllPackagesByOrder(int order) {
        String query = String.format("@order:[%d %d]", order, order);
        LOG.info("Query getAllPackagesByOrder: {}", query);
        return search(query, 0, 1000, "order", true);
    }

    public List<Packages> getAllPackagesByIsDisplayed(boolean isDisplayed) {
        int value = isDisplayed ? 1 : 0;
        String query = String.format("@isDisplayed:[%d %d]", value, value);
        LOG.info("Query getAllPackagesByIsDisplayed: {}", query);
        return search(query, 0, 1000, "order", true);
    }

    public List<Packages> getAllPackagesByIsPremium(boolean isPremium) {
        int value = isPremium ? 1 : 0;
        String query = String.format("@isPremium:[%d %d]", value, value);
        LOG.info("Query getAllPackagesByIsPremium: {}", query);
        return search(query, 0, 1000, "order", true);
    }

    public List<Packages> getAllPackagesByIsAnimated(boolean isAnimated) {
        int value = isAnimated ? 1 : 0;
        String query = String.format("@isAnimated:[%d %d]", value, value);
        LOG.info("Query getAllPackagesByIsAnimated: {}", query);
        return search(query, 0, 1000, "order", true);
    }

    public List<Packages> getAllPackagesByCreatedDateRange(long from, long to) {
        String query = String.format("@createdDate:[%d %d]", from, to);
        LOG.info("Query getAllPackagesByCreatedDateRange: {}", query);
        return search(query, 0, 1000, "createdDate", false);
    }

}
