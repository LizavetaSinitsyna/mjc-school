package com.epam.esm.repository.config;

import java.util.ResourceBundle;

import org.springframework.stereotype.Component;

@Component
public class ResourceManager {
	private ResourceBundle bundle = ResourceBundle.getBundle(DatabaseParameter.DB_PROPERTY_FILE_NAME);

	public final String getParam(String paramName) {
		return bundle.getString(paramName);
	}

}
