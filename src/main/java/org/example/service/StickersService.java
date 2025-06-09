package org.example.service;

import io.vertx.core.Future;
import org.example.config.MyBatisUltil;
import org.example.database.mapper.StickersMapper;
import org.example.database.model.Stickers;
import org.example.service.cache.CacheService;

import java.util.Collections;
import java.util.List;

public class StickersService {
    private final StickersMapper stickersMapper;
    private final CacheService cacheService;

    public StickersService(CacheService cacheService) {
        this.stickersMapper = MyBatisUltil.getSqlSessionFactory().openSession().getMapper(StickersMapper.class);
        this.cacheService = cacheService;
    }

    public Future<List<Stickers>> getAllStickers() {
        return cacheService.getAllStickers()
                .compose(cachedStickers -> {
                    if (cachedStickers != null && !cachedStickers.isEmpty()) {
                        return Future.succeededFuture(cachedStickers);
                    }
                    // If not in cache, get from database
                    List<Stickers> stickers = stickersMapper.getAllStickers();
                    if (stickers != null && !stickers.isEmpty()) {
                        return cacheService.setAllStickers(stickers)
                                .map(v -> stickers);
                    }
                    return Future.succeededFuture(Collections.emptyList());
                });
    }

    public Future<Stickers> getStickerById(int id) {
        return cacheService.getStickerById(id)
                .compose(cached -> {
                    if (cached != null) {
                        return Future.succeededFuture(cached);
                    }
                    // If not in cache, get from database
                    Stickers sticker = stickersMapper.getStickerById(id);
                    if (sticker != null) {
                        return cacheService.setSticker(sticker)
                                .map(v -> sticker);
                    }
                    return Future.succeededFuture(null);
                });
    }

    public Future<Void> setSticker(Stickers sticker) {
        if (sticker == null) {
            return Future.failedFuture("Invalid sticker data");
        }

        // Validate required fields
        if (sticker.getUrl() == null || sticker.getUrl().trim().isEmpty()) {
            return Future.failedFuture("Sticker URL is required");
        }
        if (sticker.getPackageId() <= 0) {
            return Future.failedFuture("Valid package ID is required");
        }
        if (sticker.getLocale() == null || sticker.getLocale().trim().isEmpty()) {
            return Future.failedFuture("Locale is required");
        }

        // Save to database first
        stickersMapper.insertSticker(sticker);

        // Then update cache
        return cacheService.setSticker(sticker);
    }

    public Future<Void> deleteSticker(int id) {
        // Check if sticker exists
        Stickers sticker = stickersMapper.getStickerById(id);
        if (sticker == null) {
            return Future.failedFuture("Sticker not found with id: " + id);
        }

        // Delete from database
        stickersMapper.deleteSticker(id);
        
        // Delete from cache
        return cacheService.deleteSticker(id);
    }
}
