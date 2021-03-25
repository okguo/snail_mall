package com.okguo.snailmall.order.config;

import com.alibaba.druid.pool.DruidDataSource;
import io.seata.rm.datasource.DataSourceProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @Description:
 * @Author: Guoyongfu
 * @Date: 2021/03/25 20:24
 */
@Configuration
public class SeataDataSourceProxyConfig {

    @Autowired
    DataSourceProperties dataSourceProperties;
//TODO
    @Bean
    public DataSource dataSource(DataSourceProperties properties) {
        return new DataSourceProxy(properties.initializeDataSourceBuilder().type(DruidDataSource.class).build());
    }

}
