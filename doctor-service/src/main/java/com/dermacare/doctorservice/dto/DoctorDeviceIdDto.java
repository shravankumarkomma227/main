package com.dermacare.doctorservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DoctorDeviceIdDto {
	
	private String hospitalId;
	private String doctorId;
	private String doctorMobileFcmToken;
	private String doctorWebFcmToken;

}
