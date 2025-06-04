package org.example.database.mapper;

import jakarta.ws.rs.GET;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.database.model.Stickers;

import java.util.List;

public interface StickersMapper {
    List<Stickers> getAllStickers();

    @Select("SELECT * FROM stickers WHERE id=#{id}")
    Stickers getStickerById(int id);

    @Insert("INSERT INTO stickers (url, packageId, order, viewCount, createdDate, emojis, isPremium)" +
            " VALUES (#{url}, #{packageId}, #{order}, #{viewCount}, #{createdDate}, #{emojis}, #{isPremium})")
    void insertSticker(Stickers sticker);

    @Update("UPDATE stickers SET url=#{url}, packageId=#{packageId}, order=#{order}, viewCount=#{viewCount}, " +
            "createdDate=#{createdDate}, emojis=#{emojis}, isPremium=#{isPremium} WHERE id=#{id}")
    void updateSticker(Stickers sticker);

    @Delete("DELETE FROM stickers WHERE id=#{id}")
    void deleteSticker(int id);
}
