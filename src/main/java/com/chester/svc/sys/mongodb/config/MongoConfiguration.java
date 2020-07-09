package com.chester.svc.sys.mongodb.config;

import com.chester.data.mongo.MongoInt64IdGenerator;
import com.mongodb.client.MongoDatabase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoConfiguration {

    @Bean
    public MongoInt64IdGenerator userGenerator(MongoDatabase db) {
        return new MongoInt64IdGenerator(db, MongoCollections.user);
    }

    @Bean
    public MongoInt64IdGenerator menuGenerator(MongoDatabase db) {
        return new MongoInt64IdGenerator(db, MongoCollections.menu);
    }

    @Bean
    public MongoInt64IdGenerator roleGenerator(MongoDatabase db) {
        return new MongoInt64IdGenerator(db, MongoCollections.role);
    }

}
