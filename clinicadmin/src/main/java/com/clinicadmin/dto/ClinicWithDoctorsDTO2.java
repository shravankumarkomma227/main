package com.clinicadmin.dto;

import java.util.List;

import com.clinicadmin.entity.Doctors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ClinicWithDoctorsDTO2 {

	private String hospitalId; // MongoDB uses String as the default ID type
	private String name;
	private String address;
	private String city;
	private String contactNumber;
	private double hospitalOverallRating;
	private String hospitalRegistrations;
	private String openingTime;
	private String closingTime;
	private String hospitalLogo;
	private String emailAddress;
	private String website;
	private String licenseNumber;
	private String issuingAuthority;
	private String hospitalDocuments;
	private String contractorDocuments;
	private boolean recommended;
	private DoctorsDTO doctors;
	
	public ClinicWithDoctorsDTO2(String hospitalId, String name, String address, String city,
            String contactNumber, double hospitalOverallRating,
            String openingTime, String closingTime, String hospitalLogo,
            String emailAddress, String website, String licenseNumber,
            String issuingAuthority, String hospitalDocuments,
            String contractorDocuments, boolean recommended,
            DoctorsDTO doctors) {
this.hospitalId = hospitalId;
this.name = name;
this.address = address;
this.city = city;
this.contactNumber = contactNumber;
this.hospitalOverallRating = hospitalOverallRating;
this.openingTime = openingTime;
this.closingTime = closingTime;
this.hospitalLogo = hospitalLogo;
this.emailAddress = emailAddress;
this.website = website;
this.licenseNumber = licenseNumber;
this.issuingAuthority = issuingAuthority;
this.hospitalDocuments = hospitalDocuments;
this.contractorDocuments = contractorDocuments;
this.recommended = recommended;
this.doctors = doctors;
}

}
