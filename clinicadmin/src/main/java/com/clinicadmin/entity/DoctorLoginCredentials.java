package com.clinicadmin.entity;

import lombok.*;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Document(collection = "doctor_login_credentials")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class DoctorLoginCredentials {

    @Id
    private String id;
    private String doctorId;   
    private String hospitalId;
    private String username;       
    private String password;      
    private List<String> roles; 
}

