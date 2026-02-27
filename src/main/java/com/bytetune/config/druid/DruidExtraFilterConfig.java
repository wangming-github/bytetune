package com.bytetune.config.druid;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.pool.DruidDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Deprecated(forRemoval = true)
@Slf4j
@Configuration
public class DruidExtraFilterConfig {

    @Bean
    public Filter extraFilter() {
        return new DruidSingleLineFilter();
    }

    @Bean
    public DruidDataSource druidDataSource(DruidSingleLineFilter singleLineFilter) {
        DruidDataSource ds = new DruidDataSource();
        ds.setUrl("jdbc:mysql://localhost:3306/byte_tune?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai");
        ds.setUsername("root");
        ds.setPassword("admin123");
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");

        // 统计日志
        ds.getProxyFilters().add(singleLineFilter);
        ds.setInitialSize(5);
        ds.setMaxActive(20);
        ds.setMinIdle(5);
        ds.setMaxWait(60000);
        ds.setValidationQuery("SELECT 1");
        ds.setTestOnBorrow(true);
        ds.setTestWhileIdle(true);
        return ds;
    }
}