package org.example.main;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import net.spy.memcached.MemcachedClient;
import org.example.api.CategoriesApi;
import org.example.api.PackagesApi;
import org.example.api.StickersApi;
import org.example.config.AppConfig;
import org.example.service.cache.MemCacheService;

import java.io.IOException;
import java.net.InetSocketAddress;

public class MemCacheMain extends AbstractVerticle {
    String host = AppConfig.get("memcached.host");
    int memcachedPort = AppConfig.getInt("memcached.port");
    int httpPort = AppConfig.getInt("server2.port"); // Use server.port + 1 for HTTP

    @Override
    public void start(Promise<Void> startPromise) {
        try {
            System.out.println("Memcached host: " + host);
            System.out.println("Memcached port: " + memcachedPort);
            System.out.println("HTTP port: " + httpPort);

            // Create Memcached client
            MemcachedClient memcachedClient = new MemcachedClient(
                    new InetSocketAddress(host, memcachedPort));

            // Create Memcached cache service
            MemCacheService cacheService = new MemCacheService(memcachedClient, vertx);

            // Create router
            Router router = Router.router(vertx);
            router.route().handler(BodyHandler.create());

            // Register API endpoints
            CategoriesApi categoriesApi = new CategoriesApi(cacheService);
            PackagesApi packagesApi = new PackagesApi(cacheService);
            StickersApi stickersApi = new StickersApi(cacheService);

            // Mount API routers
            router.mountSubRouter("/", categoriesApi.getRouter());
            router.mountSubRouter("/", packagesApi.getRouter());
            router.mountSubRouter("/", stickersApi.getRouter());

            // Create HTTP server
            vertx.createHttpServer()
                    .requestHandler(router)
                    .listen(httpPort, ar -> {
                        if (ar.succeeded()) {
                            startPromise.complete();
                            System.out.println("Memcached Cache Server started on port " + httpPort);
                        } else {
                            startPromise.fail(ar.cause());
                        }
                    });

        } catch (IOException e) {
            System.err.println("Failed to connect Memcached client: " + e.getMessage());
            e.printStackTrace();
            startPromise.fail(e);
        }
    }
    @Override
    public void stop(Promise<Void> stopPromise)  {
        try {
            // Close Memcached client if needed
            stopPromise.complete();
        } catch (Exception e) {
            System.err.println("Failed to stop Memcached client: " + e.getMessage());
            stopPromise.fail(e);
        }
    }
}
