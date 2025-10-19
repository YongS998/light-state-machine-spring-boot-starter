package com.yongs.statemachine.annotation;

import com.yongs.statemachine.autoconfigure.StateMachineAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)//只能用于类，接口或枚举
@Retention(RetentionPolicy.RUNTIME)//保留到运行时，可反射获取
@Documented
@Import(StateMachineAutoConfiguration.class)//导入状态机自动配置类
public @interface EnableStateMachine {
}