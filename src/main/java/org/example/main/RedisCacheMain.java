package org.example.main;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisOptions;
import org.example.api.CategoriesApi;
import org.example.api.PackagesApi;
import org.example.api.StickersApi;
import org.example.service.cache.RedisCacheService;

public class RedisCacheMain extends AbstractVerticle {
    private static final int PORT = 8080;

    @Override
    public void start(Promise<Void> startPromise) {
        // Create Redis client
        RedisOptions redisOptions = new RedisOptions()
                .setConnectionString("redis://localhost:6379");
        Redis redis = Redis.createClient(vertx, redisOptions);

        // Create Redis cache service
        RedisCacheService cacheService = new RedisCacheService(redis);

        // Create router
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());

        // Register API endpoints
        CategoriesApi categoriesApi = new CategoriesApi(cacheService);
        PackagesApi packagesApi = new PackagesApi(cacheService);
        StickersApi stickersApi = new StickersApi(cacheService);

        // Create HTTP server
        vertx.createHttpServer()
                .requestHandler(router)
                .listen(PORT, http -> {
                    if (http.succeeded()) {
                        startPromise.complete();
                        System.out.println("Redis Cache Server started on port " + PORT);
                    } else {
                        startPromise.fail(http.cause());
                    }
                });
    }
}
