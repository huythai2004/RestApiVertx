package org.example;

import io.vertx.core.Vertx;
import org.example.config.ServerSettings;
import org.example.main.MemCacheMain;


public class Main {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        ServerSettings  serverSettings = new ServerSettings();
        try {
            serverSettings.parse(args);
        } catch (Exception e) {
            System.err.println("Error parsing command line arguments: " + e.getMessage());
            System.exit(1);
        }
        System.out.println("Deploying Service Verticle...");
        vertx.deployVerticle(new ServiceVerticle(serverSettings), res -> {
            if (res.succeeded()) {
                System.out.println("Service Verticle deployed successfully!");
            } else {
                System.err.println("Failed to deploy Service Verticle: " + res.cause().getMessage());
            }
        });

        System.out.println("Deploying MemCacheMain...");
        vertx.deployVerticle(new MemCacheMain(), res -> {
            if (res.succeeded()) {
                System.out.println("MemCacheMain deployed successfully!");
            } else {
                System.err.println("Failed to deploy MemCacheMain: " + res.cause().getMessage());
            }
        });
    }
}
