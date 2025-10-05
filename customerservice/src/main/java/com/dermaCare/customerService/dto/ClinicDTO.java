package com.dermaCare.customerService.dto;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClinicDTO {

	private String hospitalId;

	private String name;
	private String address;

	private String city;

	private double hospitalOverallRating;

	private String contactNumber;

	private String openingTime;

	private String closingTime;

	private String hospitalLogo;

	private String emailAddress;

	private String website;

	private boolean recommended;

	private String subscription;

	private String walkthrough;

	

}