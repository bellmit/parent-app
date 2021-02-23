package com.yesido.mongodb.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import com.mongodb.MongoClient;

/**
 * ignoreResourceNotFound：指定的配置文件不存在是否报错，默认是false，当设置为 true 时，若该文件不存在，程序不会报错
 * 
 * @author yesido
 * @date 2019年7月26日 上午10:24:49
 */
@Configuration
@PropertySource(value = {"classpath:mongodb-${spring.profiles.active}.properties"},
        ignoreResourceNotFound = false)
public class MongodbConfig {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${mongodb.host}")
    private String host;

    @Value("${mongodb.port}")
    private int port;

    @Value("${mongodb.username}")
    private String username;

    @Value("${mongodb.password}")
    private String password;

    @Value("${mongodb.dbname}")
    private String dbname;

    @Bean
    public MongoDbFactory mongoDbFactory() {
        MongoDbFactory mongoDbFactory = new SimpleMongoDbFactory(mongoClient(), dbname);
        return mongoDbFactory;
    }

    @Bean
    public MongoClient mongoClient() {
        logger.info("初始化mongoClient，host：{}，port：{}", host, port);
        MongoClient mongo = new MongoClient(host, port);
        return mongo;
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        DbRefResolver dbRefResolver = new DefaultDbRefResolver(mongoDbFactory());
        MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver, new MongoMappingContext());
        converter.setTypeMapper(new DefaultMongoTypeMapper(null)); // 去掉_class字段
        MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory(), converter);

        return mongoTemplate;
    }

}
