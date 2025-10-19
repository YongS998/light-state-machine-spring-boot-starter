package com.yongs.statemachine.core;

import com.yongs.statemachine.event.StateTransitionEvent;
import org.springframework.context.ApplicationContext;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * 功能：简单状态机实现（线程安全）
 *
 * 该类实现了基于事件驱动的有限状态机（Finite State Machine）。
 * 支持定义状态之间的转移规则，并在事件触发时执行条件判断、动作执行以及发布状态转换事件。
 *
 * 核心特性：
 * - 使用嵌套 Map 存储转移规则：Map<状态, Map<事件, 转移>>
 * - 支持条件判断（Predicate）和转移动作（Consumer）
 * - 集成 Spring 事件机制，支持监听状态转换前后事件
 * - 提供查询接口：检查是否允许转移、获取可用事件等
 *
 * 作者：YongS
 * 日期：2025/10/19 12:32
 */
public class SimpleStateMachine<S,E> {

    /**
     * 状态机唯一标识：ID，区分多个状态机实例
     */
    private final String machineId;

    /**
     * 状态转移规则存储
     * 外层Map：key为起始状态，value为该状态的所有可能的转移映射
     * 内存：key为触发事件，value为对应的Transition对象
     *
     * concurrentHashMap线程安全
     */
    private final Map<S,Map<E,Transition<S,E>>> transitions = new ConcurrentHashMap<>();

    /**
     * Spring 应用上下文，发布状态切换相关事件
     * 允许外部组件通过@EventListener监听状态变化
     */
    private final ApplicationContext applicationContext;

    /**
     * 构造方法
     */
    public SimpleStateMachine(String machineId,ApplicationContext applicationContext){
        this.machineId = machineId;
        this.applicationContext = applicationContext;
    }

    /**
     * 添加单个状态转移规则到状态机中
     * 如果已存在相同转移，会覆盖
     */
    public void addTransition(Transition<S,E> transition){
        transitions
                //computeIfAbsent,如果键不存在或值为null时，初始化为一个Map
                //返回值就是返回value，不存在则是你指定的返回
                .computeIfAbsent(transition.getFromState(),k -> new ConcurrentHashMap<>())
                .put(transition.getEvent(),transition);
    }

    /**
     * 批量添加多个状态规则
     */
    public void addTransitions(Transition<S,E>... transitions){
        for (Transition<S, E> transition : transitions) {
            addTransition(transition);
        }
    }

    /**
     * 触发一个事件
     * 这是状态机的核心方法，执行完整的转移流程：
     * 1. 查找转移规则
     * 2. 发布“转移前”事件
     * 3. 检查条件是否满足
     * 4. 执行转移动作
     * 5. 发布“转移后”事件
     *
     * @param currentState 当前状态
     * @param event        触发的事件
     * @param context      上下文对象，传递给 condition 和 action 使用（如业务数据）
     * @return 新的状态（toState）
     * @throws StateMachineException 当状态不存在、不允许事件或条件/动作执行失败时抛出
     */
    public S fireEvent(S currentState,E event,Object context){
        //获取当前状态对应的所有转移规则
        Map<E, Transition<S, E>> stateTransitions = transitions.get(currentState);

        if (stateTransitions == null){
            throw new StateMachineException(
                    "该状态 [%s] 没有定义任何状态转移规则".formatted(currentState));
        }

        //查找该事件对应的转移规则
        Transition<S, E> transition = stateTransitions.get(event);
        if (transition == null){
            throw new StateMachineException(
                    "状态 [%s] 不允许事件 [%s]".formatted(currentState,event));
        }

        // === 转移前，发布事件通知 ===
        applicationContext.publishEvent(new StateTransitionEvent.Before<>(
                machineId,transition.getFromState(),transition.getToState(),transition.getToState(),context));

        // === 条件检查 ===
        Predicate<Object> condition = transition.getCondition();
        if (condition != null && !condition.test(context)){
            //有条件限制，并且这个条件返回为false时，异常
            throw new StateMachineException(
                    "状态转换条件不满足：[%s] -> [%s]".formatted(currentState,transition.getToState()));
        }

        // === 执行附属操作 ===
        Consumer<Object> action = transition.getAction();
        if (action != null){
            try {
                action.accept(context);
            } catch (Exception e) {
                throw new StateMachineException("状态转换动作执行失败",e);
            }
        }

        // === 获取目标状态 ===
        S newState = transition.getToState();

        // === 转移后：发布事件通知 ===
        applicationContext.publishEvent(new StateTransitionEvent.After<>(
                machineId,transition.getFromState(),transition.getEvent(),transition.getToState(),context));

        // 返回新状态
        return newState;
    }

    /**
     * 检查当前状态下是否可以触发事件（转移是否允许）
     * 只做判断，没有实际操作
     */
    public boolean canFireEvent(S currentState,E event, Object context){
        try {
            Map<E, Transition<S, E>> stateTransition = transitions.get(currentState);
            //该状态没有转移规则
            if (stateTransition == null) return false;

            Transition<S, E> transition = stateTransition.get(event);
            //该事件没有转移规则
            if (transition == null) return false;

            Predicate<Object> condition = transition.getCondition();
            //没有条件限制或者条件限制为true时，可以转移
            return condition == null || condition.test(context);
        }catch (Exception e){
            //任何异常都表示不可转移
            return false;
        }
    }

    /**
     * 获取从当前状态，对某个事件的转移规则
     */
    public Transition<S,E> getTransition(S currentState,E event){
        Map<E, Transition<S, E>> stateTransition = transitions.get(currentState);
        return stateTransition != null ? stateTransition.get(event) : null;
    }

    /**
     * 获取状态机唯一标识
     */
    public String getMachineId(){
        return this.machineId;
    }

    /**
     * 获取所有状态转移规则的不可变视图（只读）
     */
    public Map<S,Map<E,Transition<S,E>>> getTransitions(){
        return Collections.unmodifiableMap(transitions);
    }
}
