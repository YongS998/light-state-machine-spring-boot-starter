package com.yongs.statemachine.support;

import com.yongs.statemachine.core.SimpleStateMachine;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 功能：状态机注册中心
 *
 * 统一管理状态机实例，添加，查找，移除
 *
 * 作者：YongS
 * 日期：2025/10/19 14:25
 */
@Component//Spring容器管理
public class StateMachineRegistry {

    /**
     * 内部存储容器
     */
    private final Map<String, SimpleStateMachine<?,?>> stateMachines = new ConcurrentHashMap<>();

    /**
     * 注册一个状态机实例
     */
    public <S,E> void registerStateMachine(String machineId,SimpleStateMachine<S,E> stateMachine){
        stateMachines.put(machineId,stateMachine);
    }

    /**
     * 根据machineId获取已注册状态机实例
     */
    @SuppressWarnings("unchecked")
    public <S,E> SimpleStateMachine<S,E> getStateMachine(String machineId){
        return (SimpleStateMachine<S, E>) stateMachines.get(machineId);
    }

    /**
     * 注销一个已注册的状态机实例
     */
    public void unregisterStateMachine(String machineId){
        stateMachines.remove(machineId);
    }

    /**
     * 获取所有已注册的状态机的ID集合
     */
    public Set<String> getAllMachineIds(){
        return stateMachines.keySet();
    }

    /**
     * 检查某个ID的状态机是否已注册
     */
    public boolean containsStateMachine(String machineId){
        return stateMachines.containsKey(machineId);
    }
}
