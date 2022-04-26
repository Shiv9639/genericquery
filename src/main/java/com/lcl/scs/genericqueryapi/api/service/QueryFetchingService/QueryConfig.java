package com.lcl.scs.genericqueryapi.api.service.QueryFetchingService;

import com.lcl.scs.genericqueryapi.api.util.logging.LoggingUtilities;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "com.lcl.scs.genericqueryapi.api.service.QueryFetchingService", mongoTemplateRef = "QueryTemplate")
public class QueryConfig {
	
	@Value("${spring.data.mongodb.uri}")
	private String uri;

	@Value("${spring.data.mongodb.database}")
	private String db;
	
	@Bean(name="QueryTemplate")
    public MongoTemplate QueryTemplate() throws Exception {
        final ConnectionString connectionString = new ConnectionString(uri);
		final MongoClientSettings mongoClientSettings = MongoClientSettings.builder().applyConnectionString(connectionString).build();
		final MongoClient mongo =  MongoClients.create(mongoClientSettings);

		final MongoTemplate template = new MongoTemplate(mongo, db);
		return template;
    }
}