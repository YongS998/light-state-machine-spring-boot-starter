package com.yongs.statemachine.core;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * 功能：状态转移核心类
 * 描述：表示一个状态在某个事件触发下，满足条件转移到另一个状态的规则。
 * 转移过程中可执行动作并支持条件判断。
 * 作者：YongS
 * 日期：2025/10/19 12:14
 */
public class Transition<S,E> {
    //起始状态
    private final S fromState;
    //触发事件
    private final E event;
    //目标状态
    private final S toState;
    //转移的执行条件，只有条件为true，才转移
    private final Predicate<Object> condition;
    //转移发送时的动作。如日志记录，数据更新等副属性操作
    private final Consumer<Object> action;
    //该转移对象的描述
    private final String description;

    /**
     * 私有构造方法，使用构建者模式构建实例
     */
    private Transition(S fromState, E event, S toState,
                       Predicate<Object> condition, Consumer<Object> action, String description) {
        this.fromState = fromState;
        this.event = event;
        this.toState = toState;
        this.condition = condition;
        this.action = action;
        this.description = description;
    }

    // --- Getter 方法 ---

    public S getFromState() {
        return fromState;
    }

    public E getEvent() {
        return event;
    }

    public S getToState() {
        return toState;
    }

    public Predicate<Object> getCondition() {
        return condition;
    }

    public Consumer<Object> getAction() {
        return action;
    }

    public String getDescription() {
        return description;
    }

    // --- 静态内部类：构建器（Builder）模式实现

    /**
     * Transition的构建器类，通过链式调用构建实例
     *
     * 示例：
     * Transition<String, String> transition = new Transition.Builder<String, String>()
     *     .from("IDLE")
     *     .on("START")
     *     .to("RUNNING")
     *     .when(ctx -> ((MyContext) ctx).isValid())
     *     .perform(ctx -> System.out.println("Started!"))
     *     .description("Idle to Running on START event")
     *     .build();
     * @param <S>
     * @param <E>
     */
    public static class Builder<S,E>{
        private S fromState;
        private E event;
        private S toState;
        private Predicate<Object> condition;
        private Consumer<Object> action;
        private String description;

        /**
         * 设置起始状态
         */
        public Builder<S,E> from(S fromState){
            this.fromState = fromState;
            return this;
        }

        /**
         * 设置触发事件
         */
        public Builder<S,E> on(E event){
            this.event = event;
            return this;
        }

        /**
         * 设置目标状态
         */
        public Builder<S,E> to(S toState){
            this.toState = toState;
            return this;
        }

        /**
         * 设置转移的条件
         */
        public Builder<S,E> when(Predicate<Object> condition){
            this.condition = condition;
            return this;
        }

        /**
         * 设置转移发生时的执行动作
         */
        public Builder<S,E> perform(Consumer<Object> action){
            this.action = action;
            return this;
        }

        /**
         * 设置转移的描述信息
         */
        public Builder<S,E> description(String description){
            this.description = description;
            return this;
        }

        /**
         * 构建并返回不可变的Transition实例
         */
        public Transition<S,E> build(){
            return new Transition<>(fromState,event,toState,condition,action,description);
        }
    }
}
