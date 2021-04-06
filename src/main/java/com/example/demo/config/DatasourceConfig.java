package com.example.demo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.druid.pool.DruidDataSource;

@Configuration
public class DatasourceConfig {


    @Bean(initMethod = "init", destroyMethod = "close")
    @ConfigurationProperties(prefix = "spring.clickhource", ignoreUnknownFields = true)
    public DruidDataSource clickHourceDataSource() {
        return new DruidDataSource();
    }
}
