package com.yongs.statemachine.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 功能：
 * 作者：YongS
 * 日期：2025/10/19 14:54
 */
@ConfigurationProperties(prefix = "statemachine")
public class StateMachineProperties {

    /**
     * 是否启用状态机自动配置
     */
    private boolean enabled = true;

    /**
     * 状态机配置前缀
     */
    private String prefix = "statemachine";

    /**
     * 是否开启调试模式
     */
    private boolean debug = false;

    /**
     * 是否开启性能监控
     */
    private boolean monitor = false;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean isMonitor() {
        return monitor;
    }

    public void setMonitor(boolean monitor) {
        this.monitor = monitor;
    }
}
