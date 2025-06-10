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
            dataSource.setUrl("db.url");
            dataSource.setUsername("db.username");
            dataSource.setPassword("db.password");

            Environment environment = new Environment("development", new JdbcTransactionFactory(), dataSource);
            Configuration configuration = new Configuration(environment);

            sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
            System.out.println("MyBatis SQL Session Factory initialized successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static SqlSessionFactory getSqlSessionFactory() {
        System.out.println("MyBatis SQL Session Factory accessed.");
        return sqlSessionFactory;
    }
}
