package com.epam.esm.service.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.epam.esm.dto.CertificateDto;
import com.epam.esm.dto.TagDto;
import com.epam.esm.exception.GeneratorException;
import com.epam.esm.service.CertificateService;
import com.epam.esm.service.ServiceConstant;

@Component
public class CertificateGenerator {
	private final CertificateService certificateService;

	@Autowired
	public CertificateGenerator(CertificateService certificateService) {
		this.certificateService = certificateService;
	}

	public List<CertificateDto> generateCertificates(int certificatesAmount, List<TagDto> existedtags,
			int certificateTagsMinAmount, int certificateTagsMaxAmount) {
		List<CertificateDto> certificateDtosToCreate = new ArrayList<>(certificatesAmount);
		ClassPathResource certificatesNameResource = new ClassPathResource(
				GeneratorConstant.CERTIFICATE_NAME_FILE_PATH);
		ClassPathResource certificatesDescriptionResource = new ClassPathResource(
				GeneratorConstant.CERTIFICATE_DESCRIPTION_FILE_PATH);
		int lineCounter = 0;
		try (BufferedReader certificateNameReader = new BufferedReader(
				new InputStreamReader(certificatesNameResource.getInputStream()));
				BufferedReader certificateDescriptionReader = new BufferedReader(
						new InputStreamReader(certificatesDescriptionResource.getInputStream()))) {
			String name;
			String description;
			while ((name = certificateNameReader.readLine()) != null
					&& (description = certificateDescriptionReader.readLine()) != null
					&& lineCounter < certificatesAmount) {
				CertificateDto certificateDto = new CertificateDto();
				certificateDto.setName(name);
				certificateDto.setDescription(description);
				int duration = DataGenerator.getRandomNumber(ServiceConstant.CERTIFICATE_MIN_DURATION,
						ServiceConstant.CERTIFICATE_MAX_DURATION + 1);
				certificateDto.setDuration(duration);
				BigDecimal price = ServiceConstant.CERTIFICATE_MIN_PRICE.add(new BigDecimal(Math.random()).multiply(
						ServiceConstant.CERTIFICATE_MAX_PRICE.subtract(ServiceConstant.CERTIFICATE_MIN_PRICE)));
				certificateDto.setPrice(price.setScale(ServiceConstant.CERTIFICATE_PRICE_SCALE, RoundingMode.CEILING));
				int tagsAmount = DataGenerator.getRandomNumber(certificateTagsMinAmount, certificateTagsMaxAmount + 1);
				List<TagDto> certificateTags = new ArrayList<>(tagsAmount);
				for (int i = certificateTagsMinAmount; i <= tagsAmount; i++) {
					List<Integer> usedTagIndeces = new ArrayList<>(tagsAmount);
					int tagIndex = DataGenerator.getRandomNumber(0, existedtags.size());
					while (usedTagIndeces.contains(tagIndex)) {
						tagIndex = DataGenerator.getRandomNumber(0, existedtags.size());
					}
					certificateTags.add(existedtags.get(tagIndex));
					usedTagIndeces.add(tagIndex);
				}
				certificateDto.setTags(certificateTags);
				certificateDtosToCreate.add(certificateDto);
				++lineCounter;
			}
			if (lineCounter != certificatesAmount) {
				throw new GeneratorException(GeneratorConstant.NOT_ENOUGH_DATA_EXCEPTION);
			}
		} catch (IOException e) {
			throw new GeneratorException(e.getMessage());
		}
		return certificateService.createCertificates(certificateDtosToCreate);
	}
}
