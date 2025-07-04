package org.example.api;

import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import org.example.database.model.Packages;
import org.example.search.PackageRediSearch;
import org.example.service.PackagesService;
import org.example.service.cache.CacheService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/packages")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PackagesApi {
    private final PackagesService packagesService;
    private final Router router;

    public PackagesApi(CacheService cacheService) {
        this.packagesService = new PackagesService(cacheService, new PackageRediSearch());
        this.router = Router.router(cacheService.getVertx());
        setupRoutes();
    }

    private void setupRoutes() {
        router.route().handler(BodyHandler.create());

        // Get all packages
        router.get("/packages").handler(ctx -> {
            getAllPackages()
                    .onSuccess(packages -> {
                        ctx.response()
                                .putHeader("content-type", "application/json")
                                .end(Json.encode(packages));
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

        // Get package by ID
        router.get("/packages/:id").handler(ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            getPackageById(id)
                    .onSuccess(packages -> {
                        if (packages != null) {
                            ctx.response()
                                    .putHeader("content-type", "application/json")
                                    .end(Json.encode(packages));
                        } else {
                            JsonObject error = new JsonObject()
                                    .put("Error: ", "Package not found")
                                    .put("status", 404);
                            ctx.response()
                                    .setStatusCode(404)
                                    .putHeader("content-type", "application/json")
                                    .end(error.encode());
                        }
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
        });

        // Create new package
        router.post("/packages").handler(ctx -> {
            try {
                JsonObject body = ctx.getBodyAsJson();
                if (body == null) {
                    JsonObject error = new JsonObject()
                            .put("Error: ", "Request body is required")
                            .put("status", 400);
                    ctx.response()
                            .setStatusCode(400)
                            .putHeader("content-type", "application/json")
                            .end(error.encode());
                    return;
                }
                Packages packages = body.mapTo(Packages.class);
                createPackage(packages)
                        .onSuccess(v -> {
                            ctx.response()
                                    .setStatusCode(201)
                                    .putHeader("content-type", "application/json")
                                    .end(Json.encode(packages));
                        })
                        .onFailure(err -> {
                            err.printStackTrace();
                            JsonObject error = new JsonObject()
                                    .put("Error: ", err.getMessage())
                                    .put("status", 500)
                                    ;
                            ctx.response()
                                    .setStatusCode(500)
                                    .putHeader("content-type", "application/json")
                                    .end(error.encode());
                        });
            } catch (Exception e) {
                JsonObject error = new JsonObject()
                        .put("Error", "Invalid request body: " + e.getMessage())
                        .put("status", 400);
                ctx.response()
                        .setStatusCode(400)
                        .putHeader("content-type", "application/json")
                        .end(error.encode());
            }
        });

        // Update package
        router.patch("/packages/:id").handler(ctx -> {
            try {
                int id = Integer.parseInt(ctx.pathParam("id"));
                JsonObject body = ctx.getBodyAsJson();
                if (body == null) {
                    JsonObject error = new JsonObject()
                            .put("Error: ", "Request body is required")
                            .put("status", 400);
                    ctx.response()
                            .setStatusCode(400)
                            .putHeader("content-type", "application/json")
                            .end(error.encode());
                    return;
                }

                //First get the existing package
                getPackageById(id)
                        .onSuccess(existingPackage -> {
                            if (existingPackage == null) {
                                JsonObject error = new JsonObject()
                                        .put("Error: ", "Package not found")
                                        .put("status", 404);
                                ctx.response()
                                        .setStatusCode(404)
                                        .putHeader("content-type", "application/json")
                                        .end(error.encode());
                                return;
                            }

                            try {
                                //Update only provided fields
                                if (body.containsKey("name")) existingPackage.setName(body.getString("name"));

                                if (body.containsKey("creatorName"))
                                    existingPackage.setCreatorName(body.getString("creatorName"));

                                if (body.containsKey("stickerCount")) {
                                    Object stickerCountObj = body.getValue("stickerCount");
                                    if (stickerCountObj instanceof Number) {
                                        existingPackage.setStickerCount(((Number) stickerCountObj).intValue());
                                    } else if (stickerCountObj instanceof String) {
                                        existingPackage.setStickerCount(Integer.parseInt((String) stickerCountObj));
                                    }
                                }

                                if (body.containsKey("addWhatsApp"))
                                    existingPackage.setAddWhatsApp(body.getString("addWhatsApp"));

                                if (body.containsKey("addTelegram"))
                                    existingPackage.setAddTelegram(body.getString("addTelegram"));

                                if (body.containsKey("viewCount")) {
                                    Object viewCountObj = body.getValue("viewCount");
                                    if (viewCountObj instanceof Number) {
                                        existingPackage.setViewCount(((Number) viewCountObj).intValue());
                                    } else if (viewCountObj instanceof String) {
                                        existingPackage.setViewCount(Integer.parseInt((String) viewCountObj));
                                    }
                                }

                                if (body.containsKey("categoryIds"))
                                    existingPackage.setCategoryIds(body.getString("categoryIds"));

                                if (body.containsKey("isDisplayed")) {
                                    Object isDisplayedObj = body.getValue("isDisplayed");

                                    if (isDisplayedObj instanceof Boolean) {
                                        existingPackage.setIsDisplayed((Boolean) isDisplayedObj);
                                    } else if (isDisplayedObj instanceof String) {
                                        existingPackage.setIsDisplayed(Boolean.parseBoolean((String) isDisplayedObj));
                                    }
                                }

                                if (body.containsKey("createdDate")) {
                                    Object createdDateObj = body.getValue("createdDate");
                                    if (createdDateObj instanceof Number) {
                                        existingPackage.setCreatedDate(((Number) createdDateObj).longValue());
                                    } else if (createdDateObj instanceof String) {
                                        existingPackage.setCreatedDate((Long) createdDateObj);
                                    }
                                }
                                if (body.containsKey("locale")) existingPackage.setLocale(body.getString("locale"));

                                if (body.containsKey("order")) existingPackage.setOrder(body.getInteger("order"));

                                if (body.containsKey("isPremium")) {
                                    Object isPremiumObj = body.getValue("isPremium");
                                    if (isPremiumObj instanceof Boolean) {
                                        existingPackage.setIsPremium((Boolean) isPremiumObj);
                                    } else if (isPremiumObj instanceof String) {
                                        existingPackage.setIsPremium(Boolean.parseBoolean((String) isPremiumObj));
                                    }
                                }

                                if (body.containsKey("isAnimated")) {
                                    Object isAnimatedObj = body.getValue("isAnimated");
                                    if (isAnimatedObj instanceof Boolean) {
                                        existingPackage.setIsAnimated((Boolean) isAnimatedObj);
                                    } else if (isAnimatedObj instanceof String) {
                                        existingPackage.setIsAnimated(Boolean.parseBoolean((String) isAnimatedObj));
                                    }
                                }
                                createPackage(existingPackage)
                                        .onSuccess(v -> {
                                            ctx.response()
                                                    .putHeader("content-type", "application/json")
                                                    .end(Json.encodePrettily(existingPackage));
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
                                        .put("Error: ", "Invalid request body" + e.getMessage())
                                        .put("status", 400);
                                ctx.response()
                                        .setStatusCode(400)
                                        .putHeader("content-type", "application/json")
                                        .end(error.encode());
                                ;
                            }
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
                        .put("Error: ", "Invalid request: " + e.getMessage())
                        .put("status", 400);
                ctx.response()
                        .setStatusCode(400)
                        .putHeader("content-type", "application/json")
                        .end(error.encode());
            }
        });

        // Delete package
        router.delete("/packages/:id").handler(ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            deletePackage(id)
                    .onSuccess(v -> {
                        ctx.response()
                                .setStatusCode(204)
                                .putHeader("content-type", "application/json")
                                .end();
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
        });
    }

    @GET
    @Path("/")
    public Future<List<Packages>> getAllPackages() {
        return packagesService.getAllPackages()
                .recover(err -> {
                    JsonObject error = new JsonObject()
                            .put("error", err.getMessage())
                            .put("status", 500);
                    return Future.failedFuture(error.encode());
                });
    }

    @GET
    @Path("/{id}")
    public Future<Packages> getPackageById(@PathParam("id") int id) {
        return packagesService.getPackageById(id)
                .recover(err -> {
                    JsonObject error = new JsonObject()
                            .put("error", err.getMessage())
                            .put("status", 500);
                    return Future.failedFuture(error.encode());
                });
    }

    @POST
    @Path("/")
    public Future<Void> createPackage(Packages packages) {
        return packagesService.setPackage(packages)
                .recover(err -> {
                    JsonObject error = new JsonObject()
                            .put("error", err.getMessage())
                            .put("status", 500);
                    return Future.failedFuture(error.encode());
                });
    }

    @PATCH
    @Path("/{id}")
    public Future<Packages> updatePackage(@PathParam("id") int id, JsonObject updates) {
        return getPackageById(id)
                .compose(existingPackage -> {
                    if (existingPackage == null) {
                        return Future.failedFuture("Package not found");
                    }

                    // Update only provided fields
                    if (updates.containsKey("name")) existingPackage.setName(updates.getString("name"));
                    if (updates.containsKey("creatorName"))
                        existingPackage.setCreatorName(updates.getString("creatorName"));
                    if (updates.containsKey("stickerCount"))
                        existingPackage.setStickerCount(updates.getInteger("stickerCount"));
                    if (updates.containsKey("addWhatsApp"))
                        existingPackage.setAddWhatsApp(updates.getString("addWhatsApp"));
                    if (updates.containsKey("addTelegram"))
                        existingPackage.setAddTelegram(updates.getString("addTelegram"));
                    if (updates.containsKey("viewCount")) existingPackage.setViewCount(updates.getInteger("viewCount"));
                    if (updates.containsKey("categoryIds"))
                        existingPackage.setCategoryIds(updates.getString("categoryIds"));
                    if (updates.containsKey("isDisplayed"))
                        existingPackage.setIsDisplayed(updates.getBoolean("isDisplayed"));
                    if (updates.containsKey("createdDate"))
                        existingPackage.setCreatedDate(updates.getLong(("createdDate")));
                    if (updates.containsKey("locale")) existingPackage.setLocale(updates.getString("locale"));
                    if (updates.containsKey("order")) existingPackage.setOrder(updates.getInteger("order"));
                    if (updates.containsKey("isPremium")) existingPackage.setIsPremium(updates.getBoolean("isPremium"));
                    if (updates.containsKey("isAnimated"))
                        existingPackage.setIsAnimated(updates.getBoolean("isAnimated"));

                    return createPackage(existingPackage)
                            .map(v -> existingPackage);
                });
    }

    @DELETE
    @Path("/{id}")
    public Future<Void> deletePackage(@PathParam("id") int id) {
        return packagesService.deletePackage(id)
                .recover(err -> {
                    JsonObject error = new JsonObject()
                            .put("error", err.getMessage())
                            .put("status", 500);
                    return Future.failedFuture(error.encode());
                });
    }

    public Router getRouter() {
        return router;
    }
}
