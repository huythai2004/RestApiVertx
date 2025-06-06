package org.example.api;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
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

    public CategoriesApi(CacheService cacheService) {
        this.categoriesService = new CategoriesService(cacheService);
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
}
