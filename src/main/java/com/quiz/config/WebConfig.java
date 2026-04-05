package com.quiz.config;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS for local dev (different ports) and production (env-driven).
 * UI served from the same host as the API does not rely on CORS.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${quiz.cors.allow-all:false}")
    private boolean corsAllowAll;

    @Value("${quiz.cors.additional-origins:}")
    private String additionalOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        List<String> patterns = new ArrayList<>();
        if (corsAllowAll) {
            patterns.add("*");
        } else {
            patterns.add("http://localhost:*");
            patterns.add("http://127.0.0.1:*");
            if (additionalOrigins != null && !additionalOrigins.isBlank()) {
                for (String part : additionalOrigins.split(",")) {
                    String p = part.trim();
                    if (!p.isEmpty()) {
                        patterns.add(p);
                    }
                }
            }
        }

        registry.addMapping("/api/**")
                .allowedOriginPatterns(patterns.toArray(String[]::new))
                .allowedMethods("GET", "POST", "OPTIONS")
                .allowedHeaders("*");
    }
}
