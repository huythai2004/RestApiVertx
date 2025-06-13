package org.example.api;

import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.example.database.model.Stickers;
import org.example.service.StickersService;
import org.example.service.cache.CacheService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

import io.vertx.ext.web.handler.BodyHandler;

@Path("/stickers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class StickersApi {
    private final StickersService stickersService;
    private final Router router;

    public StickersApi(CacheService cacheService) {
        this.stickersService = new StickersService(cacheService);
        this.router = Router.router(cacheService.getVertx());
        setupRoutes();
    }

    private void setupRoutes() {
        router.route().handler(BodyHandler.create());

        // Get all stickers
        router.get("/stickers").handler(ctx -> getAllStickers()
                .onSuccess(result -> ctx.response()
                        .putHeader("content-type", "application/json")
                        .end(Json.encode(result)))
                .onFailure(err -> ctx.response()
                        .setStatusCode(500)
                        .end("Error: " + err.getMessage())));

        // Get sticker by ID
        router.get("/stickers/:id").handler(ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            getStickerById(id)
                    .onSuccess(result -> ctx.response()
                            .putHeader("content-type", "application/json")
                            .end(Json.encode(result)))
                    .onFailure(err -> ctx.response()
                            .setStatusCode(500)
                            .end("Error: " + err.getMessage()));
        });

        // Create new sticker
        router.post("/stickers").handler(ctx -> createSticker(ctx)
                .onSuccess(v -> ctx.response()
                        .setStatusCode(201)
                        .end())
                .onFailure(err -> ctx.response()
                        .setStatusCode(500)
                        .end("Error: " + err.getMessage())));

        // Update sticker
        router.put("/stickers/:id").handler(ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            updateSticker(id, ctx)
                    .onSuccess(v -> ctx.response()
                            .setStatusCode(200)
                            .end())
                    .onFailure(err -> ctx.response()
                            .setStatusCode(500)
                            .end("Error: " + err.getMessage()));
        });

        // Delete sticker
        router.delete("/stickers/:id").handler(ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            deleteSticker(id)
                    .onSuccess(v -> ctx.response()
                            .setStatusCode(204)
                            .end())
                    .onFailure(err -> ctx.response()
                            .setStatusCode(500)
                            .end("Error: " + err.getMessage()));
        });
    }

    public Router getRouter() {
        return router;
    }

    @GET
    @Path("/")
    public Future<List<Stickers>> getAllStickers() {
        return stickersService.getAllStickers()
                .recover(err -> {
                    JsonObject error = new JsonObject()
                            .put("error", err.getMessage())
                            .put("status", 500);
                    return Future.failedFuture(error.encode());
                });
    }

    @GET
    @Path("/{id}")
    public Future<Stickers> getStickerById(@PathParam("id") int id) {
        return stickersService.getStickerById(id)
                .recover(err -> {
                    JsonObject error = new JsonObject()
                            .put("error", err.getMessage())
                            .put("status", 500);
                    return Future.failedFuture(error.encode());
                });
    }

    @POST
    @Path("/")
    public Future<Void> createSticker(RoutingContext context) {
        try {
            Stickers sticker = context.getBodyAsJson().mapTo(Stickers.class);
            return stickersService.setSticker(sticker)
                    .recover(err -> {
                        JsonObject error = new JsonObject()
                                .put("error", err.getMessage())
                                .put("status", 500);
                        return Future.failedFuture(error.encode());
                    });
        } catch (Exception e) {
            JsonObject error = new JsonObject()
                    .put("error", e.getMessage())
                    .put("status", 400);
            return Future.failedFuture(error.encode());
        }
    }

    @PUT
    @Path("/{id}")
    public Future<Void> updateSticker(@PathParam("id") int id, RoutingContext context) {
        try {
            Stickers sticker = context.getBodyAsJson().mapTo(Stickers.class);
            sticker.setId(id);
            return stickersService.setSticker(sticker)
                    .recover(err -> {
                        JsonObject error = new JsonObject()
                                .put("error", err.getMessage())
                                .put("status", 500);
                        return Future.failedFuture(error.encode());
                    });
        } catch (Exception e) {
            JsonObject error = new JsonObject()
                    .put("error", e.getMessage())
                    .put("status", 400);
            return Future.failedFuture(error.encode());
        }
    }

    @DELETE
    @Path("/{id}")
    public Future<Void> deleteSticker(@PathParam("id") int id) {
        return stickersService.deleteSticker(id)
                .recover(err -> {
                    JsonObject error = new JsonObject()
                            .put("error", err.getMessage())
                            .put("status", 500);
                    return Future.failedFuture(error.encode());
                });
    }


}
