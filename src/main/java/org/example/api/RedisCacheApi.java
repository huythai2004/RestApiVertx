package org.example.api;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.ext.web.Router;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import io.vertx.ext.web.RoutingContext;

@Path("/redis")
public class RedisCacheApi {
    private final Vertx vertx;
    private final HttpClient client;

    public RedisCacheApi(Vertx vertx) {
        this.vertx = vertx;
        this.client = vertx.createHttpClient();
    }

    @GET
    public void getCategories(@Context RoutingContext context) {
        // TODO: Implement Redis cache logic
        context.response()
            .putHeader("content-type", "application/json")
            .end(new io.vertx.core.json.JsonObject().put("status", "not implemented").encode());
    }
}
