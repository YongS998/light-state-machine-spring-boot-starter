# 所需设计模式

## 工厂模式（Factory）

统一对象创建，复杂对象创建
类比：汽车工厂创建汽车，我告诉工厂我要什么汽车，你给我创建并返回
```java
// 工厂：专门负责"制造东西"
汽车工厂.制造汽车("SUV") → 返回一辆SUV
汽车工厂.制造汽车("轿车") → 返回一辆轿车

// 代码中：
StateMachineFactory.创建状态机("订单状态机") → 返回订单状态机实例
```

## 注册表（Registry）

很多对象统一管理，提供对象的快速查找获取
类比：停车场管理系统，进行车辆的登记，查询，移除
```java
// 注册表：专门负责"登记和管理实例"
停车场.登记车辆("车牌A123", 奔驰车)  // 登记
停车场.查找车辆("车牌A123") → 返回奔驰车  // 查找
停车场.移除车辆("车牌A123")           // 移除

// 代码中：
StateMachineRegistry.注册("订单状态机", 状态机实例)
StateMachineRegistry.获取("订单状态机") → 返回状态机实例
```
## 模板模式（Template）

提供统一规范的方法调用模板
类比：银行标准化流程
```java
// 模板：提供"标准化操作流程"
银行柜台.办理转账(账户A, 账户B, 金额) {
    1. 验证身份()
    2. 检查余额()
    3. 执行转账()
    4. 发送通知()
    // 所有转账都走这个标准化流程
}

// 代码中：
StateMachineTemplate.触发事件(状态机名, 当前状态, 事件, 数据) {
    1. 查找状态机()
    2. 验证权限()
    3. 执行转换()
    4. 记录日志()
}
```

## 三者协作关系

```java
用户请求
    ↓
StateMachineTemplate.触发事件()           ← 用户只跟模板交互
    ↓
Template 向 Registry 查询状态机           ← 模板找注册表要状态机
    ↓  
Registry 返回对应的状态机实例             ← 注册表知道所有状态机在哪
    ↓
Template 调用状态机执行业务逻辑           ← 模板调用具体状态机
    ↓
状态机由 Factory 创建并注册到 Registry   ← 状态机生命周期管理
```

## 特点和条件

**使用 Factory 当：**

- ✅ 对象创建过程复杂（需要很多配置）
    
- ✅ 需要根据不同参数创建不同类型对象
    
- ✅ 想要隐藏对象创建的细节
    

**使用 Registry 当：**

- ✅ 系统中有多个同类型对象需要管理
    
- ✅ 需要通过名字/ID快速查找对象
    
- ✅ 需要跟踪对象的生命周期
    

**使用 Template 当：**

- ✅ 某个操作有固定流程步骤
    
- ✅ 想在操作前后统一添加逻辑（日志、监控、事务）
    
- ✅ 想要简化复杂操作的调用方式

```java
新功能开发时，问自己这些问题：

1. 这个功能需要创建复杂对象吗？
   ├─ 是 → 使用 Factory 模式
   └─ 否 → 直接 new 或者依赖注入

2. 系统中有多个同类实例需要管理吗？
   ├─ 是 → 使用 Registry 模式  
   └─ 否 → 单个实例不需要注册表

3. 这个操作有固定流程或需要统一增强吗？
   ├─ 是 → 使用 Template 模式
   └─ 否 → 直接调用方法

4. 需要结合使用吗？
   ├─ 创建+管理 → Factory + Registry
   ├─ 管理+使用 → Registry + Template  
   └─ 创建+管理+使用 → Factory + Registry + Template
```

# 什么是状态机?

1. 状态机本质是一个规则引擎

- **当前状态**：对象现在处于什么情况
    
- **允许的操作**：在当前状态下能做什么
    
- **状态变化**：操作后会变成什么状态

2. 核心组件：3大

```tex
状态机 = 状态规则 + 事件处理器 + 上下文管理
```

- 状态规则：Transition
```java
// 这就是一条规则："如果现在是待支付状态，收到支付事件，就变成已支付状态"
规则 = {
    当前状态: "待支付",
    触发事件: "支付", 
    目标状态: "已支付",
    条件: "金额必须大于0",      // 可选：额外检查
    动作: "发送支付通知"        // 可选：额外操作
}
```

