package com.epam.esm.service.converter;

import com.epam.esm.dto.CertificateDto;
import com.epam.esm.repository.model.CertificateModel;

public class CertificateConverter {
	public static CertificateDto convertModelToDTO(CertificateModel certificateModel) {
		if (certificateModel == null) {
			return null;
		}

		CertificateDto certificateDTO = new CertificateDto();
		certificateDTO.setId(certificateModel.getId());
		certificateDTO.setName(certificateModel.getName());
		certificateDTO.setDescription(certificateModel.getDescription());
		certificateDTO.setDuration(certificateModel.getDuration());
		certificateDTO.setCreateDate(certificateModel.getCreateDate());
		certificateDTO.setLastUpdateDate(certificateModel.getLastUpdateDate());
		certificateDTO.setPrice(certificateModel.getPrice());

		return certificateDTO;
	}

	public static CertificateModel convertDtoToModel(CertificateDto certificateDto) {
		if (certificateDto == null) {
			return null;
		}

		CertificateModel certificateModel = new CertificateModel();
		certificateModel.setId(certificateDto.getId());
		certificateModel.setName(certificateDto.getName());
		certificateModel.setDescription(certificateDto.getDescription());
		certificateModel.setDuration(certificateDto.getDuration());
		certificateModel.setCreateDate(certificateDto.getCreateDate());
		certificateModel.setLastUpdateDate(certificateDto.getLastUpdateDate());
		certificateModel.setPrice(certificateDto.getPrice());

		return certificateModel;
	}
}
