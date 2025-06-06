package org.example.database.mapper;

import org.apache.ibatis.annotations.*;
import org.example.database.model.Stickers;

import java.util.List;

public interface StickersMapper {
    @Select("SELECT * FROM stickers")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "url", column = "url"),
            @Result(property = "packageId", column = "packageId"),
            @Result(property = "order", column = "order"),
            @Result(property = "viewCount", column = "viewCount"),
            @Result(property = "createdDate", column = "createdDate"),
            @Result(property = "emojis", column = "emojis"),
            @Result(property = "isPremium", column = "isPremium")
    })
    List<Stickers> getAllStickers();

    @Select("SELECT * FROM stickers WHERE id=#{id}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "url", column = "url"),
            @Result(property = "packageId", column = "packageId"),
            @Result(property = "order", column = "order"),
            @Result(property = "viewCount", column = "viewCount"),
            @Result(property = "createdDate", column = "createdDate"),
            @Result(property = "emojis", column = "emojis"),
            @Result(property = "isPremium", column = "isPremium")
    })
    Stickers getStickerById(int id);

    @Insert("INSERT INTO stickers (url, packageId, order, viewCount, createdDate, emojis, isPremium)" +
            " VALUES (#{url}, #{packageId}, #{order}, #{viewCount}, #{createdDate}, #{emojis}, #{isPremium})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertSticker(Stickers sticker);

    @Update("UPDATE stickers SET url=#{url}, packageId=#{packageId}, order=#{order}, viewCount=#{viewCount}, " +
            "createdDate=#{createdDate}, emojis=#{emojis}, isPremium=#{isPremium} WHERE id=#{id}")
    void updateSticker(Stickers sticker);

    @Delete("DELETE FROM stickers WHERE id=#{id}")
    void deleteSticker(int id);
}
