package com.bytetune;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * ================================
 * 🚀 Spring Boot 启动类
 * ================================
 * <p>
 * 核心功能注解说明：
 * <p>
 * 1️⃣ @SpringBootApplication
 * - 标识主启动类
 * - 包含：
 *
 * @Configuration → 当前类是配置类
 * @EnableAutoConfiguration → 开启自动装配
 * @ComponentScan → 扫描当前包及子包组件
 * <p>
 * --------------------------------------------------
 * <p>
 * 2️⃣ @EnableScheduling
 * - 开启定时任务支持
 * - 允许使用 @Scheduled
 * <p>
 * --------------------------------------------------
 * <p>
 * 3️⃣ @EnableAsync
 * - 开启异步任务支持
 * - 配合 @Async 使用
 * <p>
 * --------------------------------------------------
 * <p>
 * 4️⃣ @MapperScan("com.xxx.mapper")
 * - 扫描 MyBatis Mapper 接口
 * - 自动生成代理实现类
 * <p>
 * --------------------------------------------------
 * <p>
 * 5️⃣ @EnableTransactionManagement
 * - 开启声明式事务
 * - 允许使用 @Transactional
 * <p>
 * --------------------------------------------------
 * <p>
 * 6️⃣ @EnableCaching
 * - 开启缓存支持
 * - 允许使用：
 * @Cacheable
 * @CachePut
 * @CacheEvict --------------------------------------------------
 * <p>
 * 7️⃣ @EnableConfigurationProperties
 * - 启用 @ConfigurationProperties 绑定
 * <p>
 * --------------------------------------------------
 * <p>
 * 8️⃣ @Import
 * - 手动导入配置类
 * <p>
 * --------------------------------------------------
 * <p>
 * 9️⃣ @EnableAspectJAutoProxy
 * - 开启 AOP 支持
 * - 允许使用 @Aspect
 * <p>
 * --------------------------------------------------
 * <p>
 * 🔟 @ServletComponentScan
 * - 扫描 @WebFilter / @WebServlet / @WebListener
 * <p>
 * --------------------------------------------------
 * <p>
 * ⚠ 启动类必须放在项目根包，否则组件扫描会失效
 */
@SpringBootApplication
@EnableTransactionManagement
@EnableScheduling                 // 定时任务
// @EnableAsync                      // 异步任务
// @EnableTransactionManagement      // 事务
// @EnableCaching                    // 缓存
// @EnableAspectJAutoProxy           // AOP
@MapperScan("com.bytetune.mapper")// MyBatis
public class ByteTuneApplication {

    public static void main(String[] args) {
        SpringApplication.run(ByteTuneApplication.class, args);
    }

}