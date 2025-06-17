package org.example.service.cache;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisAPI;
import io.vertx.redis.client.RedisConnection;
import io.vertx.redis.client.Command;
import io.vertx.redis.client.Response;
import org.example.database.model.Categories;
import org.example.database.model.Packages;
import org.example.database.model.Stickers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RedisCacheService implements CacheService {
    private final Redis redis;
    private final Vertx vertx;
    private RedisAPI redisAPI;
    private static final String CATEGORIES_KEY = "categories:";
    private static final String PACKAGES_KEY = "packages:";
    private static final String STICKERS_KEY = "stickers:";
    private static final int EXPIRATION_TIME = 3600; // 1 hour in seconds

    public RedisCacheService(Redis redis, Vertx vertx) {
        this.redis = redis;
        this.vertx = vertx;
        connect();
    }

    private void connect() {
        Promise<RedisConnection> promise = Promise.promise();
        redis.connect()
                .onSuccess(conn -> {
                    redisAPI = RedisAPI.api(conn);
                    promise.complete(conn);
                })
                .onFailure(err -> {
                    System.err.println("Failed to connect to Redis: " + err.getMessage());
                    promise.fail(err);
                });
    }

    private Future<RedisAPI> getRedisAPI() {
        if (redisAPI != null) {
            return Future.succeededFuture(redisAPI);
        }
        Promise<RedisAPI> promise = Promise.promise();
        connect();
        promise.complete(redisAPI);
        return promise.future();
    }

    @Override
    public Vertx getVertx() {
        return vertx;
    }

    // Categories methods
    public Future<Response> loadFromDatabaseAndCache(List<Categories> categories) {
        categories.sort(Comparator.comparing(Categories::getName));
        return redisAPI.send(Command.SET, "categories:all", Json.encode(categories));

    }

    @Override
      public Future<List<Categories>> getAllCategories() {
            return getRedisAPI()
                    .compose(api -> {
                        Promise<List<Categories>> promise = Promise.promise();
                        api.send(Command.KEYS, CATEGORIES_KEY + "*")
                                .onSuccess(keys -> {
                                    if (keys.size() == 0) {
                                        promise.complete(new ArrayList<>());
                                        return;
                                    }
                                    List<String> keyList = new ArrayList<>();
                                    for (int i = 0; i < keys.size(); i++) {
                                        keyList.add(keys.get(i).toString());
                                    }
                                    api.send(Command.MGET, keyList.toArray(new String[0]))
                                            .onSuccess(values -> {
                                                List<Categories> categories = new ArrayList<>();
                                                for (int i = 0; i < values.size(); i++) {
                                                    String value = values.get(i).toString();
                                                    if (value != null && !value.equals("null")) {
                                                        try {
                                                            categories.add(Json.decodeValue(value, Categories.class));
                                                        } catch (Exception e) {
                                                            System.err.println("Error decoding category: " + e.getMessage());
                                                        }
                                                    }
                                                }

                                                // Sắp xếp theo ID
                                                categories.sort(Comparator.comparing(Categories::getId));

                                                promise.complete(categories);
                                            })
                                            .onFailure(err -> {
                                                System.err.println("Error getting categories from Redis: " + err.getMessage());
                                                promise.fail(err);
                                            });
                                })
                                .onFailure(err -> {
                                    System.err.println("Error getting category keys from Redis: " + err.getMessage());
                                    promise.fail(err);
                                });
                        return promise.future();
                    });

                    }


    @Override
    public Future<Categories> getCategoriesById(int id) {
        return getRedisAPI()
                .compose(api -> {
                    Promise<Categories> promise = Promise.promise();
                    api.send(Command.GET, CATEGORIES_KEY + id)
                            .onSuccess(value -> {
                                if (value == null || value.toString().equals("null")) {
                                    promise.complete(null);
                                    return;
                                }
                                try {
                                    promise.complete(Json.decodeValue(value.toString(), Categories.class));
                                } catch (Exception e) {
                                    System.err.println("Error decoding category: " + e.getMessage());
                                    promise.fail(e);
                                }
                            })
                            .onFailure(err -> {
                                System.err.println("Error getting category from Redis: " + err.getMessage());
                                promise.fail(err);
                            });
                    return promise.future();
                });
    }

    @Override
    public Future<Void> setAllCategories(List<Categories> categories) {
        if (categories == null || categories.isEmpty()) {
            return Future.succeededFuture();
        }

        return getRedisAPI()
                .compose(api -> {
                    Promise<Void> promise = Promise.promise();
                    List<Future<Void>> futures = new ArrayList<>();

                    for (Categories category : categories) {
                        String key = CATEGORIES_KEY + category.getId();
                        String value = Json.encode(category);
                        futures.add(api.send(Command.SET, key, value)
                                .compose(v -> api.send(Command.EXPIRE, key, String.valueOf(EXPIRATION_TIME)))
                                .mapEmpty());
                    }

                    Future.all(futures)
                            .onSuccess(x -> promise.complete())
                            .onFailure(err -> {
                                System.err.println("Error setting categories in Redis: " + err.getMessage());
                                promise.fail(err);
                            });
                    return promise.future();
                });
    }

    @Override
    public Future<Void> setCategories(Categories category) {
        if (category == null) {
            return Future.failedFuture("Category cannot be null");
        }

        return getRedisAPI()
                .compose(api -> {
                    Promise<Void> promise = Promise.promise();
                    String key = CATEGORIES_KEY + category.getId();
                    String value = Json.encode(category);

                    api.send(Command.SET, key, value)
                            .compose(v -> api.send(Command.EXPIRE, key, String.valueOf(EXPIRATION_TIME)))
                            .onSuccess(v -> promise.complete())
                            .onFailure(err -> {
                                System.err.println("Error setting category in Redis: " + err.getMessage());
                                promise.fail(err);
                            });
                    return promise.future();
                });
    }

    @Override
    public Future<Void> deleteCategories(int id) {
        return getRedisAPI()
                .compose(api -> {
                    Promise<Void> promise = Promise.promise();
                    api.send(Command.DEL, CATEGORIES_KEY + id)
                            .onSuccess(v -> promise.complete())
                            .onFailure(err -> {
                                System.err.println("Error deleting category from Redis: " + err.getMessage());
                                promise.fail(err);
                            });
                    return promise.future();
                });
    }

    // Packages methods
    @Override
    public Future<List<Packages>> getAllPackages() {
        return getRedisAPI()
                .compose(api -> {
                    Promise<List<Packages>> promise = Promise.promise();
                    api.send(Command.KEYS, PACKAGES_KEY + "*")
                            .onSuccess(keys -> {
                                if (keys.size() == 0) {
                                    promise.complete(new ArrayList<>());
                                    return;
                                }
                                List<String> keyList = new ArrayList<>();
                                for (int i = 0; i < keys.size(); i++) {
                                    keyList.add(keys.get(i).toString());
                                }
                                api.send(Command.MGET, keyList.toArray(new String[0]))
                                        .onSuccess(values -> {
                                            List<Packages> packages = new ArrayList<>();
                                            for (int i = 0; i < values.size(); i++) {
                                                String value = values.get(i).toString();
                                                if (value != null && !value.equals("null")) {
                                                    try {
                                                        packages.add(Json.decodeValue(value, Packages.class));
                                                    } catch (Exception e) {
                                                        System.err.println("Error decoding package: " + e.getMessage());
                                                    }
                                                }
                                            }
                                            packages.sort(Comparator.comparing(Packages::getId));
                                            promise.complete(packages);
                                        })
                                        .onFailure(err -> {
                                            System.err.println("Error getting packages from Redis: " + err.getMessage());
                                            promise.fail(err);
                                        });
                            })
                            .onFailure(err -> {
                                System.err.println("Error getting package keys from Redis: " + err.getMessage());
                                promise.fail(err);
                            });
                    return promise.future();
                });
    }

    @Override
    public Future<Packages> getPackageById(int id) {
        return getRedisAPI()
                .compose(api -> {
                    Promise<Packages> promise = Promise.promise();
                    api.send(Command.GET, PACKAGES_KEY + id)
                            .onSuccess(value -> {
                                if (value == null || value.toString().equals("null")) {
                                    promise.complete(null);
                                    return;
                                }
                                try {
                                    promise.complete(Json.decodeValue(value.toString(), Packages.class));
                                } catch (Exception e) {
                                    System.err.println("Error decoding package: " + e.getMessage());
                                    promise.fail(e);
                                }
                            })
                            .onFailure(err -> {
                                System.err.println("Error getting package from Redis: " + err.getMessage());
                                promise.fail(err);
                            });
                    return promise.future();
                });
    }

    @Override
    public Future<Void> setAllPackages(List<Packages> packages) {
        if (packages == null || packages.isEmpty()) {
            return Future.succeededFuture();
        }

        return getRedisAPI()
                .compose(api -> {
                    Promise<Void> promise = Promise.promise();
                    List<Future<Void>> futures = new ArrayList<>();

                    for (Packages pkg : packages) {
                        String key = PACKAGES_KEY + pkg.getId();
                        String value = Json.encode(pkg);
                        futures.add(api.send(Command.SET, key, value)
                                .compose(v -> api.send(Command.EXPIRE, key, String.valueOf(EXPIRATION_TIME)))
                                .mapEmpty());
                    }

                    Future.all(futures)
                            .onSuccess(x -> promise.complete())
                            .onFailure(err -> {
                                System.err.println("Error setting packages in Redis: " + err.getMessage());
                                promise.fail(err);
                            });
                    return promise.future();
                });
    }

    @Override
    public Future<Void> setPackage(Packages pkg) {
        if (pkg == null) {
            return Future.failedFuture("Package cannot be null");
        }

        return getRedisAPI()
                .compose(api -> {
                    Promise<Void> promise = Promise.promise();
                    String key = PACKAGES_KEY + pkg.getId();
                    String value = Json.encode(pkg);

                    api.send(Command.SET, key, value)
                            .compose(v -> api.send(Command.EXPIRE, key, String.valueOf(EXPIRATION_TIME)))
                            .onSuccess(v -> promise.complete())
                            .onFailure(err -> {
                                System.err.println("Error setting package in Redis: " + err.getMessage());
                                promise.fail(err);
                            });
                    return promise.future();
                });
    }

    @Override
    public Future<Void> deletePackages(int id) {
        return getRedisAPI()
                .compose(api -> {
                    Promise<Void> promise = Promise.promise();
                    api.send(Command.DEL, PACKAGES_KEY + id)
                            .onSuccess(v -> promise.complete())
                            .onFailure(err -> {
                                System.err.println("Error deleting package from Redis: " + err.getMessage());
                                promise.fail(err);
                            });
                    return promise.future();
                });
    }

    // Stickers methods
    @Override
    public Future<List<Stickers>> getAllStickers() {
        return getRedisAPI()
                .compose(api -> {
                    Promise<List<Stickers>> promise = Promise.promise();
                    api.send(Command.KEYS, STICKERS_KEY + "*")
                            .onSuccess(keys -> {
                                if (keys.size() == 0) {
                                    promise.complete(new ArrayList<>());
                                    return;
                                }
                                List<String> keyList = new ArrayList<>();
                                for (int i = 0; i < keys.size(); i++) {
                                    keyList.add(keys.get(i).toString());
                                }
                                api.send(Command.MGET, keyList.toArray(new String[0]))
                                        .onSuccess(values -> {
                                            List<Stickers> stickers = new ArrayList<>();
                                            for (int i = 0; i < values.size(); i++) {
                                                String value = values.get(i).toString();
                                                if (value != null && !value.equals("null")) {
                                                    try {
                                                        stickers.add(Json.decodeValue(value, Stickers.class));
                                                    } catch (Exception e) {
                                                        System.err.println("Error decoding sticker: " + e.getMessage());
                                                    }
                                                }
                                            }
                                            stickers.sort(Comparator.comparing(Stickers::getId));
                                            promise.complete(stickers);
                                        })
                                        .onFailure(err -> {
                                            System.err.println("Error getting stickers from Redis: " + err.getMessage());
                                            promise.fail(err);
                                        });
                            })
                            .onFailure(err -> {
                                System.err.println("Error getting sticker keys from Redis: " + err.getMessage());
                                promise.fail(err);
                            });
                    return promise.future();
                });
    }

    @Override
    public Future<Stickers> getStickerById(int id) {
        return getRedisAPI()
                .compose(api -> {
                    Promise<Stickers> promise = Promise.promise();
                    api.send(Command.GET, STICKERS_KEY + id)
                            .onSuccess(value -> {
                                if (value == null || value.toString().equals("null")) {
                                    promise.complete(null);
                                    return;
                                }
                                try {
                                    promise.complete(Json.decodeValue(value.toString(), Stickers.class));
                                } catch (Exception e) {
                                    System.err.println("Error decoding sticker: " + e.getMessage());
                                    promise.fail(e);
                                }
                            })
                            .onFailure(err -> {
                                System.err.println("Error getting sticker from Redis: " + err.getMessage());
                                promise.fail(err);
                            });
                    return promise.future();
                });
    }

    @Override
    public Future<Void> setAllStickers(List<Stickers> stickers) {
        if (stickers == null || stickers.isEmpty()) {
            return Future.succeededFuture();
        }

        return getRedisAPI()
                .compose(api -> {
                    Promise<Void> promise = Promise.promise();
                    List<Future<Void>> futures = new ArrayList<>();

                    for (Stickers sticker : stickers) {
                        String key = STICKERS_KEY + sticker.getId();
                        String value = Json.encode(sticker);
                        futures.add(api.send(Command.SET, key, value)
                                .compose(v -> api.send(Command.EXPIRE, key, String.valueOf(EXPIRATION_TIME)))
                                .mapEmpty());
                    }

                    Future.all(futures)
                            .onSuccess(x -> promise.complete())
                            .onFailure(err -> {
                                System.err.println("Error setting stickers in Redis: " + err.getMessage());
                                promise.fail(err);
                            });
                    return promise.future();
                });
    }

    @Override
    public Future<Void> setSticker(Stickers sticker) {
        if (sticker == null) {
            return Future.failedFuture("Sticker cannot be null");
        }

        return getRedisAPI()
                .compose(api -> {
                    Promise<Void> promise = Promise.promise();
                    String key = STICKERS_KEY + sticker.getId();
                    String value = Json.encode(sticker);

                    api.send(Command.SET, key, value)
                            .compose(v -> api.send(Command.EXPIRE, key, String.valueOf(EXPIRATION_TIME)))
                            .onSuccess(v -> promise.complete())
                            .onFailure(err -> {
                                System.err.println("Error setting sticker in Redis: " + err.getMessage());
                                promise.fail(err);
                            });
                    return promise.future();
                });
    }

    @Override
    public Future<Void> deleteSticker(int id) {
        return getRedisAPI()
                .compose(api -> {
                    Promise<Void> promise = Promise.promise();
                    api.send(Command.DEL, STICKERS_KEY + id)
                            .onSuccess(v -> promise.complete())
                            .onFailure(err -> {
                                System.err.println("Error deleting sticker from Redis: " + err.getMessage());
                                promise.fail(err);
                            });
                    return promise.future();
                });
    }

    public void close() {
        if (redisAPI != null) {
            redisAPI.close();
        }
    }
}
