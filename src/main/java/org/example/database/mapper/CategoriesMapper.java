package org.example.database.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.database.model.Categories;

import java.util.List;

public interface CategoriesMapper {
    List<Categories> getAllCategories();

    @Select("SELECT * FROM categories")
    Categories getCategoryById(int id);

    @Insert("INSERT INTO categories (name, url, locale, order, isDisplayed, packageCount, createdDate)" +
            " VALUES (#{name}, #{url}, #{locale}, #{order}, #{isDisplayed}, #{packageCount}, #{createdDate})")
    void insertCategory(Categories category);

    @Update("UPDATE categories SET name=#{name}, url=#{url}, locale=#{locale}, order=#{order}, isDisplayed=#{isDisplayed}," +
            " packageCount=#{packageCount}, createdDate=#{createdDate} WHERE id=#{id}")
    void updateCategory(Categories category);

    @Delete("DELETE FROM categories WHERE id=#{id}")
    void deleteCategory(int id);
}
