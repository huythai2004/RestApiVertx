package org.example;

import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.config.ServerSettings;
import org.example.search.AbstractRedisSearch;

import java.util.HashSet;
import java.util.Set;

public class ServiceVerticle extends AbstractVerticle {

    private static final Logger logger = LogManager.getLogger(ServiceVerticle.class);

    public static final String API_ROOT = "/api";

    private final ServerSettings serverSettings;

    public ServiceVerticle(ServerSettings serverSettings) {
        this.serverSettings = serverSettings;
    }

    @Override
    public void start() {
        Set<String> allowedHeaders = new HashSet<>();
        allowedHeaders.add("Access-Control-Request-Headers");
        allowedHeaders.add("Access-Control-Allow-Credentials");
        allowedHeaders.add("Access-Control-Allow-Origin");
        allowedHeaders.add("Access-Control-Allow-Headers");
        allowedHeaders.add("Content-Type");
        allowedHeaders.add("Origin");
        allowedHeaders.add("X-Token");

        Router router = Router.router(this.vertx);

        //enable CORS
        router.route().handler(io.vertx.ext.web.handler.CorsHandler.create("*"))
                .allowed
        // TODO: Add your routes and server logic here
        logger.info("ServiceVerticle started on port: {}", serverSettings.getPort());
    }
}
