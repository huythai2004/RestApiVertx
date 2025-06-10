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
import org.example.config.AppConfig;
import org.example.service.cache.RedisCacheService;

public class RedisCacheMain extends AbstractVerticle {
    //private static final int HTTP_PORT = 8888;
    String host = AppConfig.get("redis.host");
    int port = AppConfig.getInt("redis.port");
    @Override
    public void start(Promise<Void> startPromise) {
        RedisOptions redisOptions = new RedisOptions()
                .setConnectionString("Connecting to Redis at: redis://" + host + ":" + port);

        Redis redis = Redis.createClient(vertx, redisOptions);

        RedisCacheService cacheService = new RedisCacheService(redis);

        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());

        new CategoriesApi(cacheService);
        new PackagesApi(cacheService);
        new StickersApi(cacheService);

        CategoriesApi categoriesApi = new CategoriesApi(cacheService);
        PackagesApi packagesApi = new PackagesApi(cacheService);
        StickersApi stickersApi = new StickersApi(cacheService);

        categoriesApi.registerRoutersCategories(router);
        packagesApi.registerRouterPackages(router);
        stickersApi.registerRouterStickers(router);

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(port, http -> {
                    if (http.succeeded()) {
                        startPromise.complete();
                        System.out.println("Redis Cache Server started on memCachePort " + port);
                    } else {
                        startPromise.fail(http.cause());
                        System.err.println("Failed to start HTTP server: " + http.cause());
                    }
                });
    }

}
