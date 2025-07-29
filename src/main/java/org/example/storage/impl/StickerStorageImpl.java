package org.example.storage.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.database.mapper.StickersMapper;
import org.example.database.model.Stickers;
import org.example.storage.StickerStorage;
import org.example.config.MyBatisUltil;

import java.util.ArrayList;
import java.util.List;

public class StickerStorageImpl implements StickerStorage {
    private static final Logger LOG = LogManager.getLogger(StickerStorageImpl.class);

    public static List<Stickers> getAllSticker(int offset, int limit) {
        List<Stickers> stickersList = new ArrayList<>();
        try {
            LOG.info("Getting stickers from database (offset: {}, limit: {})", offset, limit);
            StickersMapper stickersMapper = MyBatisUltil.getSqlSessionFactory().openSession().getMapper(StickersMapper.class);
            stickersList = stickersMapper.getAllStickers();
            LOG.info("Raw stickers from database: {}", stickersList.size());
            
            // Apply pagination manually since mapper doesn't support it
            int endIndex = Math.min(offset + limit, stickersList.size());
            if (offset < stickersList.size()) {
                stickersList = stickersList.subList(offset, endIndex);
            } else {
                stickersList = new ArrayList<>();
            }
            
            LOG.info("Found {} stickers for indexing (offset: {}, limit: {})", stickersList.size(), offset, limit);
        } catch (Exception e) {
            LOG.error("Error at getting all stickers: {}", e.getMessage(), e);
        }
        return stickersList;
    }
}
