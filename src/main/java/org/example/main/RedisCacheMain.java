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
    String host = AppConfig.get("redis.host");
    int redisPort = AppConfig.getInt("redis.port");
    int httpPort = AppConfig.getInt("server.port"); // Use server.port for HTTP

    @Override
    public void start(Promise<Void> startPromise) {
        System.out.println("Redis host: " + host);
        System.out.println("Redis port: " + redisPort);
        System.out.println("HTTP port: " + httpPort);
        if(host == null || host.isEmpty()) {
            System.err.println("Error: Redis is not configured properly");
            startPromise.fail("Redis host is null or empty");
            return;
        }
        if(redisPort == 0){
            System.err.println("Error: Redis port is not configured properly");
            startPromise.fail("Redis port is not set or invalid");
            return;
        }

        RedisOptions options = new RedisOptions()
                .setConnectionString("redis://" + host + ":" + redisPort);

        Redis client = Redis.createClient(vertx, options);
        RedisCacheService cacheService = new RedisCacheService(client, vertx);

        // Create router
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());

        // Register API endpoints and routers
        CategoriesApi categoriesApi = new CategoriesApi(cacheService);
        PackagesApi packagesApi = new PackagesApi(cacheService);
        StickersApi stickersApi = new StickersApi(cacheService);

        // Mount API routers
        router.mountSubRouter("/", categoriesApi.getRouter());
        router.mountSubRouter("/", packagesApi.getRouter());
        router.mountSubRouter("/", stickersApi.getRouter());

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(httpPort, http -> {
                    if (http.succeeded()) {
                        startPromise.complete();
                        System.out.println("Redis Cache Server started on port " + httpPort);
                    } else {
                        startPromise.fail(http.cause());
                        System.err.println("Failed to start HTTP server: " + http.cause());
                    }
                });
    }

}
