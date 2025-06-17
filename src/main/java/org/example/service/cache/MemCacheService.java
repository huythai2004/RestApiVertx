package org.example.service.cache;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import net.spy.memcached.MemcachedClient;
import org.example.database.model.Categories;
import org.example.database.model.Packages;
import org.example.database.model.Stickers;

import java.util.ArrayList;
import java.util.List;

public class MemCacheService implements CacheService {
    private final MemcachedClient memcachedClient;
    private final Vertx vertx;
    private static final String CATEGORIES_KEY = "categories:";
    private static final String PACKAGES_KEY = "packages:";
    private static final String STICKERS_KEY = "stickers:";
    private static final int EXPIRATION_TIME = 3600; // 1 hour

    public MemCacheService(MemcachedClient memcachedClient, Vertx vertx) {
        this.memcachedClient = memcachedClient;
        this.vertx = vertx;
    }

    @Override
    public Vertx getVertx() {
        return vertx;
    }

    @Override
    public Future<List<Categories>> getAllCategories() {
        Promise<List<Categories>> promise = Promise.promise();
        try {
            // First, clear any invalid cache
            memcachedClient.delete(CATEGORIES_KEY + "all");

            // Get from database through service
            return Future.succeededFuture(new ArrayList<>());
        } catch (Exception e) {
            promise.fail(e);
        }
        return promise.future();
    }

    @Override
    public Future<Categories> getCategoriesById(int id) {
        Promise<Categories> promise = Promise.promise();
        try {
            Object value = memcachedClient.get(CATEGORIES_KEY + id);
            if (value == null) {
                promise.complete(null);
                return promise.future();
            }
            promise.complete(Json.decodeValue(value.toString(), Categories.class));
        } catch (Exception e) {
            promise.fail(e);
        }
        return promise.future();
    }

    @Override
    public Future<Void> setAllCategories(List<Categories> categories) {
        Promise<Void> promise = Promise.promise();
        try {
            // Clear existing cache first
            memcachedClient.delete(CATEGORIES_KEY + "all");
            
            // Store individual categories
            for (Categories category : categories) {
                memcachedClient.set(CATEGORIES_KEY + category.getId(), EXPIRATION_TIME, Json.encode(category));
            }
            
            // Store the list as a new array
            memcachedClient.set(CATEGORIES_KEY + "all", EXPIRATION_TIME, Json.encode(categories));
            promise.complete();
        } catch (Exception e) {
            promise.fail(e);
        }
        return promise.future();
    }

    @Override
    public Future<Void> setCategories(Categories category) {
        Promise<Void> promise = Promise.promise();
        try {
            // Store the single category
            memcachedClient.set(CATEGORIES_KEY + category.getId(), EXPIRATION_TIME, Json.encode(category));
            
            // Update the all categories list
            Object allCategories = memcachedClient.get(CATEGORIES_KEY + "all");
            List<Categories> categoriesList;
            if (allCategories != null) {
                try {
                    io.vertx.core.json.JsonArray jsonArray = new io.vertx.core.json.JsonArray(allCategories.toString());
                    categoriesList = new ArrayList<>();
                    for (int i = 0; i < jsonArray.size(); i++) {
                        categoriesList.add(jsonArray.getJsonObject(i).mapTo(Categories.class));
                    }
                    categoriesList.removeIf(c -> c.getId() == category.getId());
                    categoriesList.add(category);
                } catch (Exception e) {
                    // If error parsing existing cache, start fresh
                    categoriesList = new ArrayList<>();
                    categoriesList.add(category);
                }
            } else {
                categoriesList = new ArrayList<>();
                categoriesList.add(category);
            }
            memcachedClient.set(CATEGORIES_KEY + "all", EXPIRATION_TIME, Json.encode(categoriesList));
            promise.complete();
        } catch (Exception e) {
            promise.fail(e);
        }
        return promise.future();
    }

