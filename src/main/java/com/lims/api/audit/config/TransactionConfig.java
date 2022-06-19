package com.lims.api.audit.config;

import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
public class TransactionConfig {

    @Bean
    public DataSourceTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

//    @Bean
//    public TransactionInterceptor transactionAdvice() {
//        List<RollbackRuleAttribute> rollbackRules = Collections.singletonList(new RollbackRuleAttribute(Exception.class));
//
//        RuleBasedTransactionAttribute transactionAttribute = new RuleBasedTransactionAttribute();
//        transactionAttribute.setRollbackRules(rollbackRules);
//        transactionAttribute.setName("*");
//
//        MatchAlwaysTransactionAttributeSource attributeSource = new MatchAlwaysTransactionAttributeSource();
//        attributeSource.setTransactionAttribute(transactionAttribute);
//
//        return new TransactionInterceptor(transactionManager(), attributeSource);
//    }
//
//    @Bean
//
    public Advisor txAdviceAdvisor() {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
         pointcut.setExpression("(execution(* *..*.service..*.*(..))");
        return new DefaultPointcutAdvisor(pointcut, txAdvice());

    }

}