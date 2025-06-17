package org.example.config;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.example.database.mapper.CategoriesMapper;
import org.example.database.mapper.PackagesMapper;
import org.example.database.mapper.StickersMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MyBatisUltil {
    private static SqlSessionFactory sqlSessionFactory;

    static {
        try {
            System.out.println("Starting MyBatis initialization...");
            
            // Load application.properties
            Properties properties = new Properties();
            try (InputStream propStream = Resources.getResourceAsStream("application.properties")) {
                if (propStream == null) {
                    throw new IOException("Could not find application.properties");
                }
                properties.load(propStream);
            }
            
            // Load mybatis config
            String resource = "config/mybatis-config.xml";
            System.out.println("Loading MyBatis config from: " + resource);
            
            try (InputStream inputStream = Resources.getResourceAsStream(resource)) {
                if (inputStream == null) {
                    throw new IOException("Could not find MyBatis config file: " + resource);
                }
                
                System.out.println("Building SqlSessionFactory...");
                sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream, properties);
                
            System.out.println("MyBatis SQL Session Factory initialized successfully.");
            }
        } catch (Exception e) {
            System.err.println("Error initializing MyBatis SQL Session Factory:");
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize MyBatis", e);
        }
    }

    public static SqlSessionFactory getSqlSessionFactory() {
        if (sqlSessionFactory == null) {
            throw new IllegalStateException("SqlSessionFactory not initialized");
        }
        return sqlSessionFactory;
    }
}
