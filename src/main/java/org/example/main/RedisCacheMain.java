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
    //private static final int HTTP_PORT = 8080;
    String host = AppConfig.get("redis.host");
    int port = AppConfig.getInt("redis.port");
    @Override
    public void start(Promise<Void> startPromise) {
        System.out.println("Redis host: " + host);
        System.out.println("Redis port: " + port);
        if(host == null || host.isEmpty()) {
            System.err.println("Error: Redis is not configured properly");
            startPromise.fail("Redis host is null or empty");
            return;
        }
        if(port == 0){
            System.err.println("Error: Redis port is not configured properly");
            startPromise.fail("Redis port is not set or invalid");
            return;
        }

        RedisOptions redisOptions = new RedisOptions()
                .setConnectionString("Connecting to Redis at: redis://" + host + ":" + port);

        Redis redis = Redis.createClient(vertx, redisOptions);
        RedisCacheService cacheService = new RedisCacheService(redis);

        // Create router
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());

        // Register API endpoints and routers
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
                        System.out.println("Redis Cache Server started on port " + port);
                    } else {
                        startPromise.fail(http.cause());
                        System.err.println("Failed to start HTTP server: " + http.cause());
                    }
                });
    }

}
