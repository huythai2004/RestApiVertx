package org.example;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisOptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.api.CategoriesApi;
import org.example.api.PackagesApi;
import org.example.api.StickersApi;
import org.example.config.AppConfig;
import org.example.config.ServerSettings;
import org.example.search.IndexingRedisService;
import org.example.service.cache.RedisCacheService;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ServiceVerticle extends AbstractVerticle {

    private static final Logger LOG = LogManager.getLogger(ServiceVerticle.class);

//    public static final String API_ROOT = "/api";

    private final ServerSettings serverSettings;

    public ServiceVerticle(ServerSettings serverSettings) {
        this.serverSettings = serverSettings;
    }

    String host = AppConfig.get("redis.host");
    int redisPort = AppConfig.getInt("redis.port");
    int httpPort = AppConfig.getInt("server.port");

    @Override
    public void start() {
        Set<String> allowedHeaders = new HashSet<>(Arrays.asList(
                "Access-Control-Request-Headers",
                "Access-Control-Allow-Credentials",
                "Access-Control-Allow-Origin",
                "Access-Control-Allow-Headers",
                "Content-Type",
                "Origin",
                "X-Token"
        ));

        //Create a router instance...
        Router router = Router.router(this.vertx);
        RedisOptions options = new RedisOptions()
                .setConnectionString("redis://" + host + ":" + redisPort);
        Redis client = Redis.createClient(vertx, options);
        RedisCacheService cacheService = new RedisCacheService(client, vertx);
        CategoriesApi categoriesApi = new CategoriesApi(cacheService);
        PackagesApi packagesApi = new PackagesApi(cacheService);
        StickersApi stickersApi = new StickersApi(cacheService);

        // Mount API routers
        router.mountSubRouter("/", categoriesApi.getRouter());
        router.mountSubRouter("/", packagesApi.getRouter());
        router.mountSubRouter("/", stickersApi.getRouter());

        Set<HttpMethod> allowedMethods = new HashSet<>(Arrays.asList(
                HttpMethod.GET,
                HttpMethod.POST,
                HttpMethod.PUT,
                HttpMethod.DELETE,
                HttpMethod.OPTIONS
        ));
        //enable CORS
        router.route().handler(io.vertx.ext.web.handler.CorsHandler.create("*")
                .allowedHeaders(allowedHeaders)
                .allowedMethods(allowedMethods));

        //Body handler for upload
        router.route().handler(BodyHandler.create().setUploadsDirectory("uploads"));

        router.route("/api/*").handler(StaticHandler.create("assets"));
        router.route("/resources/*").handler(StaticHandler.create("uploads"));

        int port = serverSettings.getPort();
        LOG.info("Listen on port: {} - vert.x thread pool size: {}", port, serverSettings.getPoolSize());

        HttpServerOptions httpServerOptions = new HttpServerOptions();
        httpServerOptions.setCompressionSupported(true);

        IndexingRedisService indexingRedisService = new IndexingRedisService();
        indexingRedisService.initAllIndexing();
        vertx.createHttpServer(httpServerOptions).requestHandler(router).listen(port);

    }
}