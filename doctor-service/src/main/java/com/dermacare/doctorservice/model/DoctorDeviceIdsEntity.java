package com.dermacare.doctorservice.model;

import org.springframework.data.annotation.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorDeviceIdsEntity {
	
	@Id
	private String id;
	private String hospitalId;
	private String doctorId;
	private String doctorMobileFcmToken;
	private String doctorWebFcmToken;

}
