package com.yongs.statemachine.support;

import com.yongs.statemachine.core.SimpleStateMachine;
import org.springframework.stereotype.Component;

/**
 * 功能：状态机操作模板类
 *
 * 提供统一、便捷的高级接口操作状态机实例
 *
 * 作者：YongS
 * 日期：2025/10/19 14:21
 */
@Component//Spring容器管理
public class StateMachineTemplate {

    /**
     * 状态机注册列表
     */
    private final StateMachineRegistry stateMachineRegistry;

    /**
     * 构造函数
     */
    public StateMachineTemplate(StateMachineRegistry stateMachineRegistry){
        this.stateMachineRegistry = stateMachineRegistry;
    }

    /**
     * 触发指定状态机的事件，执行状态转移
     */
    public <S,E> S fireEvent(String machineId,S currentState,E event,Object context){
        //从注册中心获取状态机实例
        SimpleStateMachine<S, E> stateMachine = stateMachineRegistry.getStateMachine(machineId);

        //是否真实存在?
        if (stateMachine == null){
            throw new IllegalArgumentException("状态机未找到："+machineId);
        }

        //委托给具体的状态机执行事件触发逻辑
        return stateMachine.fireEvent(currentState, event, context);
    }

    /**
     * 检查当前状态是否允许触发某个事件（预检）
     */
    public <S,E> boolean canFireEvent(String machineId,S currentState,E event,Object context){
        SimpleStateMachine<S, E> stateMachine = stateMachineRegistry.getStateMachine(machineId);

        //不存在，则不可转换
        if (stateMachine == null){
            return false;
        }

        return stateMachine.canFireEvent(currentState,event,context);
    }
}
