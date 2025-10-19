package com.yongs.statemachine.core;

/**
 * 功能：
 * 作者：YongS
 * 日期：2025/10/19 12:51
 */
public class StateMachineException extends RuntimeException {
    public StateMachineException(String message) {
        super(message);
    }

    public StateMachineException(String message,Throwable cause){
        super(message,cause);
    }
}
