/**
 * 明白了，你问的是 entity 包的通用职责和能放哪些类、关系类型，不局限某个项目。⚡
 * <p>
 * ⸻
 * <p>
 * 1️⃣ entity 包的核心定位
 * •	作用：存放 与数据库表或集合一一对应的实体类，即 Domain Model / Persistent Entity
 * •	特点：
 * •	包含数据字段 + 数据库映射注解（如 @Entity / @Table / @Document / @Id / @Indexed）
 * •	业务逻辑极少或只涉及简单状态更新
 * •	可以有嵌套对象（如 MongoDB 的子文档）
 * <p>
 * 总原则：能直接映射数据库表/集合的类都能放这里
 * <p>
 * ⸻
 * <p>
 * 2️⃣ 可以放的类类型
 * <p>
 * 2.1 基础实体
 * •	对应数据库表/集合的主类
 * •	例子：
 * •	User → 用户表
 * •	Product → 商品表
 * •	Order → 订单表
 * <p>
 * ⸻
 * <p>
 * 2.2 子实体 / 嵌套类
 * •	数据库中作为 嵌套对象或子文档
 * •	例子：
 * •	OrderItem → 订单条目
 * •	Address → 用户地址
 * •	LyricLine → 歌词行
 * <p>
 * MongoDB 特别适合放嵌套文档，关系型数据库可以通过 @Embeddable / @Embedded 实现。
 * <p>
 * ⸻
 * <p>
 * 2.3 枚举或状态类（与实体紧密相关）
 * •	放在实体包里没问题，如果它仅用于实体字段
 * •	例子：
 * •	OrderStatus → 订单状态枚举
 * •	UserRole → 用户角色枚举
 * <p>
 * ⸻
 * <p>
 * 2.4 实体关系类（多对多 / 关联表）
 * •	对应关联表或关系表的实体
 * •	例子：
 * •	UserRoleMapping → 用户-角色映射表
 * •	ProductCategory → 商品-分类多对多表
 * <p>
 * 也可以根据项目复杂度放在单独的 relation 子包，但仍属于实体范畴。
 * <p>
 * ⸻
 * <p>
 * 2.5 日志 / 审计实体
 * •	只要是持久化的、与数据库表对应，也可放
 * •	例子：
 * •	LoginLog → 登录日志表
 * •	ChangeHistory → 数据变更历史
 * <p>
 * ⸻
 * <p>
 * 3️⃣ 不应该放在 entity 包的类
 * •	Service / Manager / Business Logic
 * •	Repository / DAO
 * •	Controller / API
 * •	DTO / VO / Request / Response
 * •	工具类、枚举（如果非实体字段相关）
 * <p>
 * ⸻
 * <p>
 * 4️⃣ 总结
 * <p>
 * 类类型	放 entity 包	说明
 * 主实体	✅	映射数据库表/集合
 * 子文档 / 嵌套实体	✅	Mongo 嵌套或 JPA Embeddable
 * 枚举状态	✅	仅服务于实体字段
 * 关联表实体	✅	多对多映射表
 * 审计 / 日志表	✅	数据持久化相关
 * DTO / VO	❌	用于接口层
 * Service / Controller	❌	业务逻辑/接口层
 * 工具类	❌	与数据库无关
 * <p>
 * <p>
 * ⸻
 * <p>
 * 💡 原则：凡是直接映射到数据库的类都可以放 entity 包，其他逻辑类尽量别放，保证包干净、职责单一。
 * <p>
 * ⸻
 * <p>
 * 如果你需要，我可以帮你画一张通用的 entity 包内结构示意图，展示主实体、子实体、关系表、枚举、日志的摆放关系，一目了然。
 * <p>
 * 你要我画吗？
 */
package com.maizi.bytetune.common.entity;