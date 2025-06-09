package org.example.main;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import net.spy.memcached.MemcachedClient;
import org.example.api.CategoriesApi;
import org.example.api.PackagesApi;
import org.example.api.StickersApi;
import org.example.service.cache.MemCacheService;

import java.io.IOException;
import java.net.InetSocketAddress;

public class MemCacheMain extends AbstractVerticle {
    private static final int PORT = 8081;

    @Override
    public void start(Promise<Void> startPromise) {
        try {
            // Create Memcached client
            MemcachedClient memcachedClient = new MemcachedClient(
                    new InetSocketAddress("localhost", 11211));

            // Create Memcached cache service
            MemCacheService cacheService = new MemCacheService(memcachedClient);

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
                            System.out.println("Memcached Cache Server started on port " + PORT);
                        } else {
                            startPromise.fail(http.cause());
                        }
                    });

        } catch (IOException e) {
            startPromise.fail(e);
        }
    }
}
