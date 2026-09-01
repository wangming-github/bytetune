package com.maizi.auth.starter.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy //  一般 Spring Boot 自动开了，但你最好显式写一下
public class AopConfig {
}