package org.example.database.mapper;

import org.apache.ibatis.annotations.*;
import org.example.database.model.Packages;

import javax.ws.rs.PathParam;
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

    @Select("SELECT * FROM packages WHERE name = #{name}")
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
    List<Packages> getPackageByName(String name);

    @Select("SELECT * FROM packages WHERE creatorName = #{creatorName}")
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
    List<Packages> getPackagesByCreatorName(String creatorName);

    @Select("SELECT * FROM packages WHERE stickerCount = #{stickerCount}")
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
    List<Packages> getPackagesByStickerCount(int stickerCount);

    @Select("SELECT * FROM packages WHERE addWhatsApp = #{addWhatsApp}")
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
    List<Packages> getPackagesByAddWhatsApp(String addWhatsApp);

    @Select("SELECT * FROM packages WHERE addTelegram = #{addTelegram}")
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
    List<Packages> getPackagesByAddTelegram(String addTelegram);

    @Select("SELECT * FROM packages WHERE viewCount = #{viewCount}")
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
    List<Packages> getPackagesByViewCount(int viewCount);

    @Select("SELECT * FROM packages WHERE FIND_IN_SET (#{categoryIds}, categoryIds)")
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
    List<Packages> getPackagesByCategoryIds(@PathParam("categoryIds") int categoryIds);

    @Select("SELECT * FROM packages WHERE createdDate = #{createdDate}")
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
    List<Packages> getPackagesByCreatedDate(Long createdDate);

    @Select("SELECT * FROM packages WHERE locale = #{locale}")
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
    List<Packages> getPackagesByLocale(String locale);

    @Select("SELECT * FROM packages WHERE order = #{order}")
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
    List<Packages> getPackagesByOrder(int order);

    @Select("SELECT * FROM packages WHERE isPremium = #{isPremium}")
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
    List<Packages> getPackagesByisPremium(String isPremium);

    @Select("SELECT * FROM packages WHERE isAnimated = #{isAnimated}")
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
    List<Packages> getPackagesByisDisAnimated(String isAnimated);


    @Insert("INSERT INTO packages (name, creatorName, stickerCount, addWhatsapp, addTelegram, viewCount, categoryIds, " +
            "isDisplayed, createdDate, locale, `order`, isPremium, isAnimated) " +
            "VALUES (#{name}, #{creatorName}, #{stickerCount}, #{addWhatsApp}, #{addTelegram}, #{viewCount}, " +
            "#{categoryIds}, #{isDisplayed}, #{createdDate}, #{locale}, #{order}, #{isPremium}, #{isAnimated})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertPackage(Packages packages);

    @Update("UPDATE packages SET name=#{name}, creatorName=#{creatorName}, stickerCount=#{stickerCount}, " +
            "addWhatsapp=#{addWhatsApp}, addTelegram=#{addTelegram}, viewCount=#{viewCount}, " +
            "categoryIds=#{categoryIds}, isDisplayed=#{isDisplayed}, createdDate=#{createdDate}, " +
            "locale=#{locale}, `order`=#{order}, isPremium=#{isPremium}, isAnimated=#{isAnimated} " +
            "WHERE id=#{id}")
    void updatePackage(Packages packages);

    @Delete("DELETE FROM packages WHERE id=#{id}")
    void deletePackages(int id);

}
