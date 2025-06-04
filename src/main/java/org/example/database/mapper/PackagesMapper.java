package org.example.database.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.database.model.Packages;

import java.util.List;

public interface PackagesMapper {
    @Select("SELECT * FROM packages")
    List<Packages> getAllPackages();

    Packages getPackageById(int id);

    @Insert("INSERT INTO Packages (name, creatorName, stickerCount, adWhatapps, addTelegram, viewCount, categoryIds, " +
            "isDisplayed, createdDate, locale, `order`, isPremium, isAnimated) " +
            "VALUES (#{name}, #{creatorName}, #{stickerCount}, #{adWhatapps}, #{addTelegram}, #{viewCount}, " +
            "#{categoryIds}, #{isDisplayed}, #{createdDate}, #{locale}, #{order}, #{isPremium}, #{isAnimated})")
    void insertPackage(Packages packages);

    @Update("UPDATE Packages SET name=#{name}, creatorName=#{creatorName}, stickerCount=#{stickerCount}, adWhatapps=#{adWhatapps}, "
            + "addTelegram=#{addTelegram}, viewCount=#{viewCount}, categoryIds=#{categoryIds}, isDisplayed=#{isDisplayed}, ")
    void updatePackage(Packages packages);

    @Delete("DELETE FROM Packages WHERE id=#{id}")
    void deletePackage(int id);
}
