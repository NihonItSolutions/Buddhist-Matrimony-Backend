package com.matrimony.backend;

import com.matrimony.backend.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
@EnableMethodSecurity
@EnableConfigurationProperties(AppProperties.class)
public class MatrimonyBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(MatrimonyBackendApplication.class, args);
    }
}
