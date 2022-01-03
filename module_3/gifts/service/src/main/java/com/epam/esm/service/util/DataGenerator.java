package com.epam.esm.service.util;

import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.epam.esm.dto.CertificateDto;
import com.epam.esm.dto.TagDto;
import com.epam.esm.dto.UserDto;

@Service
@PropertySource("classpath:generator.properties")
public class DataGenerator {
	private UserGenerator userGenerator;
	private Environment environment;
	private TagGenerator tagGenerator;
	private CertificateGenerator certificateGenerator;
	private OrderGenerator orderGenerator;

	@Autowired
	public DataGenerator(UserGenerator userGenerator, Environment environment, TagGenerator tagGenerator,
			CertificateGenerator certificateGenerator, OrderGenerator orderGenerator) {
		super();
		this.userGenerator = userGenerator;
		this.environment = environment;
		this.tagGenerator = tagGenerator;
		this.certificateGenerator = certificateGenerator;
		this.orderGenerator = orderGenerator;
	}

	@Transactional
	public void generateData() {
		String generate = environment.getProperty(GeneratorConstant.GENERATE);
		if (generate != null && Boolean.parseBoolean(generate)) {
			int usersAmount = Integer.parseInt(environment.getProperty(GeneratorConstant.USERS_AMOUNT));
			int tagsAmount = Integer.parseInt(environment.getProperty(GeneratorConstant.TAGS_AMOUNT));
			int certificatesAmount = Integer.parseInt(environment.getProperty(GeneratorConstant.CERTIFICATES_AMOUNT));
			int ordersAmount = Integer.parseInt(environment.getProperty(GeneratorConstant.ORDERS_AMOUNT));
			int certificateTagsMinAmount = Integer
					.parseInt(environment.getProperty(GeneratorConstant.CERTIFICATE_TAGS_MIN_AMOUNT));
			int certificateTagsMaxAmount = Integer
					.parseInt(environment.getProperty(GeneratorConstant.CERTIFICATE_TAGS_MAX_AMOUNT));
			int orderCertificatesMinAmount = Integer
					.parseInt(environment.getProperty(GeneratorConstant.ORDER_CERTIFICATES_MIN_AMOUNT));
			int orderCertificatesMaxAmount = Integer
					.parseInt(environment.getProperty(GeneratorConstant.ORDER_CERTIFICATES_MAX_AMOUNT));
			int orderUniqueCertificatesMinAmount = Integer
					.parseInt(environment.getProperty(GeneratorConstant.ORDER_UNIQUE_CERTIFICATES_MIN_AMOUNT));
			int orderUniqueCertificatesMaxAmount = Integer
					.parseInt(environment.getProperty(GeneratorConstant.ORDER_UNIQUE_CERTIFICATES_MAX_AMOUNT));

			List<UserDto> users = userGenerator.generateUsers(usersAmount);
			List<TagDto> tags = tagGenerator.generateTags(tagsAmount);
			List<CertificateDto> certificates = certificateGenerator.generateCertificates(certificatesAmount, tags,
					certificateTagsMinAmount, certificateTagsMaxAmount);
			orderGenerator.generateOrders(ordersAmount, certificates, users, orderCertificatesMinAmount,
					orderCertificatesMaxAmount, orderUniqueCertificatesMinAmount, orderUniqueCertificatesMaxAmount);
		}
	}

	public static int getRandomNumber(int min, int max) {
		Random random = new Random();
		return random.nextInt(max - min) + min;
	}
}
