package org.example.service.RedisCache;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.Promise;
import io.vertx.redis.client.Command;
import io.vertx.redis.client.Redis;
import org.example.database.model.Categories;

import io.vertx.core.Future;
import org.example.database.model.Packages;
import org.example.database.model.Stickers;
import org.example.service.CacheService;

public class RedisCacheCategoriesService implements CacheService {
    private final Redis redis;
    private final ObjectMapper mapper = new ObjectMapper();
    public RedisCacheCategoriesService(Redis redis) {
        this.redis = redis;
    }

    public Future<Categories> getCategoriesById(int id) {
        Promise<Categories> promise = Promise.promise();
        String key = "categories:" + id;

        redis.send(io.vertx.redis.client.Request.cmd(Command.create("GET")).arg(key)).onSuccess(res -> {
                    if (res != null) {
                        try {
                            Categories categories = new Categories(res.toString(), Categories.class);
                            promise.complete(categories);
                        } catch (Exception e) {
                            e.printStackTrace();
                            promise.fail("Failed to parse Categories object: " + e.getMessage());
                        }
                    } else {
                        promise.fail("Categories not found for id: " + id);
                    }
                }).onFailure(promise::fail);
     return promise.future();
    }


    @Override
    public Future<Categories> getCategories(int id) {
        return Future.succeededFuture();
    }

    @Override
    public Future<Void> saveCategories(Categories categories) {
        return  saveCategories(categories);
    }

    @Override
    public Future<Void> deleteCategories(int id) {
        return deleteCategories(id);
    }

    @Override
    public Future<Packages> getPackages(int id) {
        return null;
    }

    @Override
    public Future<Void> savePackages(Packages packages) {
        return null;
    }

    @Override
    public Future<Void> deletePackages(int id) {
        return null;
    }

    @Override
    public Future<Stickers> getStickers(int id) {
        return null;
    }

    @Override
    public Future<Void> saveStickers(Stickers stickers) {
        return null;
    }

    @Override
    public Future<Void> deleteStickers(int id) {
        return null;
    }


}
