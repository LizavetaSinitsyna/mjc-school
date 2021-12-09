package com.epam.esm.service.converter;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.epam.esm.dto.CertificateDto;
import com.epam.esm.repository.model.CertificateModel;

@Component
public class CertificateConverter {
	private ModelMapper modelMapper;
	private Converter<String, String> spaceRemover = (src) -> src.getSource().trim().replaceAll("\\s+", " ").trim();

	public CertificateConverter() {
		this.modelMapper = new ModelMapper();
		modelMapper.addConverter(spaceRemover);
	}

	public CertificateDto convertToDto(CertificateModel certificateModel) {
		return modelMapper.map(certificateModel, CertificateDto.class);
	}

	public CertificateModel convertToModel(CertificateDto certificateDto) {
		return modelMapper.map(certificateDto, CertificateModel.class);
	}
}
