package org.example;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import org.example.main.MemCacheMain;
import org.example.main.RedisCacheMain;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        System.out.println("Deploying RedisCacheMain...");
        vertx.deployVerticle(new RedisCacheMain(), res -> {
            if (res.succeeded()) {
                System.out.println("RedisCacheMain deployed successfully!");
            } else {
                System.err.println("Failed to deploy RedisCacheMain: " + res.cause().getMessage());
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
