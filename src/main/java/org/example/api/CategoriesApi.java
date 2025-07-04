package org.example.api;

import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import org.example.database.model.Categories;
import org.example.service.CategoriesService;
import org.example.service.cache.CacheService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/categories")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CategoriesApi {
    private final CategoriesService categoriesService;
    private final Router router;
    public CategoriesApi(CacheService cacheService) {
        this.categoriesService = new CategoriesService(cacheService);
        this.router = Router.router(cacheService.getVertx());
        setupRoutes();
    }

    private void setupRoutes() {
        // Add body handler to parse JSON requests
        router.route().handler(BodyHandler.create());

        // GET all categories
        router.get("/categories").handler(ctx -> {
            getAllCategories()
                    .onSuccess(categories -> {
                        ctx.response()
                                .putHeader("content-type", "application/json")
                                .end(Json.encode(categories));
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

        // GET category by ID
        router.get("/categories/:id").handler(ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            getCategoryById(id)
                    .onSuccess(category -> {
                        if (category != null) {
                            ctx.response()
                                    .putHeader("content-type", "application/json")
                                    .end(Json.encode(category));
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

        // Create new category
        router.post("/categories").handler(ctx -> {
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

                Categories category = body.mapTo(Categories.class);
                createCategory(category)
                        .onSuccess(v -> {
                            ctx.response()
                                    .setStatusCode(201)
                                    .putHeader("content-type", "application/json")
                                    .end(Json.encode(category));
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

        // DELETE category
        router.delete("/categories/:id").handler(ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            deleteCategory(id)
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

        // PATCH update category
        router.patch("/categories/:id").handler(ctx -> {
            try {
                int id = Integer.parseInt(ctx.pathParam("id"));
                JsonObject body = ctx.getBodyAsJson();
                if (body == null) {
                    JsonObject error = new JsonObject()
                            .put("Error:", "Request body is required")
                            .put("status", 400);
                    ctx.response()
                            .setStatusCode(400)
                            .putHeader("content-type", "application/json")
                            .end(error.encode());
                    return;
                }

                // First get existing category
                getCategoryById(id)
                        .onSuccess(existingCategory -> {
                            if (existingCategory == null) {
                                JsonObject error = new JsonObject()
                                        .put("Error:", "Category not found")
                                        .put("status", 404);
                                ctx.response()
                                        .setStatusCode(404)
                                        .putHeader("content-type", "application/json")
                                        .end(error.encode());
                                return;
                            }

                            try {
                                // Update only provided fields
                                if (body.containsKey("name")) existingCategory.setName(body.getString("name"));
                                if (body.containsKey("url")) existingCategory.setUrl(body.getString("url"));
                                if (body.containsKey("locale")) existingCategory.setLocale(body.getString("locale"));
                                if (body.containsKey("order")) {
                                    Object orderObj = body.getValue("order");
                                    if (orderObj instanceof Number) {
                                        existingCategory.setOrder(((Number) orderObj).intValue());
                                    } else if (orderObj instanceof String) {
                                        existingCategory.setOrder(Integer.parseInt((String) orderObj));
                                    }
                                }
                                if (body.containsKey("packageCount")) {
                                    Object packageCountObj = body.getValue("packageCount");
                                    if (packageCountObj instanceof Number) {
                                        existingCategory.setPackageCount(((Number) packageCountObj).intValue());
                                    } else if (packageCountObj instanceof String) {
                                        existingCategory.setPackageCount(Integer.parseInt((String) packageCountObj));
                                    }
                                }
                                if (body.containsKey("createdDate")) {
                                    Object createdDateObj = body.getValue("createdDate");
                                    if (createdDateObj instanceof Number) {
                                        existingCategory.setCreatedDate(((Number) createdDateObj).longValue());
                                    } else if (createdDateObj instanceof String) {
                                        existingCategory.setCreatedDate((Long) createdDateObj);
                                    }
                                }
                                if (body.containsKey("isDisplayed")) {
                                    Object isDisplayedObj = body.getValue("isDisplayed");
                                    if (isDisplayedObj instanceof Boolean) {
                                        existingCategory.setDisplayed((Boolean) isDisplayedObj);
                                    } else if (isDisplayedObj instanceof String) {
                                        existingCategory.setDisplayed(Boolean.parseBoolean((String) isDisplayedObj));
                                    }
                                }

                                // Save updated category
                                createCategory(existingCategory)
                                        .onSuccess(v -> {
                                            ctx.response()
                                                    .putHeader("content-type", "application/json")
                                                    .end(Json.encode(existingCategory));
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
                                        .put("Error:", "Invalid field value: " + e.getMessage())
                                        .put("status", 400);
                                ctx.response()
                                        .setStatusCode(400)
                                        .putHeader("content-type", "application/json")
                                        .end(error.encode());
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
                        .put("Error:", "Invalid request: " + e.getMessage())
                        .put("status", 400);
                ctx.response()
                        .setStatusCode(400)
                        .putHeader("content-type", "application/json")
                        .end(error.encode());
            }
        });
    }

    public Router getRouter() {
        return router;
    }

    @GET
    @Path("/")
    public Future<List<Categories>> getAllCategories() {
        return categoriesService.getAllCategories()
                .recover(err -> {
                    JsonObject error = new JsonObject()
                            .put("error", err.getMessage())
                            .put("status", 500);
                    return Future.failedFuture(error.encode());
                });
    }

    @GET
    @Path("/{id}")
    public Future<Categories> getCategoryById(@PathParam("id") int id) {
        return categoriesService.getCategoryById(id)
                .recover(err -> {
                    JsonObject error = new JsonObject()
                            .put("error", err.getMessage())
                            .put("status", 500);
                    return Future.failedFuture(error.encode());
                });
    }

    @POST
    @Path("/")
    public Future<Void> createCategory(Categories category) {
        return categoriesService.setCategory(category)
                .recover(err -> {
                    JsonObject error = new JsonObject()
                            .put("error", err.getMessage())
                            .put("status", 500);
                    return Future.failedFuture(error.encode());
                });
    }

    @DELETE
    @Path("/{id}")
    public Future<Void> deleteCategory(@PathParam("id") int id) {
        return categoriesService.deleteCategory(id)
                .recover(err -> {
                    JsonObject error = new JsonObject()
                            .put("Error: ", err.getMessage())
                            .put("status", 500);
                    return Future.failedFuture(error.encode());
                });
    }

    @PATCH
    @Path("/{id}")
    public Future<Categories> updateCategory(@PathParam("id") int id, JsonObject updates) {
        return getCategoryById(id)
                .compose(existingCategory -> {
                    if (existingCategory == null) {
                        return Future.failedFuture("Category not found");
                    }

                    // Update only provided fields
                    if (updates.containsKey("name")) existingCategory.setName(updates.getString("name"));
                    if (updates.containsKey("url")) existingCategory.setUrl(updates.getString("url"));
                    if (updates.containsKey("locale")) existingCategory.setLocale(updates.getString("locale"));
                    if (updates.containsKey("order")) existingCategory.setOrder(updates.getInteger("order"));
                    if (updates.containsKey("packageCount"))
                        existingCategory.setPackageCount(updates.getInteger("packageCount"));
                    if (updates.containsKey("createdDate"))
                        existingCategory.setCreatedDate(updates.getLong("createdDate"));
                    if (updates.containsKey("isDisplayed"))
                        existingCategory.setDisplayed(updates.getBoolean("isDisplayed"));

                    return createCategory(existingCategory)
                            .map(v -> existingCategory);
                });
    }
}
