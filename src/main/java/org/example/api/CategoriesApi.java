package org.example.api;

import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
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
        router.route().handler(BodyHandler.create());

        // Get all categories
        router.get("/categories").handler(ctx -> getAllCategories()
                .onSuccess(result -> ctx.response().putHeader("Content-Type", "application/json")
                        .end(Json.encode(result)))
                .onFailure(err -> ctx.response().setStatusCode(500)
                        .end("Error: " + err.getMessage())));

        // Get category by ID
        router.get("/categories/:id").handler(ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            getCategoryById(id)
                    .onSuccess(result -> ctx.response().putHeader("Content-Type", "application/json")
                            .end(Json.encode(result)))
                    .onFailure(err -> ctx.response().setStatusCode(500)
                            .end("Error: " + err.getMessage()));
        });

        // Create new category
        router.post("/categories").handler(ctx -> createCategory(ctx)
                .onSuccess(v -> ctx.response().setStatusCode(201).end())
                .onFailure(err -> ctx.response().setStatusCode(500)
                        .end("Error: " + err.getMessage())));

        // Update category
        router.put("/categories/:id").handler(ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            updateCategory(id, ctx)
                    .onSuccess(result -> ctx.response().setStatusCode(201).end())
                    .onFailure(err -> ctx.response().setStatusCode(500)
                            .end("Error: " + err.getMessage()));
        });

        // Delete category
        router.delete("/categories/:id").handler(ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            deleteCategory(id)
                    .onSuccess(v -> ctx.response().setStatusCode(204).end())
                    .onFailure(err -> ctx.response().setStatusCode(500)
                            .end("Error: " + err.getMessage()));
        });
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
    public Future<Void> createCategory(RoutingContext context) {
        try {
            Categories category = context.getBodyAsJson().mapTo(Categories.class);
            return categoriesService.setCategory(category)
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
    public Future<Void> updateCategory(@PathParam("id") int id, RoutingContext context) {
        try {
            Categories category = context.getBodyAsJson().mapTo(Categories.class);
            category.setId(id);
            return categoriesService.setCategory(category)
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
    public Future<Void> deleteCategory(@PathParam("id") int id) {
        return categoriesService.deleteCategory(id)
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
