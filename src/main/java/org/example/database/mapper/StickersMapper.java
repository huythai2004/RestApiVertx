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
            @Result(property = "locale", column = "locale"),
            @Result(property = "order", column = "order"),
            @Result(property = "viewCount", column = "viewCount"),
            @Result(property = "createdDate", column = "createdDate"),
            @Result(property = "emojis", column = "emojis"),
            @Result(property = "isPremium", column = "isPremium")
    })
    List<Stickers> getAllStickers();

    @Select("SELECT * FROM stickers WHERE url = #{url}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "url", column = "url"),
            @Result(property = "packageId", column = "packageId"),
            @Result(property = "locale", column = "locale"),
            @Result(property = "order", column = "order"),
            @Result(property = "viewCount", column = "viewCount"),
            @Result(property = "createdDate", column = "createdDate"),
            @Result(property = "emojis", column = "emojis"),
            @Result(property = "isPremium", column = "isPremium")
    })
    List<Stickers> getStickerByUrl(String url);

    @Select("SELECT * FROM stickers WHERE packageId = #{packageId}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "url", column = "url"),
            @Result(property = "packageId", column = "packageId"),
            @Result(property = "locale", column = "locale"),
            @Result(property = "order", column = "order"),
            @Result(property = "viewCount", column = "viewCount"),
            @Result(property = "createdDate", column = "createdDate"),
            @Result(property = "emojis", column = "emojis"),
            @Result(property = "isPremium", column = "isPremium")
    })
    List<Stickers> getStickerByPackageId(int packageId);

    @Select("SELECT * FROM stickers WHERE locale = #{locale}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "url", column = "url"),
            @Result(property = "packageId", column = "packageId"),
            @Result(property = "locale", column = "locale"),
            @Result(property = "order", column = "order"),
            @Result(property = "viewCount", column = "viewCount"),
            @Result(property = "createdDate", column = "createdDate"),
            @Result(property = "emojis", column = "emojis"),
            @Result(property = "isPremium", column = "isPremium")
    })
    List<Stickers> getStickerByLocale(String locale);

    @Select("SELECT * FROM stickers WHERE order = #{order}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "url", column = "url"),
            @Result(property = "packageId", column = "packageId"),
            @Result(property = "locale", column = "locale"),
            @Result(property = "order", column = "order"),
            @Result(property = "viewCount", column = "viewCount"),
            @Result(property = "createdDate", column = "createdDate"),
            @Result(property = "emojis", column = "emojis"),
            @Result(property = "isPremium", column = "isPremium")
    })
    List<Stickers> getStickerByOrder(int order);

    @Select("SELECT * FROM stickers WHERE viewCount = #{viewCount}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "url", column = "url"),
            @Result(property = "packageId", column = "packageId"),
            @Result(property = "locale", column = "locale"),
            @Result(property = "order", column = "order"),
            @Result(property = "viewCount", column = "viewCount"),
            @Result(property = "createdDate", column = "createdDate"),
            @Result(property = "emojis", column = "emojis"),
            @Result(property = "isPremium", column = "isPremium")
    })
    List<Stickers> getStickerByViewCount(int viewCount);

    @Select("SELECT * FROM stickers WHERE emojis = #{emojis}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "url", column = "url"),
            @Result(property = "packageId", column = "packageId"),
            @Result(property = "locale", column = "locale"),
            @Result(property = "order", column = "order"),
            @Result(property = "viewCount", column = "viewCount"),
            @Result(property = "createdDate", column = "createdDate"),
            @Result(property = "emojis", column = "emojis"),
            @Result(property = "isPremium", column = "isPremium")
    })
    List<Stickers> getStickerByEmojis(String emojis);

    @Select("SELECT * FROM stickers WHERE id=#{id}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "url", column = "url"),
            @Result(property = "packageId", column = "packageId"),
            @Result(property = "locale", column = "locale"),
            @Result(property = "order", column = "order"),
            @Result(property = "viewCount", column = "viewCount"),
            @Result(property = "createdDate", column = "createdDate"),
            @Result(property = "emojis", column = "emojis"),
            @Result(property = "isPremium", column = "isPremium")
    })
    Stickers getStickerById(int id);

    @Insert("INSERT INTO stickers (url, packageId, locale, `order`, viewCount, createdDate, emojis, isPremium)" +
            " VALUES (#{url}, #{packageId}, #{locale}, #{order}, #{viewCount}, #{createdDate}, #{emojis}, #{isPremium})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertSticker(Stickers sticker);

    @Update("UPDATE stickers SET url=#{url}, packageId=#{packageId}, locale=#{locale} ,`order`=#{order}, viewCount=#{viewCount}, " +
            "createdDate=#{createdDate}, emojis=#{emojis}, isPremium=#{isPremium} WHERE id=#{id}")
    void updateSticker(Stickers sticker);

    @Delete("DELETE FROM stickers WHERE id=#{id}")
    void deleteSticker(int id);
}
