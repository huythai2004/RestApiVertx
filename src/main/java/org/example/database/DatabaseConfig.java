package org.example.database;

import org.apache.ibatis.session.SqlSessionFactory;
import org.example.api.CategoriesApi;
import org.example.database.mapper.CategoriesMapper;
import org.example.database.mapper.PackagesMapper;
import org.example.database.mapper.StickersMapper;

public class DatabaseConfig {
    public static final String URL = "jdbc:mysql://localhost:3306/utf8stickit?useSSL=false&serverTimezone=UTC";
    public static final String USERNAME = "root";
    public static final String PASSWORD = "Huythai-27112004";

}
