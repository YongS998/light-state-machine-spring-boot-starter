package com.yongs.statemachine.support;

import com.yongs.statemachine.core.SimpleStateMachine;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * 功能：状态机工厂类
 *
 * 统一创建和管理SimpleStateMachine实例的工厂
 *
 * 作者：YongS
 * 日期：2025/10/19 14:13
 */
@Component//Spring容器自动扫描
public class StateMachineFactory {

    /**
     * Spring 应用上下文
     * 创建实例所需
     */
    private final ApplicationContext applicationContext;

    /**
     * 构造函数
     */
    public StateMachineFactory(ApplicationContext applicationContext){
        this.applicationContext = applicationContext;
    }

    /**
     * 创建一个指定ID的状态机实例
     */
    public <S,E> SimpleStateMachine<S,E> createStateMachine(String machineId){
        return new SimpleStateMachine<>(machineId, applicationContext);
    }

    /**
     * 创建指定ID和类型的状态机实例
     */
    public <S,E> SimpleStateMachine<S,E> createStateMachine(
            String machineId,
            Class<S> stateType,
            Class<E> eventType
    ){
        return new SimpleStateMachine<>(machineId,applicationContext);
    }
}