- 状态机引擎：`SimpleStateMachine`
```java
// 状态机的工作流程
收到事件 → 查找规则 → 检查条件 → 执行动作 → 更新状态
    ↓         ↓         ↓         ↓         ↓
  用户操作  规则存在？  业务条件？  业务逻辑  状态变化
```

- 状态机管理：（Registry+Template）
```java
// 管理多个状态机，提供统一接口
状态机注册表 = {
    "订单状态机": 订单状态规则,
    "用户状态机": 用户状态规则,
    "审批状态机": 审批状态规则
}
```

# 项目结构图

```java
light-state-machine-spring-boot-starter/
├── src/main/java/
│   └── com/
│       └── yongs/
│           └── statemachine/
│               ├── autoconfigure/
│               │   ├── StateMachineAutoConfiguration.java
│               │   └── StateMachineProperties.java
│               ├── core/
│               │   ├── SimpleStateMachine.java
│               │   ├── Transition.java
│               │   └── StateMachineException.java
│               ├── annotation/
│               │   ├── EnableStateMachine.java
│               │   └── StateTransition.java
│               ├── support/
│               │   ├── StateMachineFactory.java
│               │   ├── StateMachineTemplate.java
│               │   └── StateMachineRegistry.java
│               └── event/
│                   ├── StateTransitionEvent.java
│                   └── StateMachineEventListener.java
├── src/main/resources/
│   └── META-INF/
│       ├── spring/
│       │   └── org.springframework.boot.autoconfigure.AutoConfiguration.imports
│       └── additional-spring-configuration-metadata.json
└── pom.xml
```

# 项目依赖

```xml
<!--    打包方式-->  
    <packaging>jar</packaging>
<!--    父类SpringBoot-->  
    <parent>  
        <groupId>org.springframework.boot</groupId>  
        <artifactId>spring-boot-starter-parent</artifactId>  
        <version>3.5.6</version>  
        <relativePath/>  
    </parent>

    <dependencies>  
<!--        springboot-->  
        <dependency>  
            <groupId>org.springframework.boot</groupId>  
            <artifactId>spring-boot-starter</artifactId>  
        </dependency>  
<!--        配置相关-->  
        <dependency>  
            <groupId>org.springframework.boot</groupId>  
            <artifactId>spring-boot-configuration-processor</artifactId>  
        </dependency>  
<!--        测试相关-->  
        <dependency>  
            <groupId>org.springframework.boot</groupId>  
            <artifactId>spring-boot-starter-test</artifactId>  
        </dependency>  
    </dependencies>
    
    <build>  
        <plugins>  
            <plugin>  
                <!--                maven compiler 插件-->  
                <groupId>org.apache.maven.plugins</groupId>  
                <artifactId>maven-compiler-plugin</artifactId>  
                <version>3.14.0</version>  
<!--                编译后兼容Java17-->  
                <configuration>  
                    <source>17</source>  
                    <target>17</target>  
                </configuration>  
            </plugin>  
        </plugins>  
    </build>
```

# 状态机核心类

## Transition（状态转换类）

```java
/**  
 * 功能：状态转移核心类  
 * 描述：表示一个状态在某个事件触发下，满足条件转移到另一个状态的规则。  
 * 转移过程中可执行动作并支持条件判断。  
 * 作者：YongS  
 * 日期：2025/10/19 12:14  
 */public class Transition<S,E> {  
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
     *     .build();     * @param <S>  
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
```

###  构建者模式

为类添加静态内部类，实现链式调用的方式创建对象实例

```java
    /**
     * Transition 的构建器类，提供流畅的 API（Fluent API）用于构建 Transition 实例。
     * 使用链式调用方式，提升代码可读性和易用性。
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
     */
```

## `SimpleStateMachine`(状态机心脏)

触发事件，判断触发是否可行

```java
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
 */public class SimpleStateMachine<S,E> {  
  
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
  
        // === 转移前，发布事件通知 TODO  
  
        // === 条件检查 ===        Predicate<Object> condition = transition.getCondition();  
        if (condition != null && !condition.test(context)){  
            //有条件限制，并且这个条件返回为false时，异常  
            throw new StateMachineException(  
                    "状态转换条件不满足：[%s] -> [%s]".formatted(currentState,transition.getToState()));  
        }  
  
        // === 执行附属操作 ===        Consumer<Object> action = transition.getAction();  
        if (action != null){  
            try {  
                action.accept(context);  
            } catch (Exception e) {  
                throw new StateMachineException("状态转换动作执行失败",e);  
            }  
        }  
  
        // === 获取目标状态 ===        S newState = transition.getToState();  
  
        // === 转移后：发布事件通知 ===        // TODO  
  
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
```

