package org.example.search;

import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.service.RedisService;
import redis.clients.jedis.JedisPooled;

import javax.inject.Inject;

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

    private void markIndexAsLoaded(String indexKey) {
        try {
            jedisPooled.set(indexKey, "1");
        } catch (Exception e) {
            LOG.error("Error marking index as loaded");
        }
    }

    private boolean isIndexExists(String indexKey) {
        try {
            String value = jedisPooled.get(indexKey);
            return value == null || !value.equals("1");
        } catch (Exception e) {
            LOG.error("Error checking index existence for {}: {}", indexKey, e.getMessage());
            return true;
        }
    }

    public int initCategoriesIndexing(){
        final  CategoyRediSearch categoyRediSearch = new CategoyRediSearch();
        int result = categoyRediSearch.initAllIndexing();
        LOG.info("Categories indexing result: {}", result);
        return  result;
    }

    public int initPackagesIndexing() {
        final PackageRediSearch packageRediSearch = new PackageRediSearch();
        int result = packageRediSearch.initAllIndexing();
        LOG.info("Packages indexing result: {}", result);
        return result;
    }

    public int initStickersIndexing() {
        final StickerRediSearch stickerRediSearch = new StickerRediSearch();
        int result = stickerRediSearch.initAllIndexing();
        LOG.info("Stickers indexing result: {}", result);
        return result;
    }

}
