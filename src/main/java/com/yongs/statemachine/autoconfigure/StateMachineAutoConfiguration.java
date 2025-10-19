package com.yongs.statemachine.autoconfigure;

import com.yongs.statemachine.support.StateMachineFactory;
import com.yongs.statemachine.support.StateMachineRegistry;
import com.yongs.statemachine.support.StateMachineTemplate;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * 功能：
 * 作者：YongS
 * 日期：2025/10/19 14:56
 */
@AutoConfiguration
@EnableConfigurationProperties(StateMachineProperties.class)
@ConditionalOnProperty(prefix = "statemachine",name = "enabled",havingValue = "true", matchIfMissing = true)
public class StateMachineAutoConfiguration {

    /**
     * 工厂Bean注入
     * @param applicationContext
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public StateMachineFactory stateMachineFactory(ApplicationContext applicationContext){
        return new StateMachineFactory(applicationContext);
    }

    /**
     * 注册中心注入
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public StateMachineRegistry stateMachineRegistry(){
        return new StateMachineRegistry();
    }

    /**
     * 模板类注入
     * @param stateMachineRegistry
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public StateMachineTemplate stateMachineTemplate(StateMachineRegistry stateMachineRegistry){
        return new StateMachineTemplate(stateMachineRegistry);
    }
}
