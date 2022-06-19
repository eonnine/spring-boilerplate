package com.lims.api.audit.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.interceptor.*;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

@Configuration
@MapperScan(basePackages = {"com.lims.api.**.dao"})
public class MybatisConfig {
    /**
     * TODO
     * @optional
     * - transaction 묶어서 처리하는 옵션
     */

    @Bean
    public SqlSessionFactory sqlSessionFactory(ApplicationContext context, DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setMapperLocations(context.getResources("classpath:mapper/*.xml"));
        sessionFactory.setTypeAliasesPackage("com.lims.api.**.dto");
        sessionFactory.setConfigurationProperties(getProperties());
        return sessionFactory.getObject();
    }

    private Properties getProperties() {
        Properties properties = new Properties();
        properties.setProperty("mapUnderscoreToCamelCase", "true");
        return properties;
    }
}