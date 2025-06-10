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
    //private static final int PORT = 8888;
    String host = AppConfig.get("memcached.host");
    int port = AppConfig.getInt("memcached.port");

    @Override
    public void start(Promise<Void> startPromise) {
        try {
            // Create Memcached client
            MemcachedClient memcachedClient = new MemcachedClient(
                    new InetSocketAddress(host, port));

            // Create Memcached cache service
            MemCacheService cacheService = new MemCacheService(memcachedClient);

            // Create router
            Router router = Router.router(vertx);
            router.route().handler(BodyHandler.create());

            // Register API endpoints
            CategoriesApi categoriesApi = new CategoriesApi(cacheService);
            PackagesApi packagesApi = new PackagesApi(cacheService);
            StickersApi stickersApi = new StickersApi(cacheService);

            categoriesApi.registerRoutersCategories(router);
            packagesApi.registerRouterPackages(router);
            stickersApi.registerRouterStickers(router);

            // Create HTTP server
            vertx.createHttpServer()
                    .requestHandler(router)
                    .listen(port, http -> {
                        if (http.succeeded()) {
                            startPromise.complete();
                            System.out.println("Memcached Cache Server started on memCachePort " + port);
                        } else {
                            startPromise.fail(http.cause());
                        }
                    });

        } catch (IOException e) {
            System.err.println("Failed to connect Memcached client: " + e.getMessage());
            e.printStackTrace();
            startPromise.fail(e);
        }
    }
}
