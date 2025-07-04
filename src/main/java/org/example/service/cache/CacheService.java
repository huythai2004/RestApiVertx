package org.example.service.cache;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import org.example.database.model.Categories;
import org.example.database.model.Packages;
import org.example.database.model.Stickers;
import redis.clients.jedis.JedisPooled;

import java.util.List;

public interface CacheService {
    Vertx getVertx();
    public static JedisPooled getRedisPooled(){
        return CacheService.getRedisPooled();
    }


    //Categories
    Future<List<Categories>> getAllCategories();

    Future<Categories> getCategoriesById(int id);
    Future<Void> setAllCategories(List<Categories> categories);
    Future<Void> setCategories(Categories category);
    Future<Void> deleteCategories(int id);

    //Packages
    Future<List<Packages>> getAllPackages();
    Future<Packages> getPackageById(int id);
    Future<Void> setAllPackages(List<Packages> packages);
    Future<Void> setPackage(Packages packages);
    Future<Void> deletePackages(int id);

    //Stickers
    Future<List<Stickers>> getAllStickers();
    Future<Stickers> getStickerById(int id);
    Future<Void> setAllStickers(List<Stickers> stickers);
    Future<Void> setSticker(Stickers sticker);
    Future<Void> deleteSticker(int id);
}