    @Override
    public Future<Void> deleteCategories(int id) {
        Promise<Void> promise = Promise.promise();
        try {
            // Delete the single category
            memcachedClient.delete(CATEGORIES_KEY + id);

            // Update the all categories list
            Object allCategories = memcachedClient.get(CATEGORIES_KEY + "all");
            if (allCategories != null) {
                io.vertx.core.json.JsonArray jsonArray = new io.vertx.core.json.JsonArray(allCategories.toString());
                List<Categories> categories = new ArrayList<>();
                for (int i = 0; i < jsonArray.size(); i++) {
                    categories.add(jsonArray.getJsonObject(i).mapTo(Categories.class));
                }
                categories.removeIf(c -> c.getId() == id);
                memcachedClient.set(CATEGORIES_KEY + "all", EXPIRATION_TIME, Json.encode(categories));
            }
            promise.complete();
        } catch (Exception e) {
            promise.fail(e);
        }
        return promise.future();
    }

    @Override
    public Future<List<Packages>> getAllPackages() {
        Promise<List<Packages>> promise = Promise.promise();
        try {
            // First, clear any invalid cache
            memcachedClient.delete(PACKAGES_KEY + "all");

            // Get from database through service
            return Future.succeededFuture(new ArrayList<>());
        } catch (Exception e) {
            promise.fail(e);
        }
        return promise.future();
    }

    @Override
    public Future<Packages> getPackageById(int id) {
        Promise<Packages> promise = Promise.promise();
        try {
            Object value = memcachedClient.get(PACKAGES_KEY + id);
            if (value == null) {
                promise.complete(null);
                return promise.future();
            }
            promise.complete(Json.decodeValue(value.toString(), Packages.class));
        } catch (Exception e) {
            promise.fail(e);
        }
        return promise.future();
    }

    @Override
    public Future<Void> setAllPackages(List<Packages> packages) {
        Promise<Void> promise = Promise.promise();
        try {
            for (Packages pack : packages) {
                memcachedClient.set(PACKAGES_KEY + pack.getId(), EXPIRATION_TIME, Json.encode(pack));
            }
            memcachedClient.set(PACKAGES_KEY + "all", EXPIRATION_TIME, Json.encode(packages));
            promise.complete();
        } catch (Exception e) {
            promise.fail(e);
        }
        return promise.future();
    }

    @Override
    public Future<Void> setPackage(Packages packages) {
        Promise<Void> promise = Promise.promise();
        try {
            memcachedClient.set(PACKAGES_KEY + packages.getId(), EXPIRATION_TIME, Json.encode(packages));
            Object allPackages = memcachedClient.get(PACKAGES_KEY + "all");
            List<Packages> packagesList;
            if (allPackages != null) {
                try {
                    io.vertx.core.json.JsonArray jsonArray = new io.vertx.core.json.JsonArray(allPackages.toString());
                    packagesList = new ArrayList<>();
                    for (int i = 0; i < jsonArray.size(); i++) {
                        packagesList.add(jsonArray.getJsonObject(i).mapTo(Packages.class));
                    }
                    packagesList.removeIf(p -> p.getId() == packages.getId());
                    packagesList.add(packages);
                    } catch (Exception e) {
                    packagesList = new ArrayList<>();
                    packagesList.add(packages);
                }
            } else {
                packagesList = new ArrayList<>();
                packagesList.add(packages);
            }
            memcachedClient.set(PACKAGES_KEY + "all", EXPIRATION_TIME, Json.encode(packagesList));
            promise.complete();
        } catch (Exception e) {
            promise.fail(e);
        }
        return promise.future();
    }

    @Override
    public Future<Void> deletePackages(int id) {
        Promise<Void> promise = Promise.promise();
        try {
            memcachedClient.delete(PACKAGES_KEY + id);
            Object allPackages = memcachedClient.get(PACKAGES_KEY + "all");
            if(allPackages != null){
                io.vertx.core.json.JsonArray jsonArray = new io.vertx.core.json.JsonArray(allPackages.toString());
                List<Packages> packagesList = new ArrayList<>();
                for (int i = 0; i < jsonArray.size(); i++) {
                    packagesList.add(jsonArray.getJsonObject(i).mapTo(Packages.class));
                }
                packagesList.removeIf(p -> p.getId() == id);
                memcachedClient.set(PACKAGES_KEY + "all", EXPIRATION_TIME, Json.encode(packagesList));
            }
            promise.complete();
        } catch (Exception e) {
            promise.fail(e);
        }
        return promise.future();
    }

