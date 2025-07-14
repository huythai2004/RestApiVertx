package org.example.service;

import io.vertx.core.Future;
import org.example.config.MyBatisUltil;
import org.example.database.mapper.StickersMapper;
import org.example.database.model.Stickers;
import org.example.search.StickerRediSearch;
import org.example.service.cache.CacheService;

import java.util.Collections;
import java.util.List;

public class StickersService {
    private final StickersMapper stickersMapper;
    private final CacheService cacheService;
    private final org.apache.ibatis.session.SqlSession sqlSession;
    private final StickerRediSearch stickerRediSearch;

    public StickersService(CacheService cacheService, StickerRediSearch stickerRediSearch) {
        this.cacheService = cacheService;
        this.sqlSession = MyBatisUltil.getSqlSessionFactory().openSession();
        this.stickersMapper = sqlSession.getMapper(StickersMapper.class);
        this.stickerRediSearch = stickerRediSearch;
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
                    Stickers stickers = stickersMapper.getStickerById(id);
                    if (stickers != null) {
                        return cacheService.setSticker(stickers)
                                .map(v -> stickers);
                    }
                    return Future.succeededFuture(null);
                });
    }

    public Future<List<Stickers>> searchStickers(String url, Integer packageId, String locale, Integer order, Integer viewCount, String emojis) {
        List<Stickers> result = null;
        String searchValue = null;

        // Confirm value to find first
         if (url != null && !url.isEmpty()) {
            searchValue = url;
        } else if (packageId != null && packageId > 0) {
            searchValue = String.valueOf(packageId);
        } else if (locale != null && !locale.isEmpty()) {
            searchValue = locale;
        } else if (order != null && order > 0) {
            searchValue = String.valueOf(order);
        } else if (viewCount != null && viewCount > 0) {
             searchValue = String.valueOf(viewCount);
        } else if (emojis != null && !emojis.isEmpty()) {
            searchValue = emojis;
        }

        // Find in redis if has data
        if (searchValue != null ) {
            result = stickerRediSearch.getAllStickersByName(searchValue);
            if (result != null && !result.isEmpty()) {
                return Future.succeededFuture(result);
            }
        }

        // Fallback DB - use all method available
        if (url != null && !url.isEmpty()) {
            List<Stickers> stickersList = stickersMapper.getStickerByUrl(url);
            if (stickersList != null && !stickersList.isEmpty()) {
                result = stickersList;
            }
        } else if (packageId != null && packageId > 0) {
            List<Stickers> stickersList = stickersMapper.getStickerByPackageId(packageId);
            if (stickersList != null && !stickersList.isEmpty()) {
                result = stickersList;
            }
        } else if (locale != null && !locale.isEmpty()) {
            List<Stickers> stickersList = stickersMapper.getStickerByLocale(locale);
            if (stickersList != null && !stickersList.isEmpty()) {
                result = stickersList;
            }
        } else if (order != null && order > 0) {
            List<Stickers> stickersList = stickersMapper.getStickerByOrder(order);
            if (stickersList != null && !stickersList.isEmpty()) {
                result = stickersList;
            }
        } else if (viewCount != null && viewCount > 0) {
            List<Stickers> stickersList = stickersMapper.getStickerByViewCount(viewCount);
            if (stickersList != null && !stickersList.isEmpty()) {
                result = stickersList;
            }
        } else if (emojis != null && !emojis.isEmpty()) {
            List<Stickers> stickersList = stickersMapper.getStickerByEmojis(emojis);
            if (stickersList != null && !stickersList.isEmpty()) {
                result = stickersList;
            }
        }
        // Nếu không có kết quả, trả về empty list
        if (result == null) {
            result = Collections.emptyList();
        }
        return Future.succeededFuture(result);
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
        if (sticker.getViewCount() < 0) {
            return Future.failedFuture("View count cannot be negative");
        }

        try {
            // Check if category exists
            Stickers existingSticker = stickersMapper.getStickerById(sticker.getId());
            if (existingSticker != null) {
                // Update existing category
                stickersMapper.updateSticker(sticker);
            } else {
                // Insert new category
                stickersMapper.insertSticker(sticker);
            }
            sqlSession.commit(); // Commit the transaction

            // Then update cache
            return cacheService.setSticker(sticker);
        } catch (Exception e) {
            sqlSession.rollback(); // Rollback on error
            return Future.failedFuture("Failed to save stickers: " + e.getMessage());
        }
    }

    public Future<Void> deleteSticker(int id) {
        // Check if sticker exists
        try {
            // Check if category exists
            Stickers stickers = stickersMapper.getStickerById(id);
            if (stickers == null) {
                return Future.failedFuture("Stickers not found with id: " + id);
            }

            // Delete from database
            stickersMapper.deleteSticker(id);
            sqlSession.commit(); // Commit the transaction

            // Delete from cache
            return cacheService.deleteSticker(id);
        } catch (Exception e) {
            sqlSession.rollback(); // Rollback on error
            return Future.failedFuture("Failed to delete stickers: " + e.getMessage());
        }
    }
}
