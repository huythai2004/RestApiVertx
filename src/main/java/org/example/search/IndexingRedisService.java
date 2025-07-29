package org.example.search;

import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.service.RedisService;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.resps.ScanResult;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IndexingRedisService {
    private static final Logger LOG = LogManager.getLogger(IndexingRedisService.class);
    private static final String CATEGORIES_INDEX_KEY = "markedIndex:categories:loaded";
    private static final String PACKAGES_INDEX_KEY = "markedIndex:packages:loaded";
    private static final String STICKERS_INDEX_KEY = "markedIndex:stickers:loaded";
    private final JedisPooled jedisPooled;

    @Inject
    public IndexingRedisService() {
        LOG.info("Initializing IndexingRedisService");
        this.jedisPooled = RedisService.getRedisPooled();
    }

    public JsonObject initAllIndexing() {
        boolean categoriesIndexExists = isIndexExists(CATEGORIES_INDEX_KEY);
        boolean packagesIndexExists = isIndexExists(PACKAGES_INDEX_KEY);
        boolean stickersIndexExists = isIndexExists(STICKERS_INDEX_KEY);
        JsonObject response = new JsonObject();

        if (categoriesIndexExists) {
            response.put("categories_indexed", initCategoriesIndexing());
            markIndexAsLoaded(CATEGORIES_INDEX_KEY);
            LOG.info("Categories indexing completed and marked as loaded");
        } else {
            LOG.info("Categories already indexed, skip indexing");
        }

        if (packagesIndexExists) {
            response.put("packages_indexed", initPackagesIndexing());
            markIndexAsLoaded(PACKAGES_INDEX_KEY);
            LOG.info("Packages indexing completed and marked as loaded");
        } else {
            LOG.info("Packages already indexed, skip indexing");
        }

        if (stickersIndexExists) {
            response.put("stickers_indexed", initStickersIndexing());
            markIndexAsLoaded(STICKERS_INDEX_KEY);
            LOG.info("Stickers indexing completed and marked as loaded");
        } else {
            LOG.info("Stickers already indexed, skip indexing");
        }
        return response;
    }

    // Initialize categories indexing
    public int initCategoriesIndexing() {

//        final  CategoryRediSearch categoryRediSearch = new CategoryRediSearch();
//        int result = categoryRediSearch.initAllIndexing();
//        LOG.info("Categories indexing result: {}", result);
//        return  result;

        LOG.info("Starting categories indexing...");
        try {
            final CategoryRediSearch categoryRediSearch = new CategoryRediSearch();
            int result = categoryRediSearch.initAllIndexing();
            LOG.info("Categories indexing completed with result: {}", result);
            return result;
        } catch (Exception e) {
            LOG.error("Error during categories indexing: {}", e.getMessage(), e);
            return 0;
        }
    }

    // Initialize packages indexing
    public int initPackagesIndexing() {

//        final StickerRediSearch stickerRediSearch = new StickerRediSearch();
//        int result = stickerRediSearch.initAllIndexing();
//        LOG.info("Stickers indexing result: {}", result);
//        return result;
        final PackageRediSearch packageRediSearch = new PackageRediSearch();
        int result = packageRediSearch.initAllIndexing();
        LOG.info("Packages indexing result: {}", result);
        return result;
    }

    // Initialize stickers indexing
    public int initStickersIndexing() {

//        final StickerRediSearch stickerRediSearch = new StickerRediSearch();
//        int result = stickerRediSearch.initAllIndexing();
//        LOG.info("Stickers indexing result: {}", result);
//        return result;

        LOG.info("Starting stickers indexing...");
        try {
            final StickerRediSearch stickerRediSearch = new StickerRediSearch();
            int result = stickerRediSearch.initAllIndexing();
            LOG.info("Stickers indexing completed with result: {}", result);
            return result;
        } catch (Exception e) {
            LOG.error("Error during stickers indexing: {}", e.getMessage(), e);
            return 0;
        }
    }

    //Mark the index as loaded
    private void markIndexAsLoaded(String indexKey) {
        try {
            jedisPooled.set(indexKey, "1");
        } catch (Exception e) {
            LOG.error("Error marking index as loaded");
        }
    }

    //Check if the index exists
    private boolean isIndexExists(String indexKey) {
        try {
            String value = jedisPooled.get(indexKey);
            return value == null || !value.equals("1");
        } catch (Exception e) {
            LOG.error("Error checking index existence for {}: {}", indexKey, e.getMessage());
            return true;
        }
    }

    public  void clearIndexing() {
        try {
            //Delete index loading flags
            jedisPooled.del(CATEGORIES_INDEX_KEY);
            jedisPooled.del(PACKAGES_INDEX_KEY);
            jedisPooled.del(STICKERS_INDEX_KEY);

            //Delete all indexed data
            String categoriesIndex = CATEGORIES_INDEX_KEY + ":*";
            String packagesIndex = PACKAGES_INDEX_KEY + ":*";
            String stickersIndex = STICKERS_INDEX_KEY + ":*";
            List<String> prefixes = new ArrayList<>(Arrays.asList(categoriesIndex, packagesIndex, stickersIndex));
            for (String prefix : prefixes) {
                ScanParams scanParams = new ScanParams().match(prefix).count(1000);
                String cursor = ScanParams.SCAN_POINTER_START;
                deleteIndex(jedisPooled, scanParams, cursor);
            }

            //Drop all indexes
            try {
                //jedisPooled.ftDropIndex(CATEGORIES_INDEX_KEY);
                jedisPooled.ftDropIndex("idx:categories");
            } catch (Exception e) {
                LOG.warn("Error deleting categories index: {}", e.getMessage());
            }

            try {
                //jedisPooled.ftDropIndex(PACKAGES_INDEX_KEY);
                jedisPooled.ftDropIndex("idx:packages");
            } catch (Exception e) {
                LOG.warn("Error deleting packages index: {}", e.getMessage());
            }

            try {
                //jedisPooled.ftDropIndex(STICKERS_INDEX_KEY);
                jedisPooled.ftDropIndex("idx:stickers");
            } catch (Exception e) {
                LOG.warn("Error deleting stickers index: {}", e.getMessage());
            }
        } catch (Exception e) {
            LOG.error("Error clearing indexing", e.getMessage());
        }
    }

    private void deleteIndex(JedisPooled jedisPooled, ScanParams scanParams, String cursor) {
        do {
            ScanResult<String> scanResult = jedisPooled.scan(cursor, scanParams);
            cursor = scanResult.getCursor();
            if (!scanResult.getResult().isEmpty()) {
                jedisPooled.del(scanResult.getResult().toArray(new String[0]));
            }
        } while (!cursor.equals(ScanParams.SCAN_POINTER_START));
    }
}
