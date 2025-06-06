package org.example.config;

import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;


public class MyBatisUltil {
    private static SqlSessionFactory sqlSessionFactory;

    static {
        try {
            PooledDataSource dataSource = new PooledDataSource();
            dataSource.setDriver("com.mysql.cj.jdbc.Driver");
            dataSource.setUrl("jdbc:mysql://localhost:3306/utf8stickit?useSSL=true&serverTimezone=UTC");
            dataSource.setUsername("root");
            dataSource.setPassword("Huythai-27112004");

            Environment environment = new Environment("development", new JdbcTransactionFactory(), dataSource);
            Configuration configuration = new Configuration(environment);

            sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static SqlSessionFactory getSqlSessionFactory() {
        return sqlSessionFactory;
    }
}
