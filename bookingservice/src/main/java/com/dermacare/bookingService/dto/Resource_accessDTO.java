package com.dermacare.bookingService.dto;

import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Resource_accessDTO {
	
	private List<Map<String,List<String>>> roles;

}
