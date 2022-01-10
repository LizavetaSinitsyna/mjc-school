package com.epam.esm.controller.converter;

import org.modelmapper.Converter;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.epam.esm.controller.view.CertificateView;
import com.epam.esm.dto.CertificateDto;

@Component
public class CertificateViewConverter {
	private ModelMapper modelMapper;
	private Converter<String, String> spaceRemover = new AbstractConverter<String, String>() {
		protected String convert(String source) {
			return source == null ? null : source.trim().replaceAll("\\s+", " ");
		}
	};

	public CertificateViewConverter() {
		this.modelMapper = new ModelMapper();
		modelMapper.addConverter(spaceRemover);
		modelMapper.getConfiguration().setFieldMatchingEnabled(true);
	}

	public CertificateDto convertToDto(CertificateView certificateView) {
		return modelMapper.map(certificateView, CertificateDto.class);
	}

	public CertificateView convertToView(CertificateDto certificateDto) {
		return modelMapper.map(certificateDto, CertificateView.class);
	}
}