    @Override
    public Future<List<Stickers>> getAllStickers() {
        Promise<List<Stickers>> promise = Promise.promise();
        try {
            memcachedClient.delete(STICKERS_KEY + "all");

            //Get from database through service
            return Future.succeededFuture(new ArrayList<>());
        } catch (Exception e) {
            promise.fail(e);
        }
        return promise.future();
    }

    @Override
    public Future<Stickers> getStickerById(int id) {
        Promise<Stickers> promise = Promise.promise();
        try {
            Object value = memcachedClient.get(STICKERS_KEY + id);
            if (value == null) {
                promise.complete(null);
                return promise.future();
            }
            promise.complete(Json.decodeValue(value.toString(), Stickers.class));
        } catch (Exception e) {
            promise.fail(e);
        }
        return promise.future();
    }

    @Override
    public Future<Void> setAllStickers(List<Stickers> stickers) {
        Promise<Void> promise = Promise.promise();
        try {
            //Clear existing cache first
            memcachedClient.delete(STICKERS_KEY + "all");

            //Store individual stickers
            for (Stickers sticker : stickers) {
                memcachedClient.set(CATEGORIES_KEY + sticker.getId(), EXPIRATION_TIME, Json.encode(sticker));
            }

            //Store the list as a new array
            memcachedClient.set(STICKERS_KEY + "all", EXPIRATION_TIME, Json.encode(stickers));
            promise.complete();
        } catch (Exception e) {
            promise.fail(e);
        }
        return promise.future();
    }

    @Override
    public Future<Void> setSticker(Stickers sticker) {
        Promise<Void> promise = Promise.promise();
        try {
            memcachedClient.set(STICKERS_KEY + sticker.getId(), EXPIRATION_TIME, Json.encode(sticker));

            //Update the all stickers list
            Object allStickers = memcachedClient.get(STICKERS_KEY + "all");
            List<Stickers> stickersList;
            if (allStickers != null) {
                try {
                    io.vertx.core.json.JsonArray jsonArray = new io.vertx.core.json.JsonArray(allStickers.toString());
                    stickersList = new ArrayList<>();
                    for (int i = 0; i < jsonArray.size(); i++) {
                        stickersList.add(jsonArray.getJsonObject(i).mapTo(Stickers.class));
                    }
                    stickersList.removeIf(s -> s.getId() == sticker.getId());
                    stickersList.add(sticker);
                } catch (Exception e) {
                    // If error parsing existing cache, start fresh
                    stickersList = new ArrayList<>();
                    stickersList.add(sticker);
                }
            } else {
                stickersList = new ArrayList<>();
                stickersList.add(sticker);
            }
            memcachedClient.set(STICKERS_KEY + "all", EXPIRATION_TIME, Json.encode(stickersList));
            promise.complete();
        } catch (Exception e) {
            promise.fail(e);
        }
        return promise.future();
    }

    @Override
    public Future<Void> deleteSticker(int id) {
        Promise<Void> promise = Promise.promise();
        try {
            memcachedClient.delete(STICKERS_KEY + id);

            // Update the all stickers list
            Object allStickers = memcachedClient.get(STICKERS_KEY + "all");
            if (allStickers != null) {
                io.vertx.core.json.JsonArray jsonArray = new io.vertx.core.json.JsonArray(allStickers.toString());
                List<Stickers> stickersList = new ArrayList<>();
                for (int i = 0; i < jsonArray.size(); i++) {
                    stickersList.add(jsonArray.getJsonObject(i).mapTo(Stickers.class));
                }
                stickersList.removeIf(s -> s.getId() == id);
                memcachedClient.set(STICKERS_KEY + "all", EXPIRATION_TIME, Json.encode(stickersList));
            }
            promise.complete();
        } catch (Exception e) {
            promise.fail(e);
        }
        return promise.future();
    }
} 