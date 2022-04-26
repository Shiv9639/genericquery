package com.lcl.scs.genericqueryapi.api.service.mongo;

import javax.sql.DataSource;
import javax.swing.text.Document;

import com.lcl.scs.genericqueryapi.api.util.logging.LoggingUtilities;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "com.lcl.scs.genericqueryapi.api.service.mongo")
public class MongoConfig {
	
	@Value("${spring.mongo.uri}")
	private String uri;

	@Value("${spring.mongo.database}")
	private String database;
	
	@Bean
    public MongoTemplate mongoTemplate() throws Exception {
        final ConnectionString connectionString = new ConnectionString(uri);
		final MongoClientSettings mongoClientSettings = MongoClientSettings.builder().applyConnectionString(connectionString).build();
		final MongoClient client =  MongoClients.create(mongoClientSettings);
		final MongoTemplate template = new MongoTemplate(client, database);
		return template;
    }
}