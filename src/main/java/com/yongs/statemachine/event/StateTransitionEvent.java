package com.yongs.statemachine.event;

import org.springframework.context.ApplicationEvent;

/**
 * 功能：状态转移事件基类
 *
 * 状态发生改变时，发布的事件，可以被@EventListener监听到
 *
 * 作者：YongS
 * 日期：2025/10/19 14:03
 */
public class StateTransitionEvent<S,E> extends ApplicationEvent {

    /**
     * 状态机唯一ID
     */
    private final String machineId;

    /**
     * 起始状态
     */
    private final S fromState;

    /**
     * 触发事件
     */
    private final E event;

    /**
     * 目标状态
     */
    private final S toState;

    /**
     * 业务上下文
     */
    private final Object context;

    /**
     * 构造函数
     */
    public StateTransitionEvent(String machineId,S fromState, E event,S toState,Object context){
        super(context);//父级构造函数，设置事件源
        this.machineId = machineId;
        this.fromState = fromState;
        this.event = event;
        this.toState = toState;
        this.context = context;
    }

    // --- Getter 方法 ---

    public String getMachineId() {
        return machineId;
    }

    public S getFromState() {
        return fromState;
    }

    public E getEvent() {
        return event;
    }

    public S getToState() {
        return toState;
    }

    public Object getContext() {
        return context;
    }

    // --- 静态内部类：转移前事件 ---
    public static class Before<S,E> extends StateTransitionEvent<S,E>{
        public Before(String machineId, S fromState, E event, S toState, Object context) {
            super(machineId, fromState, event, toState, context);
        }
    }

    // --- 静态内部类：转移后事件 ---
    public static class After<S,E> extends StateTransitionEvent<S,E>{
        public After(String machineId, S fromState, E event, S toState, Object context) {
            super(machineId, fromState, event, toState, context);
        }
    }
}
