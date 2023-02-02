package com.emres;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class RowMatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(RowMatchApplication.class, args);
    }
}
