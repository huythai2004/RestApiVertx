package org.example.database.mapper;

import org.apache.ibatis.annotations.*;
import org.example.database.model.Categories;

import java.util.List;

public interface CategoriesMapper {
    @Select("SELECT * From Categories")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "name"),
            @Result(property = "url", column = "url"),
            @Result(property = "locale", column = "locale"),
            @Result(property = "order", column = "order"),
            @Result(property = "isDisplayed", column = "isDisplayed"),
            @Result(property = "packageCount", column = "packageCount"),
            @Result(property = "createdDate", column = "createdDate")
    })
    List<Categories> getAllCategories();

    @Select("SELECT * FROM categories where id = #{id}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "name"),
            @Result(property = "url", column = "url"),
            @Result(property = "locale", column = "locale"),
            @Result(property = "order", column = "order"),
            @Result(property = "isDisplayed", column = "isDisplayed"),
            @Result(property = "packageCount", column = "packageCount"),
            @Result(property = "createdDate", column = "createdDate")
    })
    Categories getCategoriesById(int id);

    @Insert("INSERT INTO categories (name, url, locale, order, isDisplayed, packageCount, createdDate)" +
            " VALUES (#{name}, #{url}, #{locale}, #{order}, #{isDisplayed}, #{packageCount}, #{createdDate})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertCategories(Categories category);

    @Update("UPDATE categories SET name=#{name}, url=#{url}, locale=#{locale}, order=#{order}, isDisplayed=#{isDisplayed}," +
            " packageCount=#{packageCount}, createdDate=#{createdDate} WHERE id=#{id}")
    void updateCategories(Categories category);

    @Delete("DELETE FROM categories WHERE id=#{id}")
    void deleteCategories(int id);
}