## `StateMachineException`(转换异常类)

```java
public class StateMachineException extends RuntimeException {  
    public StateMachineException(String message) {  
        super(message);  
    }  
  
    public StateMachineException(String message,Throwable cause){  
        super(message,cause);  
    }  
}
```

# 事件类（`StateTransitionEvent`）

状态转换事件类，在状态发生变化时，会发布这个事件，通知其他组件状态变化
Spring Application Context方式 可以被`@EventListener`监听

```java
/**  
 * 功能：状态转移事件基类  
 *  
 * 状态发生改变时，发布的事件，可以被@EventListener监听到  
 *  
 * 作者：YongS  
 * 日期：2025/10/19 14:03  
 */public class StateMachineEvent<S,E> extends ApplicationEvent {  
  
    /**  
     * 状态机唯一ID  
     */    private final String machineId;  
  
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
    public StateMachineEvent(String machineId,S fromState, E event,S toState,Object context){  
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
  
    // --- 静态内部类：转移前事件 ---    public static class Before<S,E> extends StateMachineEvent<S,E>{  
        public Before(String machineId, S fromState, E event, S toState, Object context) {  
            super(machineId, fromState, event, toState, context);  
        }  
    }  
  
    // --- 静态内部类：转移后事件 ---    public static class After<S,E> extends StateMachineEvent<S,E>{  
        public After(String machineId, S fromState, E event, S toState, Object context) {  
            super(machineId, fromState, event, toState, context);  
        }  
    }  
}
```

# 支持类

## 工厂类（`StateMachineFactory`）

统一创建状态机的类

```java
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
```

## 模板类（`StateMachineTemplate`）

统一操作状态机的类，可以进行原始类的增强

```java
/**  
 * 功能：状态机操作模板类  
 *  
 * 提供统一、便捷的高级接口操作状态机实例  
 *  
 * 作者：YongS  
 * 日期：2025/10/19 14:21  
 */@Component//Spring容器管理  
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
```

## 注册表类（`StateMachineRegistry`）

状态机注册中心，统一管理状态机实例，实现状态机的存储，查找和移除

```java
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
```

# 注解类

开启状态机注解
```java
@Target(ElementType.TYPE)//只能用于类，接口或枚举  
@Retention(RetentionPolicy.RUNTIME)//保留到运行时，可反射获取  
@Documented  
@Import(StateMachineAutoConfiguration.class)//导入状态机自动配置类  
public @interface EnableStateMachine {  
}
```

# 自动配置类

## `StateMachineProperties`

```java
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
```

## `StateMachineAutoConfiguration`

```java
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
    @ConditionalOnMissingBean    public StateMachineFactory stateMachineFactory(ApplicationContext applicationContext){  
        return new StateMachineFactory(applicationContext);  
    }  
  
    /**  
     * 注册中心注入  
     * @return  
     */  
    @Bean  
    @ConditionalOnMissingBean    public StateMachineRegistry stateMachineRegistry(){  
        return new StateMachineRegistry();  
    }  
  
    /**  
     * 模板类注入  
     * @param stateMachineRegistry  
     * @return  
     */  
    @Bean  
    @ConditionalOnMissingBean    public StateMachineTemplate stateMachineTemplate(StateMachineRegistry stateMachineRegistry){  
        return new StateMachineTemplate(stateMachineRegistry);  
    }  
}
```

# 配置文件

## `AutoConfiguration.imports`

```imports
# Spring Boot 3 新的自动配置注册方式
com.yongs.statemachine.autoconfigure.StateMachineAutoConfiguration
```

## `additional-spring-configuration-metadata.json`

```json
{
  "properties": [
    {
      "name": "statemachine.enabled",
      "type": "java.lang.Boolean",
      "description": "Whether to enable state machine auto-configuration.",
      "defaultValue": true
    },
    {
      "name": "statemachine.prefix",
      "type": "java.lang.String",
      "description": "State machine configuration prefix.",
      "defaultValue": "statemachine"
    },
    {
      "name": "statemachine.debug",
      "type": "java.lang.Boolean",
      "description": "Whether to enable debug mode.",
      "defaultValue": false
    },
    {
      "name": "statemachine.monitor",
      "type": "java.lang.Boolean",
      "description": "Whether to enable performance monitoring.",
      "defaultValue": false
    }
  ]
}
```

