package org.example.api;

import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
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
    private String getQueryParam (RoutingContext ctx, String key) {
        List<String> values = ctx.queryParam(key);
        return (values != null && !values.isEmpty()) ? values.get(0) : null;
    }
    private void setupRoutes() {
        router.route().handler(BodyHandler.create());

        // Get all packages or search with query parameters
        router.get("/packages").handler(ctx -> {
            // Get query parameters
            String name = getQueryParam(ctx,"name");
            String creatorName = getQueryParam(ctx, "creatorName");
            String stickerCountParam = getQueryParam(ctx,"stickerCount");
            String addWhatsApp = getQueryParam(ctx,"addWhatsApp");
            String addTelegram = getQueryParam(ctx,"addTelegram");
            String viewCountParam = getQueryParam(ctx,"viewCount");
            String categoryIdsParam = getQueryParam(ctx,"categoryIds");
            String locale = getQueryParam(ctx,"locale");
            String orderParam = getQueryParam(ctx,"order");

            // Parse numeric parameters
            Integer stickerCount = null;
            Integer viewCount = null;
            Integer categoryIds = null;
            Integer order = null;

            if (stickerCountParam != null) {
                try {
                    stickerCount = Integer.parseInt(stickerCountParam);
                } catch (NumberFormatException e) {
                    JsonObject error = new JsonObject()
                            .put("error", "Invalid stickerCount format. Must be integer")
                            .put("status", 400);
                    ctx.response()
                            .setStatusCode(400)
                            .putHeader("Content-type", "application/json")
                            .end(error.encode());
                    return;
                }
            }

            if (viewCountParam != null) {
                try {
                    viewCount = Integer.parseInt(viewCountParam);
                } catch (NumberFormatException e) {
                    JsonObject error = new JsonObject()
                            .put("error", "Invalid viewCount format. Must be a number")
                            .put("status", 400);
                    ctx.response()
                            .setStatusCode(400)
                            .putHeader("Content-type", "application/json")
                            .end(error.encode());
                    return;
                }
            }

            if (categoryIdsParam != null) {
                try {
                    categoryIds = Integer.parseInt(categoryIdsParam);
                } catch (NumberFormatException e) {
                    JsonObject error = new JsonObject()
                            .put("error", "Invalid categoryIds format. Must be a number.")
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
                            .put("error", "Invalid order format. Must be a number.")
                            .put("status", 400);
                    ctx.response()
                            .setStatusCode(400)
                            .putHeader("content-type", "application/json")
                            .end(error.encode());
                    return;
                }
            }

            // If no query parameters provided, return all packages
            if (name == null && creatorName == null && stickerCount == null &&
                    addWhatsApp == null && addTelegram == null && viewCount == null &&
                    categoryIds == null && locale == null && order == null) {
                packagesService.getAllPackages()
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
                return;
            }

            // Search with provided parameters
            packagesService.searchPackages(name, creatorName, stickerCount, addWhatsApp, addTelegram, viewCount, categoryIds, locale, order)
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

        //Get child category by categoryIds
        router.get("/packages/get-by-id").handler(ctx -> {
            String packIdParam = getQueryParam(ctx,"packageId");
            int packageId;
            try {
                packageId = Integer.parseInt(packIdParam);
            } catch (Exception e) {
                JsonObject error = new JsonObject()
                        .put("error", "Invalid packId format. Must be a number")
                        .put("status", 400);
                ctx.response()
                        .setStatusCode(400)
                        .putHeader("content-type", "application/json")
                        .end(error.encode());
                return;
            }
            packagesService.getPackageWithStickers(packageId)
                    .onSuccess(result -> {
                        if (result != null) {
                            ctx.response()
                                    .putHeader("content-type", "application/json")
                                    .end(Json.encode(result));
                        } else {
                            JsonObject error = new JsonObject()
                                    .put("error", "Package with id " + packageId + " does not exist")
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

        // Get package by ID
        router.get("/packages/:id").handler(ctx -> {
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

            packagesService.getPackagesById(id)
                    .onSuccess(packages -> {
                        if (packages != null) {
                            ctx.response()
                                    .putHeader("content-type", "application/json")
                                    .end(Json.encode(packages));
                        } else {
                            JsonObject error = new JsonObject()
                                    .put("error", "Package not found")
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

        // Create new package
        router.post("/packages").handler(ctx -> {
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

            Packages packages = body.mapTo(Packages.class);
            packagesService.setPackage(packages)
                    .onSuccess(v -> {
                        ctx.response()
                                .setStatusCode(201)
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

        // Update package
        router.patch("/packages/:id").handler(ctx -> {
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

            updatePackage(id, body)
                    .onSuccess(updatedPackage -> {
                        ctx.response()
                                .putHeader("content-type", "application/json")
                                .end(Json.encode(updatedPackage));
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

        // Delete package
        router.delete("/packages/:id").handler(ctx -> {
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

            deletePackage(id)
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

    @GET
    @Path("/")
    public Future<List<Packages>> getAllPackages() {
        return packagesService.getAllPackages();
    }

    @GET
    @Path("/{id}")
    public Future<Packages> getPackageById(@PathParam("id") int id) {
        return packagesService.getPackagesById(id);
    }

    @POST
    @Path("/")
    public Future<Void> createPackage(Packages packages) {
        return packagesService.setPackage(packages);
    }

    @PATCH
    @Path("/{id}")
    public Future<Packages> updatePackage(@PathParam("id") int id, JsonObject updates) {
        return packagesService.setPackage(updates.mapTo(Packages.class))
                .compose(v -> packagesService.getPackagesById(id));
    }

    @DELETE
    @Path("/{id}")
    public Future<Void> deletePackage(@PathParam("id") int id) {
        return packagesService.deletePackage(id);
    }

    public Router getRouter() {
        return router;
    }
}
