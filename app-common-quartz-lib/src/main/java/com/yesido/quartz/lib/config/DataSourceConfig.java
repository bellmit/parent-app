package com.yesido.quartz.lib.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@PropertySource("quartz.properties")
public class DataSourceConfig {

    @Value("${org.quartz.dataSource.qzDS.URL}")
    private String url;

    @Value("${org.quartz.dataSource.qzDS.user}")
    private String userName;

    @Value("${org.quartz.dataSource.qzDS.password}")
    private String password;

    @Bean(name = "quartzDataSource")
    @Qualifier("quartzDataSource")
    @ConfigurationProperties(prefix = "org.quartz.dataSource.qzDS")
    public DataSource newDataSource() {
        return DataSourceBuilder.create().url(url).username(userName).password(password).build();
    }

    @Bean(name = "quartzJdbcTemplate")
    public JdbcTemplate quartzJdbcTemplate(@Qualifier("quartzDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
