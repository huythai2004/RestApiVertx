package org.example.api;

import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.example.database.model.Categories;
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
                                    .end(Json.encode(stickers));
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
            try {
                int id = Integer.parseInt(ctx.pathParam("id"));
                JsonObject body = ctx.getBodyAsJson();
                if (body == null) {
                    JsonObject error = new JsonObject()
                            .put("Error", "Request body is required")
                            .put("status", 400);
                    ctx.response()
                            .setStatusCode(400)
                            .putHeader("content-type", "application/json")
                            .end(error.encode());
                    return;
                }

                //First get existing sticker
                getStickersById(id)
                        .onSuccess(existingSticker -> {
                            if (existingSticker == null) {
                                JsonObject error = new JsonObject()
                                        .put("Error", "Sticker not found")
                                        .put("status", 404);
                                ctx.response()
                                        .setStatusCode(404)
                                        .putHeader("content-type", "application/json")
                                        .end(error.encode());
                                return;
                            }

                            try {
                                //Update only provided fields
                                if (body.containsKey("url"))
                                    existingSticker.setUrl(body.getString("url"));

                                if (body.containsKey("packageId"))
                                    existingSticker.setPackageId(body.getInteger("packageId"));
                                if (body.containsKey("order")) {
                                    Object orderObj = body.getValue("order");
                                    if (orderObj instanceof Number) {
                                        existingSticker.setOrder(((Number) orderObj).intValue());
                                    } else if (orderObj instanceof String) {
                                        existingSticker.setOrder(Integer.parseInt((String) orderObj));
                                    }
                                }
                                if (body.containsKey("viewCount"))
                                    existingSticker.setViewCount(body.getInteger("viewCount"));

                                if (body.containsKey("createdDate")) {
                                    Object createdDateObj = body.getValue("createdDate");
                                    if (createdDateObj instanceof Number) {
                                        existingSticker.setCreatedDate(((Number) createdDateObj).longValue());
                                    } else if (createdDateObj instanceof String) {
                                        existingSticker.setCreatedDate((Long) createdDateObj);
                                    }
                                }
                                if (body.containsKey("emojis"))
                                    existingSticker.setEmojis(body.getString("emojis"));
                                if (body.containsKey("isPremium")) {
                                    Object isPremiumObj = body.getValue("isPremium");
                                    if (isPremiumObj instanceof Boolean) {
                                        existingSticker.setPremium((Boolean) isPremiumObj);
                                    } else if (isPremiumObj instanceof String) {
                                        existingSticker.setPremium(Boolean.parseBoolean((String) isPremiumObj));
                                    }
                                }
                                createStickers(existingSticker)
                                        .onSuccess(v -> {
                                            ctx.response()
                                                    .putHeader("content-type", "application/json")
                                                    .end(Json.encodePrettily(existingSticker));
                                        })
                                        .onFailure(err -> {
                                            JsonObject error = new JsonObject()
                                                    .put("Error: ", err.getMessage())
                                                    .put("status", 500);
                                            ctx.response()
                                                    .setStatusCode(500)
                                                    .putHeader("content-type", "application/json")
                                                    .end(error.encode());
                                        });
                            } catch (Exception e) {
                                JsonObject error = new JsonObject()
                                        .put("Error", e.getMessage())
                                        .put("status", 400);
                                ctx.response()
                                        .setStatusCode(400)
                                        .putHeader("content-type", "application/json")
                                        .end(error.encode());
                            }
                        }).onFailure(err -> {
                            JsonObject error = new JsonObject()
                                    .put("Error: ", err.getMessage())
                                    .put("status", 500);
                            ctx.response()
                                    .setStatusCode(500)
                                    .putHeader("content-type", "application/json")
                                    .end(error.encode());
                        });
            } catch (Exception e) {
                JsonObject error = new JsonObject()
                        .put("Error", e.getMessage())
                        .put("status", 400);
                ctx.response()
                        .setStatusCode(400)
                        .putHeader("content-type", "application/json")
                        .end(error.encode());
            }
        });

        // Delete sticker
        router.delete("/stickers/:id").handler(ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
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
    public Future<Stickers> getStickersById(@PathParam("id") int id) {
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
    public Future<Void> createStickers(Stickers stickers) {
        return stickersService.setSticker(stickers)
                .recover(err -> {
                    JsonObject error = new JsonObject()
                            .put("error", err.getMessage())
                            .put("status", 500);
                    return Future.failedFuture(error.encode());
                });
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

    @PATCH
    @Path("/{id}")
    public Future<Stickers> updateStickers(@PathParam("id") int id, JsonObject updates) {
        return getStickersById(id)
                .compose(existingSticker -> {
                    if (existingSticker == null) {
                        return Future.failedFuture("Category not found");
                    }

                    // Update only provided fields
                    if (updates.containsKey("url")) existingSticker.setUrl(updates.getString("url"));
                    if (updates.containsKey("packageId")) existingSticker.setPackageId(updates.getInteger("packageId"));
                    if (updates.containsKey("locale")) existingSticker.setLocale(updates.getString("locale"));
                    if (updates.containsKey("order")) existingSticker.setOrder(updates.getInteger("order"));
                    if (updates.containsKey("viewCount"))
                        existingSticker.setViewCount(updates.getInteger("viewCount"));
                    if (updates.containsKey("createdDate"))
                        existingSticker.setCreatedDate(updates.getLong("createdDate"));
                    if( updates.containsKey("emojis"))
                        existingSticker.setEmojis(updates.getString("emojis"));
                    if (updates.containsKey("isPremium"))
                        existingSticker.setPremium(updates.getBoolean("isPremium"));

                    return createStickers(existingSticker)
                            .map(v -> existingSticker);
                });
    }

}
