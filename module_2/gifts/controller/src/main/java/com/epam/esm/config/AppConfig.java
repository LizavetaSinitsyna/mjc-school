package com.epam.esm.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@Configuration
public class AppConfig {
	private static final String MESSAGE_SOURCE_BASENAME = "classpath:messages";
	private static final String DEFAULT_ENCODING = "UTF-8";

	@Bean
	public MessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasename(MESSAGE_SOURCE_BASENAME);
		messageSource.setDefaultEncoding(DEFAULT_ENCODING);
		return messageSource;
	}

}
