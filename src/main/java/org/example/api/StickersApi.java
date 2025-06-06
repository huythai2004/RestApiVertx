package org.example.api;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.example.database.model.Stickers;
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

    public StickersApi(CacheService cacheService) {
        this.stickersService = new StickersService(cacheService);
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
