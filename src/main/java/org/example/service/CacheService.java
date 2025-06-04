package org.example.service;

import io.vertx.core.Future;
import org.example.database.model.Categories;
import org.example.database.model.Packages;
import org.example.database.model.Stickers;

public interface CacheService {

    Future<Categories> getCategories(int id);
    Future<Void> saveCategories(Categories categories);
    Future<Void> deleteCategories(int id);

    Future<Packages> getPackages(int id);
    Future<Void> savePackages(Packages packages);
    Future<Void> deletePackages(int id);

    Future<Stickers> getStickers(int id);
    Future<Void> saveStickers(Stickers stickers);
    Future<Void> deleteStickers(int id);
}
