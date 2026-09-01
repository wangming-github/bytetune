package com.maizi.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
public class AuthStarterApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthStarterApplication.class, args);
    }

}
