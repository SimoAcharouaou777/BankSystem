package com.youcode.bankify.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.youcode.bankify.repository.jpa")
@EnableElasticsearchRepositories(basePackages = "com.youcode.bankify.repository.elasticsearch")
public class JpaConfig {
}
