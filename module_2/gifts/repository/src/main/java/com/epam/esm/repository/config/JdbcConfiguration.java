package com.epam.esm.repository.config;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableJdbcRepositories("com.epam.esm")
public class JdbcConfiguration extends AbstractJdbcConfiguration {
	@Autowired
	private ResourceManager resourceManager;

	@Bean
	public NamedParameterJdbcOperations operations() {
		return new NamedParameterJdbcTemplate(dataSource());
	}

	@Bean
	public PlatformTransactionManager transactionManager() {
		return new DataSourceTransactionManager(dataSource());
	}

	@Bean
	public DataSource dataSource() {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName(resourceManager.getParam(DatabaseParameter.DB_DRIVER));
		dataSource.setUrl(resourceManager.getParam(DatabaseParameter.DB_URL));
		dataSource.setUsername(resourceManager.getParam(DatabaseParameter.DB_USERNAME));
		dataSource.setPassword(resourceManager.getParam(DatabaseParameter.DB_PASSWORD));
		dataSource.setInitialSize(Integer.parseInt(resourceManager.getParam(DatabaseParameter.DB_INIT_POOL_SIZE)));
		dataSource.setMaxIdle(Integer.parseInt(resourceManager.getParam(DatabaseParameter.DB_MAX_IDLE)));
		dataSource.setMaxWaitMillis(Long.parseLong(resourceManager.getParam(DatabaseParameter.DB_MAX_WAIT)));
		dataSource.setMaxTotal(Integer.parseInt(resourceManager.getParam(DatabaseParameter.DB_MAX_POOL_SIZE)));
		return dataSource;
	}
	
	@Bean
	public JdbcTemplate jdbcTemplate() {
		return new JdbcTemplate(dataSource());
	}
}
