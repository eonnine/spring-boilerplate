package com.lims.api.config;

import com.lims.api.common.interceptor.MybatisInterceptor;
import com.lims.api.common.service.AuditTrailService;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@MapperScan(basePackages = {"com.lims.api.**.repository"})
@RequiredArgsConstructor
public class MybatisConfig {

    private final AuditTrailService auditTrailService;

    @Bean
    public SqlSessionFactory sqlSessionFactory(ApplicationContext context, DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setMapperLocations(context.getResources("classpath:mapper/*.xml"));
        sessionFactory.setTypeAliasesPackage("com.lims.api.**.dto");
        sessionFactory.setConfigurationProperties(getProperties());
        sessionFactory.setPlugins(new MybatisInterceptor(auditTrailService));
        return sessionFactory.getObject();
    }

    private Properties getProperties() {
        Properties properties = new Properties();
        properties.setProperty("mapUnderscoreToCamelCase", "true");
        return properties;
    }

}