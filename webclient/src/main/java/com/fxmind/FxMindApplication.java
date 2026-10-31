package com.fxmind;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * FxHubJava entry point. Lives in the root package so component, entity and repository scanning cover
 * both the core module and the web module.
 */
@SpringBootApplication
@ConfigurationPropertiesScan("com.fxmind.web")
public class FxMindApplication {

    public static void main(String[] args) {
        SpringApplication.run(FxMindApplication.class, args);
    }
}
