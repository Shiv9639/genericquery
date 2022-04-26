package com.lcl.scs.genericqueryapi.api.service.ods;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class OdsConfig {
	
	@Value("${spring.ods.datasource.url}")
	private String dbUrl;
	
	@Value("${spring.ods.datasource.username}")
	private String userName;
	
	@Value("${spring.ods.datasource.password}")
	private String password;
	
	@Primary
	@Bean(name = "odsDataSource")
	@ConfigurationProperties(prefix = "spring.ods.datasource")
	public DataSource odsDataSource() {

		DataSourceBuilder dataSource = DataSourceBuilder.create();

		dataSource.url(dbUrl);
		dataSource.username(userName);
		dataSource.password(password);

		DataSource odsDataSource = dataSource.build();

		return odsDataSource;
	}

}