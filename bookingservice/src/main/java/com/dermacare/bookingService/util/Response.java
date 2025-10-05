package com.dermacare.bookingService.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) 
@Builder
public class Response {
	
		private boolean success;
		private Object data;
		private String message;
		private int status;
		private String hospitalName;
		private String hospitalId;
	}

