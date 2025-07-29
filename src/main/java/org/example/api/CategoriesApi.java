package org.example.api;

import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.example.database.model.Categories;
import org.example.search.CategoryRediSearch;
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
        this.categoriesService = new CategoriesService(cacheService, new CategoryRediSearch());
        this.router = Router.router(cacheService.getVertx());
        setupRoutes();
    }

    private String getQueryParam(RoutingContext ctx, String key) {
        List<String> values = ctx.queryParam(key);
        return (values != null && !values.isEmpty()) ? values.get(0) : null;
    }

    private void setupRoutes() {
        router.route().handler(BodyHandler.create());

        // search all categories with no query
        router.get("/categories").handler(ctx -> {
            String name = getQueryParam(ctx, "name");
            String url = getQueryParam(ctx, "url");
            String locale = getQueryParam(ctx, "locale");
            String orderParam = getQueryParam(ctx, "order");
            String packageCountParam = getQueryParam(ctx, "packageCount ");

            Integer packageCount = null;
            Integer order = null;

            if (packageCountParam != null) {
                try {
                    packageCount = Integer.parseInt(packageCountParam);
                } catch (NumberFormatException e) {
                    JsonObject error = new JsonObject()
                            .put("error", "Invalid packageCount format. Must be integer")
                            .put("status", 400);
                    ctx.response()
                            .setStatusCode(400)
                            .putHeader("Content-type", "application/json")
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

            //If no query parameters provided, return all categories
            if (name == null && url == null && locale == null && order == null && packageCount == null) {
                categoriesService.getAllCategories()
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
                return;
            }

            //Search with provided parameters
            categoriesService.searchCategories(name, url, locale, order, packageCount)
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

        //Get child packages by categoriesIds
        router.get("/categories/get-by-id").handler(ctx -> {
            String categoriesParam = getQueryParam(ctx,"categoryIds");
            int categoriesIds;
            try {
                categoriesIds = Integer.parseInt(categoriesParam);
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
            categoriesService.getCategoriesWithPackage(categoriesIds)
                    .onSuccess(result -> {
                        if (result != null) {
                            ctx.response()
                                    .putHeader("content-type", "application/json")
                                    .end(Json.encode(result));
                        } else {
                            JsonObject error = new JsonObject()
                                    .put("error", "Categories with id " + categoriesIds + " does not exist")
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
        //Get categories by ID
        router.get("/categories/:id").handler(ctx -> {
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

            categoriesService.getCategoryById(id)
                    .onSuccess(categories -> {
                        if (categories != null) {
                            ctx.response()
                                    .putHeader("content-type", "application/json")
                                    .end(Json.encode(categories));
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

        //Create new category
        router.post("/categories").handler(ctx -> {
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

            Categories categories = body.mapTo(Categories.class);
            categoriesService.setCategory(categories)
                    .onSuccess(v -> {
                        ctx.response()
                                .setStatusCode(201)
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

        //Update categories
        router.patch("/categories/:id").handler(ctx -> {
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

            updateCategories(id, body)
                    .onSuccess(updateCategories -> {
                        ctx.response()
                                .putHeader("content-type", "application/json")
                                .end(Json.encode(updateCategories));
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

        // DELETE category
        router.delete("/categories/:id").handler(ctx -> {
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
    }

    public Router getRouter() {
        return router;
    }

    @GET
    @Path("/")
    public Future<List<Categories>> getAllCategories() {
        return categoriesService.getAllCategories();
    }

    @GET
    @Path("/{id}")
    public Future<Categories> getCategoryById(@PathParam("id") int id) {
        return categoriesService.getCategoryById(id);
    }

    @POST
    @Path("/")
    public Future<Void> createCategory(Categories category) {
        return categoriesService.setCategory(category);
    }

    @PATCH
    @Path("/{id}")
    public Future<Categories> updateCategories(@PathParam("id") int id, JsonObject updateCategories) {
        return categoriesService.setCategory(updateCategories.mapTo(Categories.class))
                .compose(v -> categoriesService.getCategoryById(id));
    }

    @DELETE
    @Path("/{id}")
    public Future<Void> deleteCategory(@PathParam("id") int id) {
        return categoriesService.deleteCategory(id);
    }

}
