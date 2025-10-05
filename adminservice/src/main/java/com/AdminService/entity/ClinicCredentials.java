package com.AdminService.entity;

import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "clinic_credentials")
public class ClinicCredentials {

    @Id
    private String id;  
    private String userName;	
    private String password;	
	private String role;
	private List<String>permissions;
	private List<String> roles;
    private String hospitalId;
    private String hospitalName;
    private String branchName;
    private String branchId;
 
	}
    
