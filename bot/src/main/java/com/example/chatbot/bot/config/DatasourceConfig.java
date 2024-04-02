package com.example.chatbot.bot.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;


@Configuration
public class DatasourceConfig {

    @Autowired
    DataSource dataSource;
    @Autowired
    ApplicationContext applicationContext;

    //这里通过SqlSessionFactoryBean去生成SqlSessionFactory对象
    @Bean
    public SqlSessionFactory mcpSqlSessionFactory() throws Exception {
        SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource);
//        sessionFactoryBean.setMapperLocations(applicationContext
//                .getResources("classpath:com/example/chatbot/bot/mapper/xml/**.xml"));
        sessionFactoryBean.setTypeAliasesPackage("com.example.chatbot.bot.entity");
        return sessionFactoryBean.getObject();
    }

    //这里通过SqlSessionFactory 获取SqlSessionTemplate
    @Bean
    public SqlSessionTemplate mcpSqlSessionTemplate() throws Exception {
        SqlSessionTemplate sqlSessionTemplate = new SqlSessionTemplate(mcpSqlSessionFactory());
        return sqlSessionTemplate;
    }

    //这个是配置的事务处理器
    @Bean
    public DataSourceTransactionManager mcpDataSourceTransactionManager() {
        return new DataSourceTransactionManager(dataSource);
    }
}
