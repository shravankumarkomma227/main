package com.clinicadmin.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClinicAdminDeviceIdDto {

	private String clinicId;
	private String branchId;
	private String clinicAdminWebFcmToken;
}
