package com.sre.triage;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class LogAnalyserApplication extends SpringBootServletInitializer {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Log Analyser API")
                        .version("1.0")
                        .description("AI-powered log analysis service for SRE triage. Automatically classifies incidents and provides remediation suggestions."));
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(LogAnalyserApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(LogAnalyserApplication.class, args);
    }
}
