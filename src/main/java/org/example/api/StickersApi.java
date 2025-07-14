package org.example.api;

import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.example.database.model.Stickers;
import org.example.search.StickerRediSearch;
import org.example.service.StickersService;
import org.example.service.cache.CacheService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/stickers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class StickersApi {
    private final StickersService stickersService;
    private final Router router;

    public StickersApi(CacheService cacheService) {
        this.stickersService = new StickersService(cacheService, new StickerRediSearch());
        this.router = Router.router(cacheService.getVertx());
        setupRoutes();
    }

    private String getQueryParam(RoutingContext ctx, String key) {
        List<String> values = ctx.queryParam(key);
        return (values != null && !values.isEmpty()) ? values.get(0) : null;
    }

    private void setupRoutes() {
        //Add body handler to parse JSON request bodies
        router.route().handler(BodyHandler.create());

        // Get all stickers
        router.get("/stickers").handler(ctx -> {
            getAllStickers()
                    .onSuccess(stickers -> {
                        ctx.response()
                                .putHeader("content-type", "application/json")
                                .end(Json.encode(stickers));
                    })
                    .onFailure(err -> {
                        JsonObject error = new JsonObject()
                                .put("error", err.getMessage())
                                .put("status", 500);
                        ctx.response()
                                .setStatusCode(500)
                                .putHeader("content-type", "application/json")
                                .end(error.encode());
                    });
        });

        router.get("/stickers/search").handler(ctx -> {
            String url = getQueryParam(ctx, "url");
            String packageIdParam = getQueryParam(ctx, "packageId");
            String locale = getQueryParam(ctx, "locale");
            String orderParam = getQueryParam(ctx, "order");
            String viewCountParam = getQueryParam(ctx, "viewCount");
            String createdDate = getQueryParam(ctx, "createdDate");
            String emojis = getQueryParam(ctx, "emojis");
            String isPremium = getQueryParam(ctx, "isPremium");

            Integer packageId = null;
            Integer order = null;
            Integer viewCount = null;

            if (packageIdParam != null) {
                try {
                    packageId = Integer.parseInt(packageIdParam);
                } catch (NumberFormatException e) {
                    JsonObject error = new JsonObject()
                            .put("error", "Invalid packageId format. Must be integer")
                            .put("status", 400);
                    ctx.response()
                            .setStatusCode(400)
                            .putHeader("content-type", "application/json")
                            .end(error.encode());
                    return;
                }
            }

            if (orderParam != null) {
                try {
                    order = Integer.parseInt(orderParam);
                } catch (NumberFormatException e) {
                    JsonObject error = new JsonObject()
                            .put("error", "Invalid order format. Must be integer")
                            .put("status", 400);
                    ctx.response()
                            .setStatusCode(400)
                            .putHeader("content-type", "application/json")
                            .end(error.encode());
                    return;
                }
            }

            if (viewCountParam != null) {
                try {
                    viewCount = Integer.parseInt(viewCountParam);
                } catch (NumberFormatException e) {
                    JsonObject error = new JsonObject()
                            .put("error", "Invalid viewCount format.Must be integer")
                            .put("status", 400);
                    ctx.response()
                            .setStatusCode(400)
                            .putHeader("Content-type", "application/json")
                            .end(error.encode());
                    return;
                }
            }

            //If no query parameters provided, return all stickers
            if (url == null && packageId == null && locale == null && order == null && viewCount == null && createdDate == null && emojis == null && isPremium == null) {
                stickersService.getAllStickers()
                        .onSuccess(stickers -> {
                            ctx.response()
                                    .putHeader("content-type", "application/json")
                                    .end(Json.encode(stickers));
                        })
                        .onFailure(err -> {
                            JsonObject error = new JsonObject()
                                    .put("error", err.getMessage())
                                    .put("status", 500);
                            ctx.response()
                                    .setStatusCode(500)
                                    .putHeader("content-type", "application/json")
                                    .end(error.encode());
                        });
                return;
            }

            stickersService.searchStickers(url, packageId, locale, order, viewCount, emojis)
                    .onSuccess(stickers -> {
                        ctx.response()
                                .putHeader("content-type", "application/json")
                                .end(Json.encode(stickers));
                    })
                    .onFailure(err -> {
                        JsonObject error = new JsonObject()
                                .put("error", err.getMessage())
                                .put("status", 500);
                        ctx.response()
                                .setStatusCode(500)
                                .putHeader("content-type", "application/json")
                                .end(error.encode());
                    });
        });

        // Get sticker by ID
        router.get("/stickers/:id").handler(ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            getStickersById(id)
                    .onSuccess(stickers -> {
                        if (stickers != null) {
                            ctx.response()
                                    .putHeader("content-type", "application/json")
                                    .end(Json.encode(stickers));
                        } else {
                            JsonObject error = new JsonObject()
                                    .put("error", "Category not found")
                                    .put("status", 404);
                            ctx.response()
                                    .setStatusCode(404)
                                    .putHeader("content-type", "application/json")
                                    .end(error.encode());
                        }
                    })
                    .onFailure(err -> {
                        JsonObject error = new JsonObject()
                                .put("error", err.getMessage())
                                .put("status", 500);
                        ctx.response()
                                .setStatusCode(500)
                                .putHeader("content-type", "application/json")
                                .end(error.encode());
                    });
        });

        // Create new sticker
        router.post("/stickers").handler(ctx -> {
            try {
                JsonObject body = ctx.getBodyAsJson();
                if (body == null) {
                    JsonObject error = new JsonObject()
                            .put("error", "Request body is required")
                            .put("status", 400);
                    ctx.response()
                            .setStatusCode(400)
                            .putHeader("content-type", "application/json")
                            .end(error.encode());
                    return;
                }

                Stickers stickers = body.mapTo(Stickers.class);
                createStickers(stickers)
                        .onSuccess(v -> {
                            ctx.response()
                                    .setStatusCode(201)
                                    .putHeader("content-type", "application/json")
                                    .end(Json.encode(stickers)); // Trả về object vừa tạo
                        })
                        .onFailure(err -> {
                            JsonObject error = new JsonObject()
                                    .put("Error:", err.getMessage())
                                    .put("status", 500);
                            ctx.response()
                                    .setStatusCode(500)
                                    .putHeader("content-type", "application/json")
                                    .end(error.encode());
                        });
            } catch (Exception e) {
                JsonObject error = new JsonObject()
                        .put("error", "Invalid request body: " + e.getMessage())
                        .put("status", 400);
                ctx.response()
                        .setStatusCode(400)
                        .putHeader("content-type", "application/json")
                        .end(error.encode());
            }
        });

        // Update sticker
        router.patch("/stickers/:id").handler(ctx -> {
            String idParam = ctx.pathParam("id");
            int id;
            try {
                id = Integer.parseInt(idParam);
            } catch (NumberFormatException e) {
                JsonObject error = new JsonObject()
                        .put("error", "Invalid ID format")
                        .put("status", 400);
                ctx.response()
                        .setStatusCode(400)
                        .putHeader("content-type", "application/json")
                        .end(error.encode());
                return;
            }
            JsonObject body = ctx.getBodyAsJson();
            if (body == null) {
                JsonObject error = new JsonObject()
                        .put("error", "Request body is required")
                        .put("status", 400);
                ctx.response()
                        .setStatusCode(400)
                        .putHeader("content-type", "application/json")
                        .end(error.encode());
                return;
            }

            updateStickers(id, body)
                    .onSuccess(updateStickers -> {
                        ctx.response()
                                .putHeader("content-type", "application/json")
                                .end(Json.encode(updateStickers));
                    })
                    .onFailure(err -> {
                        JsonObject error = new JsonObject()
                                .put("error", err.getMessage())
                                .put("status", 500);
                        ctx.response()
                                .setStatusCode(500)
                                .putHeader("content-type", "application/json")
                                .end(error.encode());
                    });
        });

        // Delete sticker
        router.delete("/stickers/:id").handler(ctx -> {
            String idParam = ctx.pathParam("id");
            int id;
            try {
                id = Integer.parseInt(idParam);
            } catch (NumberFormatException e) {
                JsonObject error = new JsonObject()
                        .put("error", "Invalid ID format")
                        .put("status", 400);
                ctx.response()
                        .setStatusCode(400)
                        .putHeader("content-type", "application/json")
                        .end(error.encode());
                return;
            }
            deleteSticker(id)
                    .onSuccess(v -> {
                        ctx.response()
                                .setStatusCode(204)
                                .end();
                    })
                    .onFailure(err -> {
                        JsonObject error = new JsonObject()
                                .put("error", err.getMessage())
                                .put("status", 500);
                        ctx.response()
                                .setStatusCode(500)
                                .putHeader("content-type", "application/json")
                                .end(error.encode());
                    });
        });
    }

    public Router getRouter() {
        return router;
    }

    @GET
    @Path("/")
    public Future<List<Stickers>> getAllStickers() {
        return stickersService.getAllStickers();
    }

    @GET
    @Path("/{id}")
    public Future<Stickers> getStickersById(@PathParam("id") int id) {
        return stickersService.getStickerById(id);
    }

    @POST
    @Path("/")
    public Future<Void> createStickers(Stickers stickers) {
        return stickersService.setSticker(stickers);
    }

    @PATCH
    @Path("/{id}")
    public Future<Stickers> updateStickers(@PathParam("id") int id, JsonObject updatesStickers) {
        return stickersService.setSticker(updatesStickers.mapTo(Stickers.class))
                .compose(v -> stickersService.getStickerById(id));
    }

    @DELETE
    @Path("/{id}")
    public Future<Void> deleteSticker(@PathParam("id") int id) {
        return stickersService.deleteSticker(id);
    }
}
