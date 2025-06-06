package org.example.service.cache;

import io.vertx.core.Future;
import org.example.database.model.Categories;
import org.example.database.model.Packages;
import org.example.database.model.Stickers;

import java.util.List;

public interface CacheService {

    //Categories
    Future<List<Categories>> getAllCategories();
    Future<Categories> getCategoriesById(int id);
    Future<Void> setCategories(List<Categories> categories);
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
    Future<Void> setStickers(List<Stickers> stickers);
    Future<Void> setSticker(Stickers sticker);
    Future<Void> deleteSticker(int id);
}
