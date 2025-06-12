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
            Object value = memcachedClient.get(CATEGORIES_KEY + "all");
            if (value != null) {
                // Convert JSON string to JsonArray and map to Categories list
                io.vertx.core.json.JsonArray jsonArray = new io.vertx.core.json.JsonArray(value.toString());
                List<Categories> categories = new ArrayList<>();
                for (int i = 0; i < jsonArray.size(); i++) {
                    categories.add(jsonArray.getJsonObject(i).mapTo(Categories.class));
                }
                promise.complete(categories);
            } else {
                promise.complete(new ArrayList<>());
            }
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
            for (Categories category : categories) {
                memcachedClient.set(CATEGORIES_KEY + category.getId(), EXPIRATION_TIME, Json.encode(category));
            }
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
            List<Categories> categories;
            if (allCategories != null) {
                io.vertx.core.json.JsonArray jsonArray = new io.vertx.core.json.JsonArray(allCategories.toString());
                categories = new ArrayList<>();
                for (int i = 0; i < jsonArray.size(); i++) {
                    categories.add(jsonArray.getJsonObject(i).mapTo(Categories.class));
                }
                categories.removeIf(c -> c.getId() == category.getId());
                categories.add(category);
            } else {
                categories = new ArrayList<>();
                categories.add(category);
            }
            memcachedClient.set(CATEGORIES_KEY + "all", EXPIRATION_TIME, Json.encode(categories));
            
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
            Object value = memcachedClient.get(PACKAGES_KEY + "all");
            if (value != null) {
                promise.complete((List<Packages>) value);
                return promise.future();
            }
            promise.complete(Json.decodeValue(value.toString(), List.class));
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
            Object value = memcachedClient.get(PACKAGES_KEY + packages.getId());
            if (value != null) {
                List<Packages> packagesList = Json.decodeValue(value.toString(), List.class);
                packagesList.removeIf(p -> p.getId() == packages.getId());
                packagesList.add(packages);
            }
            memcachedClient.set(PACKAGES_KEY + packages.getId(), EXPIRATION_TIME, Json.encode(packages));
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
            Object value = memcachedClient.get(STICKERS_KEY + "all");
            if (value != null) {
                promise.complete((List<Stickers>) value);
                return promise.future();
            }
            promise.complete(Json.decodeValue(value.toString(), List.class));
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
            if (value != null) {
                promise.complete();
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
            for (Stickers sticker : stickers) {
                memcachedClient.set(STICKERS_KEY + sticker.getId(), EXPIRATION_TIME, Json.encode(sticker));
            }
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
            Object value = memcachedClient.get(STICKERS_KEY + sticker.getId());
            if (value != null) {
                List<Stickers> stickersList = Json.decodeValue(value.toString(), List.class);
                stickersList.removeIf(s -> s.getId() == sticker.getId());
                stickersList.add(sticker);
            }
            memcachedClient.set(STICKERS_KEY + sticker.getId(), EXPIRATION_TIME, Json.encode(sticker));
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
            Object value = memcachedClient.get(STICKERS_KEY + "all");
            if (value != null) {
              io.vertx.core.json.JsonArray jsonArray = new io.vertx.core.json.JsonArray(getAllStickers().toString());
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