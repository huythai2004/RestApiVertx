package org.example.api;

import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.example.database.model.Packages;
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

    public PackagesApi(CacheService cacheService) {
        this.packagesService = new PackagesService(cacheService);
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
    public Future<Void> createPackage(RoutingContext context) {
        try {
            Packages pkg = context.getBodyAsJson().mapTo(Packages.class);
            return packagesService.setPackage(pkg)
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
    public Future<Void> updatePackage(@PathParam("id") int id, RoutingContext context) {
        try {
            Packages pkg = context.getBodyAsJson().mapTo(Packages.class);
            pkg.setId(id);
            return packagesService.setPackage(pkg)
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
    public Future<Void> deletePackage(@PathParam("id") int id) {
        return packagesService.deletePackage(id)
            .recover(err -> {
                JsonObject error = new JsonObject()
                    .put("error", err.getMessage())
                    .put("status", 500);
                return Future.failedFuture(error.encode());
            });
    }

    public void registerRouterPackages (Router router) {
        router.get("/packages").handler(ctx -> getAllPackages()
                .onSuccess(result -> ctx.response().putHeader("Content-Type", "application/json").end(Json.encode(result)))
                .onFailure(err -> ctx.response().setStatusCode(500).end(err.getMessage())));

        router.get("/pacakges/:id").handler(ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            getPackageById(id)
                    .onSuccess(result -> ctx.response().putHeader("Content-Type", "application/json").end(Json.encode(result))
                            .onFailure(err -> ctx.response().setStatusCode(500).end(err.getMessage())));
        });

        router.post("/packages").handler(ctx -> createPackage(ctx)
                .onSuccess(result -> ctx.response().putHeader("Content-Type", "application/json").setStatusCode(201).end(Json.encode(result)))
        .onFailure(err -> ctx.response().setStatusCode(400).end(err.getMessage())));

        router.put("/packages/:id").handler(ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            updatePackage(id, ctx)
                .onSuccess(result -> ctx.response().setStatusCode(200).end(Json.encode(result)))
                .onFailure(err -> ctx.response().setStatusCode(400).end(err.getMessage()));
        });

        router.delete("/packages/:id").handler(ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            deletePackage(id)
                .onSuccess(result -> ctx.response().setStatusCode(204).end())
                .onFailure(err -> ctx.response().setStatusCode(500).end(err.getMessage()));
        });
    }
}
