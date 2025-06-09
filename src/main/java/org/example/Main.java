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
        Router router = Router.router(vertx);
        vertx.deployVerticle(new MemCacheMain());
        vertx.deployVerticle(new RedisCacheMain());
    }
}