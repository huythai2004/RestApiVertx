package org.example.database;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.InputStream;
import java.util.Properties;

public class MyBatisSQLSessionFactory {
    private static SqlSessionFactory sqlSessionFactory;

    public static SqlSessionFactory getSqlSessionFactory() {
        if(sqlSessionFactory == null) {
            try {
                Properties prop = new Properties();
                prop.setProperty("username", DatabaseConfig.USERNAME);
                prop.setProperty("password", DatabaseConfig.PASSWORD);
                prop.setProperty("url", DatabaseConfig.URL);
                prop.setProperty("driver", "com.mysql.cj.jdbc.Driver");

                InputStream inputStream = MyBatisSQLSessionFactory.class
                        .getClassLoader()
                        .getResourceAsStream("mybatis-config.xml");

                sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream, prop);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Error initializing MyBatis SQL Session Factory", e);
            }
        }
        return sqlSessionFactory;
    }
}
