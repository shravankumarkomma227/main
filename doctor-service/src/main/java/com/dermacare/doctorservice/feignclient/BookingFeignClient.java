package com.dermacare.doctorservice.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.dermacare.doctorservice.dto.BookingResponse;
import com.dermacare.doctorservice.dto.ResponseStructure;


@FeignClient(name = "bookingservice")
public interface  BookingFeignClient {
	
	@GetMapping("/api/v1/getBookedServiceById/{id}")
	public ResponseEntity<ResponseStructure<BookingResponse>> getBookedService(@RequestHeader("Authorization") String token,@PathVariable String id);
	
	
	@PutMapping("/api/v1/updateAppointment")
	public ResponseEntity<?> updateAppointment(@RequestHeader("Authorization") String token,@RequestBody BookingResponse bookingResponse );
	
	@GetMapping("/api/v1/getAppointmentByPatientId/{patientId}")
	public ResponseEntity<?> getAppointmentByPatientId(@RequestHeader("Authorization") String token,@PathVariable String patientId);
	
	@GetMapping("/api/v1/getAppointsByInput/{input}")
	public ResponseEntity<?> getAppointsByInput(@RequestHeader("Authorization") String token,@PathVariable String input);
	
	@GetMapping("/api/v1/getTodayDoctorAppointmentsByDoctorId/{clinicId}/{doctorId}")
	public ResponseEntity<?> getTodayDoctorAppointmentsByDoctorId(@RequestHeader("Authorization") String token,@PathVariable String clinicId,@PathVariable String doctorId);
	
	@GetMapping("/api/v1/filterDoctorAppointmentsByDoctorId/{clinicId}/{doctorId}/{number}")
	public ResponseEntity<?> filterDoctorAppointmentsByDoctorId(@RequestHeader("Authorization") String token,@PathVariable String clinicId,@PathVariable String doctorId,@PathVariable String number);
	
	@GetMapping("/api/v1/getCompletedApntsByDoctorId/{clinicId}/{doctorId}")
	public ResponseEntity<?> filterDoctorAppointmentsByDoctorId(@RequestHeader("Authorization") String token,@PathVariable String clinicId,@PathVariable String doctorId);
	
	@GetMapping("/api/v1/getSizeOfConsultationTypesByDoctorId/{clinicId}/{doctorId}")
	public ResponseEntity<?> getSizeOfConsultationTypesByDoctorId(@RequestHeader("Authorization") String token,@PathVariable String clinicId,@PathVariable String doctorId);
	


}
