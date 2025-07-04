package org.example.search;

import org.example.service.RedisService;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.search.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public abstract class AbstractRedisSearch<T> {
    protected static final Logger LOG = Logger.getLogger(AbstractRedisSearch.class.getName());
    protected final JedisPooled jedisPooled;
    protected final String indexName;

    public AbstractRedisSearch(String indexName) {
        this.jedisPooled = RedisService.getRedisPooled();
        this.indexName = indexName;
        initializeIndex();
    }

    protected void initializeIndex() {
        try {
            Schema schema = getSchema();
            IndexDefinition indexDefinition = new IndexDefinition()
                    .setPrefixes(getKeyPrefix());

            jedisPooled.ftCreate(indexName, IndexOptions.defaultOptions().setDefinition(indexDefinition), schema);
        } catch (Exception e) {
            // Index might already exist
            LOG.warning("Error creating index {}: {}" + indexName + ", "+ e.getMessage());
        }
    }

    public void index(String id, Map<String, Object> fields) {
        try {
            String key = getKeyPrefix() + ":" + id;
            jedisPooled.hset(key, RediSearchUtil.toStringMap(fields));
        } catch (Exception e) {
            LOG.warning("Error creating index {}: {}" + indexName + ", "+ e.getMessage());
        }
    }

    public List<T> search(String queryString, int offset, int limit, String field, boolean value) {
        try {
            Query query = new Query(queryString)
                    .setSortBy(field, value)
                    .limit(offset, limit)
                    .dialect(2);
            SearchResult result = jedisPooled.ftSearch(indexName, query);
            return buildData(result.getDocuments());
        } catch (Exception e) {
            LOG.warning("Error searching index {}: {}" + indexName + ", "+ e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<T> search(String queryString, int offset, int limit) {
        try {
            Query query = new Query(queryString)
                    .setWithScores()
                    .limit(offset, limit)
                    .dialect(2);
            SearchResult result = jedisPooled.ftSearch(indexName, query);
            return buildData(result.getDocuments());
        } catch (Exception e) {
            LOG.warning("Error searching index {}: {}" + indexName + ", " + queryString + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<T> getData(String key) {
        try {
            SearchResult result = jedisPooled.ftSearch(indexName, new Query("*").limit(0, 1000).dialect(2));
            return buildData(result.getDocuments());
        } catch (Exception e) {
            LOG.warning("Error getting data for key {}: {}" + key + ", " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public void delete(String id) {
        try {
            String key = getKeyPrefix() + ":" + id;
            jedisPooled.del(key);
        } catch (Exception e) {
            LOG.warning("Error deleting index {}: {}" + indexName + ", " + e.getMessage());
        }
    }

    protected abstract List<T> buildData(List<Document> documents);

    protected abstract String getKeyPrefix();

    protected abstract Schema getSchema();

    public void close() {
        if (jedisPooled != null) {
            jedisPooled.close();
        }
    }

    public abstract int initAllIndexing();
}

