package org.example.storage.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.database.model.Stickers;
import org.example.storage.StickerStorage;

import java.util.ArrayList;
import java.util.List;

public class StickerStorageImpl implements StickerStorage {
    private static final Logger LOG = LogManager.getLogger(StickerStorageImpl.class);
    private static final String SELECT_FIELDS = "select * from stickers";
    private static final String SELECT_ALL = SELECT_FIELDS + "Where isShow = 1 ORDER BY `order` ASC LIMIT %s, %s ";

    public static List<Stickers> getAllSticker(int offset, int limit) {
        List<Stickers> stickersList = new ArrayList<>();
        try {
            String sql = String.format(SELECT_ALL, offset, limit);
            LOG.info("found {} sticker for indexing", stickersList.size());
        } catch (Exception e) {
            LOG.error("Error at getting all stickers: {}", e.getMessage());
        }
        return  stickersList;
    }
}
