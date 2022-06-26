package com.lims.api.config;

import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.interceptor.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class TransactionConfig {

    private final TransactionManager transactionManager;

    public TransactionConfig(DataSourceTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Bean
    public TransactionInterceptor transactionAdvice() {
        RuleBasedTransactionAttribute txAttribute = new RuleBasedTransactionAttribute();
        txAttribute.setRollbackRules(Collections.singletonList(new RollbackRuleAttribute(Exception.class)));
        txAttribute.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        Map<String, TransactionAttribute> txMethods = new HashMap<String, TransactionAttribute>();
        txMethods.put("*", txAttribute);

        NameMatchTransactionAttributeSource txAttributeSource = new NameMatchTransactionAttributeSource();
        txAttributeSource.setNameMap(txMethods);

        return new TransactionInterceptor(transactionManager, txAttributeSource);
    }

    @Bean
    public Advisor transactionAdviceAdvisor() {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("execution(* com.lims.api..service.*.*(..))");
        return new DefaultPointcutAdvisor(pointcut, transactionAdvice());
    }

}