package org.example.service;

import com.zandero.rest.annotation.BodyParam;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import net.spy.memcached.MemcachedClient;

@Path("/memcache")
public class MemCacheRestService {
    private final MemcachedClient memcachedClient;
    private final Vertx vertx;

    public MemCacheRestService(Vertx vertx) {
        this.vertx = vertx;
        try {
            memcachedClient = new MemcachedClient(new java.net.InetSocketAddress("localhost", 11211));
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect to Memcached server: " + e.getMessage());
        }
    }
    @Path("get/{key}")
    public void getJson (@Context RoutingContext context, @PathParam("key") String key) {
        vertx.executeBlocking(promise -> {
            try {
                Object value = memcachedClient.get(key);
                promise.complete(value);
            } catch (Exception e) {
                promise.fail(e);
            }
        }, res -> {
            if(res.succeeded()) {
                context.response().putHeader("content-type", "application/json")
                        .end(new JsonObject().put("categories", key) .put("value", res.result()).encode());
            } else {
                context.response().setStatusCode(500).end(new JsonObject().put("error", res.cause().getMessage()).encode());
            }
        });
    }

    @Path("/{key}")
    public void setJson(@Context RoutingContext context, @PathParam("key") String key, @BodyParam JsonObject body) {
        String value = body.getString("value");
        int ttlSeconds = body.getInteger("ttlSeconds", 300); // default to 5 minutes if not provided

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
    public  void deleteJson(@Context RoutingContext context, @PathParam("key") String key) {
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
                    .end(new JsonObject().put("status", "success").put("key", key).encode());
            } else {
                context.response()
                    .setStatusCode(500)
                    .end(new JsonObject().put("error", res.cause().getMessage()).encode());
            }
        });
    }
}
