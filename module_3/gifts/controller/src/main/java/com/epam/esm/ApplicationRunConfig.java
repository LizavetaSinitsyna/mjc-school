package com.epam.esm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.epam.esm.service.util.DataGenerator;

@Component
public class ApplicationRunConfig implements ApplicationRunner {
	private DataGenerator dataGenerator;

	@Autowired
	public ApplicationRunConfig(DataGenerator dataGenerator) {
		this.dataGenerator = dataGenerator;
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		dataGenerator.generateData();
	}
}