# `SpringBoot`使用

## 引入依赖
```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>light-state-machine-spring-boot-starter</artifactId>
    <version>3.0.0</version>
</dependency>
```

## `application.yml`
```yml
statemachine:
  enabled: true
  debug: true
  monitor: true
```

## 启动状态机
```java
@SpringBootApplication
@EnableStateMachine
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

## 配置订单状态机
```java
@Configuration  
@Slf4j  
public class OrderStateMachineConfig {  
  
    private final StateMachineFactory stateMachineFactory;  
    private final StateMachineRegistry stateMachineRegistry;  
  
    public OrderStateMachineConfig(StateMachineFactory stateMachineFactory, StateMachineRegistry stateMachineRegistry) {  
        this.stateMachineFactory = stateMachineFactory;  
        this.stateMachineRegistry = stateMachineRegistry;  
    }  
  
    @Bean  
    public SimpleStateMachine<OrderState, OrderEvent> orderStateMachine(){  
        SimpleStateMachine<OrderState, OrderEvent> stateMachine = stateMachineFactory  
                .createStateMachine("orderStateMachine");  
        //1.支付流程  
        stateMachine.addTransition(  
                new Transition.Builder<OrderState,OrderEvent>()  
                        .from(OrderState.UNPAID)  
                        .on(OrderEvent.PAY)  
                        .to(OrderState.PAID)  
                        .when(context -> {  
                            if (context instanceof Order){  
                                Order order = (Order) context;  
                                return order.getAmount().compareTo(BigDecimal.ZERO) > 0;  
                            }  
                            return false;  
                        })  
                        .perform(context -> {  
                            Order order = (Order) context;  
                            log.info("执行支付成功逻辑，订单号: {}",order.getOrderNo());  
                        })  
                        .description("用户支付订单")  
                        .build()  
        );  
  
        // 2. 取消订单  
        stateMachine.addTransition(  
                new Transition.Builder<OrderState, OrderEvent>()  
                        .from(OrderState.UNPAID)  
                        .on(OrderEvent.CANCEL)  
                        .to(OrderState.CANCELLED)  
                        .perform(context -> {  
                            Order order = (Order) context;  
                            System.out.println("订单已取消: " + order.getOrderNo());  
                            // 释放库存、发送通知等  
                        })  
                        .description("用户取消订单")  
                        .build()  
        );  
  
        // 3. 超时取消  
        stateMachine.addTransition(  
                new Transition.Builder<OrderState, OrderEvent>()  
                        .from(OrderState.UNPAID)  
                        .on(OrderEvent.TIMEOUT)  
                        .to(OrderState.CANCELLED)  
                        .perform(context -> {  
                            Order order = (Order) context;  
                            System.out.println("订单支付超时自动取消: " + order.getOrderNo());  
                        })  
                        .description("支付超时系统自动取消")  
                        .build()  
        );  
  
        // 4. 商家发货  
        stateMachine.addTransition(  
                new Transition.Builder<OrderState, OrderEvent>()  
                        .from(OrderState.PAID)  
                        .on(OrderEvent.SHIP)  
                        .to(OrderState.SHIPPED)  
                        .perform(context -> {  
                            Order order = (Order) context;  
                            System.out.println("订单已发货: " + order.getOrderNo());  
                            // 调用物流服务、发送发货通知等  
                        })  
                        .description("商家发货")  
                        .build()  
        );  
  
        // 5. 用户确认收货  
        stateMachine.addTransition(  
                new Transition.Builder<OrderState, OrderEvent>()  
                        .from(OrderState.SHIPPED)  
                        .on(OrderEvent.RECEIVE)  
                        .to(OrderState.RECEIVED)  
                        .perform(context -> {  
                            Order order = (Order) context;  
                            System.out.println("用户已确认收货: " + order.getOrderNo());  
                            // 更新库存、计算佣金等  
                        })  
                        .description("用户确认收货")  
                        .build()  
        );  
  
        // 6. 订单完成  
        stateMachine.addTransition(  
                new Transition.Builder<OrderState, OrderEvent>()  
                        .from(OrderState.RECEIVED)  
                        .on(OrderEvent.COMPLETE)  
                        .to(OrderState.COMPLETED)  
                        .perform(context -> {  
                            Order order = (Order) context;  
                            System.out.println("订单已完成: " + order.getOrderNo());  
                            // 结算、归档等  
                        })  
                        .description("订单流程完成")  
                        .build()  
        );  
  
        // 7. 申请退款  
        stateMachine.addTransition(  
                new Transition.Builder<OrderState, OrderEvent>()  
                        .from(OrderState.PAID)  
                        .on(OrderEvent.APPLY_REFUND)  
                        .to(OrderState.AFTER_SALE)  
                        .perform(context -> {  
                            Order order = (Order) context;  
                            System.out.println("用户申请退款: " + order.getOrderNo());  
                            // 记录退款申请、通知客服等  
                        })  
                        .description("用户申请退款")  
                        .build()  
        );  
  
        // 8. 同意退款  
        stateMachine.addTransition(  
                new Transition.Builder<OrderState, OrderEvent>()  
                        .from(OrderState.AFTER_SALE)  
                        .on(OrderEvent.APPROVE_REFUND)  
                        .to(OrderState.REFUNDED)  
                        .perform(context -> {  
                            Order order = (Order) context;  
                            System.out.println("退款申请通过: " + order.getOrderNo());  
                            // 执行退款操作、更新财务记录等  
                        })  
                        .description("管理员同意退款")  
                        .build()  
        );  
  
        // 9. 拒绝退款  
        stateMachine.addTransition(  
                new Transition.Builder<OrderState, OrderEvent>()  
                        .from(OrderState.AFTER_SALE)  
                        .on(OrderEvent.REJECT_REFUND)  
                        .to(OrderState.PAID)  
                        .perform(context -> {  
                            Order order = (Order) context;  
                            System.out.println("退款申请被拒绝: " + order.getOrderNo());  
                            // 通知用户、记录原因等  
                        })  
                        .description("管理员拒绝退款")  
                        .build()  
        );  
  
        // 注册状态机  
        stateMachineRegistry.registerStateMachine("orderStateMachine", stateMachine);  
  
        return stateMachine;  
    }  
}
```

## 业务使用
```java
@Service  
public class OrderService {  
  
