package org.example.api;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import net.spy.memcached.MemcachedClient;
import java.net.InetSocketAddress;

@Path("/memcache")
public class MemCacheApi {
    private final Vertx vertx;
    private final MemcachedClient memcachedClient;

    public MemCacheApi(Vertx vertx) {
        this.vertx = vertx;
        try {
            this.memcachedClient = new MemcachedClient(new InetSocketAddress("localhost", 11211));
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect to Memcached server: " + e.getMessage());
        }
    }

    @GET
    @Path("/{key}")
    public void get(@Context RoutingContext context, @PathParam("key") String key) {
        vertx.executeBlocking(promise -> {
            try {
                Object value = memcachedClient.get(key);
                promise.complete(value);
            } catch (Exception e) {
                promise.fail(e);
            }
        }, res -> {
            if (res.succeeded()) {
                context.response()
                    .putHeader("content-type", "application/json")
                    .end(new JsonObject()
                        .put("key", key)
                        .put("value", res.result())
                        .encode());
            } else {
                context.response()
                    .setStatusCode(500)
                    .end(new JsonObject()
                        .put("error", res.cause().getMessage())
                        .encode());
            }
        });
    }

    @POST
    @Path("/{key}")
    public void set(@Context RoutingContext context, @PathParam("key") String key, JsonObject body) {
        String value = body.getString("value");
        int ttlSeconds = body.getInteger("ttlSeconds", 300); // default 5 minutes

        if (value == null || value.trim().isEmpty()) {
            context.response()
                .setStatusCode(400)
                .end(new JsonObject().put("error", "Value cannot be empty").encode());
            return;
        }

        vertx.executeBlocking(promise -> {
            try {
                memcachedClient.set(key, ttlSeconds, value);
                promise.complete(true);
            } catch (Exception e) {
                promise.fail(e);
            }
        }, res -> {
            if (res.succeeded()) {
                context.response()
                    .putHeader("content-type", "application/json")
                    .end(new JsonObject()
                        .put("status", "success")
                        .put("key", key)
                        .put("ttl", ttlSeconds)
                        .encode());
            } else {
                context.response()
                    .setStatusCode(500)
                    .end(new JsonObject()
                        .put("error", res.cause().getMessage())
                        .encode());
            }
        });
    }

    @DELETE
    @Path("/{key}")
    public void delete(@Context RoutingContext context, @PathParam("key") String key) {
        vertx.executeBlocking(promise -> {
            try {
                memcachedClient.delete(key);
                promise.complete(true);
            } catch (Exception e) {
                promise.fail(e);
            }
        }, res -> {
            if (res.succeeded()) {
                context.response()
                    .putHeader("content-type", "application/json")
                    .end(new JsonObject()
                        .put("status", "success")
                        .put("message", "Key deleted: " + key)
                        .encode());
            } else {
                context.response()
                    .setStatusCode(500)
                    .end(new JsonObject()
                        .put("error", res.cause().getMessage())
                        .encode());
            }
        });
    }
}
