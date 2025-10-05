package com.clinicadmin.entity;

import org.springframework.data.annotation.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshJwtToken {
	
	@Id
	private String id;
	private String tokenName;
	private String jwtToken;
	
}
