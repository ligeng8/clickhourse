package com.example.demo.config;

import com.github.housepower.jdbc.BalancedClickhouseDataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.druid.pool.DruidDataSource;

import javax.sql.DataSource;
@ConditionalOnProperty(prefix = "spring.clickhource",name = "url",matchIfMissing = false)
@Configuration
public class DatasourceConfig {


    @Value("${spring.clickhource.url}")
    private String url;

    @Bean(initMethod = "init", destroyMethod = "close",name = "clickHourceDataSource")
    @ConditionalOnClass(DruidDataSource.class)
    @ConditionalOnProperty(prefix = "spring.clickhource.druid",name = "enable",havingValue = "true")
    @ConfigurationProperties(prefix = "spring.clickhource", ignoreUnknownFields = true)
    public DruidDataSource clickHourceDruidDataSource() {
        return new DruidDataSource();
    }
    
    @Bean(name = "clickHourceDataSource")
    @ConditionalOnClass(HikariDataSource.class)
    @ConditionalOnProperty(prefix = "spring.clickhource.hikari",name = "enable",havingValue = "true")
    public  DataSource  clickHourceDataSource(){
        DataSource balancedCkDs = new BalancedClickhouseDataSource(url);
        HikariConfig conf = new HikariConfig();
        conf.setDataSource(balancedCkDs);
        return new HikariDataSource(conf);
    }


}