    @Autowired  
    private StateMachineTemplate stateMachineTemplate;  
    @Autowired  
    private OrderRepository orderRepository;  
  
    /**  
     * 支付订单  
     */  
    public void payOrder(Long orderId){  
        Order order = orderRepository.findByOrderId(1L);  
  
        OrderState newState = stateMachineTemplate.fireEvent(  
                "orderStateMachine",  
                order.getState(),  
                OrderEvent.PAY,  
                order  
        );  
  
        order.setState(newState);  
        //保存  
    }  
  
    /**  
     * 发货  
     */  
    public void shipOrder(String orderId, String shippingNo) {  
        Order order = orderRepository.findByOrderId(2L);  
  
        OrderState newState = stateMachineTemplate.fireEvent(  
                "orderStateMachine",  
                order.getState(),  
                OrderEvent.SHIP,  
                order  
        );  
  
        order.setState(newState);  
    }  
  
    /**  
     * 检查是否可以执行某个操作  
     */  
    public boolean canPerformAction(String orderId, OrderEvent event) {  
        Order order = orderRepository.findByOrderId(1L);  
  
        return stateMachineTemplate.canFireEvent(  
                "orderStateMachine", order.getState(), OrderEvent.PAY, order);  
    }  
}
```

## 事件监听
```java
@Component
public class OrderStateChangeListener {
    
    private static final Logger log = LoggerFactory.getLogger(OrderStateChangeListener.class);
    
    @EventListener
    public void handleBeforeStateTransition(StateTransitionEvent.Before<OrderState, OrderEvent> event) {
        log.info("状态转换前 - 机器: {}, 从: {}, 事件: {}, 到: {}", 
                event.getMachineId(), event.getFromState(), event.getEvent(), event.getToState());
    }
    
    @EventListener
    public void handleAfterStateTransition(StateTransitionEvent.After<OrderState, OrderEvent> event) {
        log.info("状态转换完成 - 机器: {}, 从: {}, 事件: {}, 到: {}", 
                event.getMachineId(), event.getFromState(), event.getEvent(), event.getToState());
    }
}
```

