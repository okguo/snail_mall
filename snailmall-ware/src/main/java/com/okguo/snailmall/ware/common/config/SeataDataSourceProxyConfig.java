package com.okguo.snailmall.ware.common.config;

import com.alibaba.druid.pool.DruidDataSource;
import io.seata.rm.datasource.DataSourceProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class SeataDataSourceProxyConfig {

    @Autowired
    DataSourceProperties dataSourceProperties;

    @Bean
    public DataSource dataSource(DataSourceProperties properties) {
        return new DataSourceProxy(properties.initializeDataSourceBuilder().type(DruidDataSource.class).build());
    }

}