package org.example.search;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.database.model.Stickers;
import org.example.storage.impl.StickerStorageImpl;
import redis.clients.jedis.search.Document;
import redis.clients.jedis.search.Schema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StickerRediSearch extends AbstractRedisSearch<Stickers> {
    private static final Logger LOG = LogManager.getLogger(StickerRediSearch.class);
    private static final String REDIS_INDEX_STICKER_PREFIX = "idx:sticker";

    public StickerRediSearch() {
        super(REDIS_INDEX_STICKER_PREFIX);
    }

    @Override
    public String getKeyPrefix() {
        return REDIS_INDEX_STICKER_PREFIX;
    }

    @Override
    protected Schema getSchema() {
        return new Schema()
                .addNumericField("id")
                .addTagField("url")
                .addNumericField("packageId")
                .addTagField("locale")
                .addNumericField("order")
                .addNumericField("viewCount")
                .addNumericField("createdDate")
                .addTagField("emojis")
                .addNumericField("isPremium");
    }

    @Override
    protected List<Stickers> buildData(List<Document> documents) {
        List<Stickers> stickersList = new ArrayList<>();
        for (Document document : documents) {
            try {
                Stickers stickers = new Stickers();
                stickers.setId(Integer.parseInt(document.get("id").toString()));
                stickers.setUrl(document.get("url").toString());
                stickers.setPackageId(Integer.parseInt(document.get("packageId").toString()));
                stickers.setLocale(document.get("locale").toString());
                stickers.setOrder(Integer.parseInt(document.get("order").toString()));
                stickers.setViewCount(Integer.parseInt(document.get("viewCount").toString()));
                stickers.setCreatedDate(Long.parseLong(document.get("createdDate").toString()));
                stickers.setEmojis(document.get("emojis").toString());
                stickers.setIsPremium(Integer.parseInt(document.get("isPremium").toString()) == 1);
                stickersList.add(stickers);
            } catch (Exception e) {
                LOG.info("Error building Sticker from document: " + e.getMessage());
            }
        }
        return stickersList;
    }

    @Override
    public int initAllIndexing() {
        int inserted = 0;
        List<Stickers> stickersList = StickerStorageImpl.getAllSticker(0, 1000);
        while (stickersList != null && !stickersList.isEmpty()) {
            inserted += stickersList.size();
            this.indexData(stickersList);

            int nextOffset = inserted;
            stickersList = StickerStorageImpl.getAllSticker(nextOffset, 1000);
        }
        LOG.info("Inserted {} sticker", inserted);
        return inserted;
    }

    public void indexData(List<Stickers> stickers) {
        for (Stickers sticker : stickers) {
            Map<String, Object> fields = new HashMap<>();
            fields.put("id", sticker.getId());
            fields.put("url", sticker.getUrl());
            fields.put("packageId", sticker.getPackageId());
            fields.put("locale", sticker.getLocale());
            fields.put("order", sticker.getOrder());
            fields.put("viewCount", sticker.getViewCount());
            fields.put("createdDate", sticker.getCreatedDate());
            if (sticker.getEmojis() != null) {
                fields.put("emojis", sticker.getEmojis());
            }
            fields.put("isPremium", sticker.getIsPremium() ? 1 : 0);
            index(String.valueOf(sticker.getId()), fields);
        }
    }

    public List<Stickers> getAllStickersByName(String searchValue) {
        if (searchValue == null || searchValue.isEmpty()) {
            return new ArrayList<>();
        }

        String query = String.format("@url:*%s* | @locale:*%s* | @emojis:*%s* |", searchValue, searchValue, searchValue);
        try {
            int numericValue = Integer.parseInt(searchValue);
            query += String.format("@order:[%d %d] | @viewCount:[%d %d] | @packageId:[%d %d] | @isPremium:[%d %d]", numericValue, numericValue, numericValue, numericValue);
        } catch (NumberFormatException e) {
            throw new NumberFormatException(e.getMessage());
        }

        LOG.info("Query getAllStickerByName: {}", query);
        return search(query, 0, 1000, "order", true);
    }
}