package com.maizi.bytetune;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication(scanBasePackages = "com.maizi.bytetune")
@ConfigurationPropertiesScan("com.maizi.bytetune")
public class BytetuneStarterApplication {

    public static void main(String[] args) {
        SpringApplication.run(
                BytetuneStarterApplication.class, args);
    }

}
