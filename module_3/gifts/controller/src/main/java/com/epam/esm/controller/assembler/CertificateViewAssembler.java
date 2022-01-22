package com.epam.esm.controller.assembler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import com.epam.esm.controller.CertificateController;
import com.epam.esm.controller.HateoasUtil;
import com.epam.esm.controller.converter.CertificateViewConverter;
import com.epam.esm.controller.view.CertificateView;
import com.epam.esm.dto.CertificateDto;

@Component
public class CertificateViewAssembler extends RepresentationModelAssemblerSupport<CertificateDto, CertificateView> {

	private final CertificateViewConverter certificateConverter;

	@Autowired
	public CertificateViewAssembler(CertificateViewConverter certificateConverter) {
		super(CertificateController.class, CertificateView.class);
		this.certificateConverter = certificateConverter;

	}

	@Override
	public CertificateView toModel(CertificateDto entity) {
		CertificateView certificateView = certificateConverter.convertToView(entity);
		HateoasUtil.addLinksToCertificate(certificateView);
		return certificateView;
	}
}
