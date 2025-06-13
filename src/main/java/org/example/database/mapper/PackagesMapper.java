package org.example.database.mapper;

import org.apache.ibatis.annotations.*;
import org.example.database.model.Packages;

import java.util.List;

public interface PackagesMapper {
    @Select("SELECT * FROM packages")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "name"),
            @Result(property = "creatorName", column = "creatorName"),
            @Result(property = "stickerCount", column = "stickerCount"),
            @Result(property = "addWhatsApp", column = "addWhatsapp"),
            @Result(property = "addTelegram", column = "addTelegram"),
            @Result(property = "viewCount", column = "viewCount"),
            @Result(property = "categoryIds", column = "categoryIds"),
            @Result(property = "isDisplayed", column = "isDisplayed"),
            @Result(property = "createdDate", column = "createdDate"),
            @Result(property = "locale", column = "locale"),
            @Result(property = "order", column = "order"),
            @Result(property = "isPremium", column = "isPremium"),
            @Result(property = "isAnimated", column = "isAnimated")
    })
    List<Packages> getAllPackages();

    @Select("SELECT * FROM packages WHERE id = #{id}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "name"),
            @Result(property = "creatorName", column = "creatorName"),
            @Result(property = "stickerCount", column = "stickerCount"),
            @Result(property = "addWhatsApp", column = "addWhatsapp"),
            @Result(property = "addTelegram", column = "addTelegram"),
            @Result(property = "viewCount", column = "viewCount"),
            @Result(property = "categoryIds", column = "categoryIds"),
            @Result(property = "isDisplayed", column = "isDisplayed"),
            @Result(property = "createdDate", column = "createdDate"),
            @Result(property = "locale", column = "locale"),
            @Result(property = "order", column = "order"),
            @Result(property = "isPremium", column = "isPremium"),
            @Result(property = "isAnimated", column = "isAnimated")
    })
    Packages getPackageById(int id);

    @Insert("INSERT INTO Packages (name, creatorName, stickerCount, addWhatsapp, addTelegram, viewCount, categoryIds, " +
            "isDisplayed, createdDate, locale, `order`, isPremium, isAnimated) " +
            "VALUES (#{name}, #{creatorName}, #{stickerCount}, #{addWhatsApp}, #{addTelegram}, #{viewCount}, " +
            "#{categoryIds}, #{isDisplayed}, #{createdDate}, #{locale}, #{order}, #{isPremium}, #{isAnimated})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertPackage(Packages packages);

    @Update("UPDATE Packages SET name=#{name}, creatorName=#{creatorName}, stickerCount=#{stickerCount}, " +
            "addWhatsapp=#{addWhatsApp}, addTelegram=#{addTelegram}, viewCount=#{viewCount}, " +
            "categoryIds=#{categoryIds}, isDisplayed=#{isDisplayed}, createdDate=#{createdDate}, " +
            "locale=#{locale}, `order`=#{order}, isPremium=#{isPremium}, isAnimated=#{isAnimated} " +
            "WHERE id=#{id}")
    void updatePackage(Packages packages);

    @Delete("DELETE FROM Packages WHERE id=#{id}")
    void deletePackages( int id);
}
