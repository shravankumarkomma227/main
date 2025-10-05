package com.dermacare.doctorservice.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.dermacare.doctorservice.dermacaredoctorutils.AutoCheckJwtToken;
import com.dermacare.doctorservice.dto.ChangeDoctorPasswordDTO;
import com.dermacare.doctorservice.dto.DoctorAvailabilityStatusDTO;
import com.dermacare.doctorservice.dto.DoctorDeviceIdDto;
import com.dermacare.doctorservice.dto.Response;
import com.dermacare.doctorservice.feignclient.ClinicAdminServiceClient;
import com.dermacare.doctorservice.model.DoctorDeviceIdsEntity;
import com.dermacare.doctorservice.repository.DoctorDeviceIdsRepository;
import com.dermacare.doctorservice.service.DoctorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    @Autowired
    private final ClinicAdminServiceClient clinicAdminServiceClient;
    
    @Autowired
   	private AutoCheckJwtToken token;
    
    @Autowired
    private DoctorDeviceIdsRepository doctorDeviceIdsRepository;
   	

    private Response validateChangePasswordRequest(String username, ChangeDoctorPasswordDTO updateDTO) {
        if (username == null || username.isBlank()) {
       return Response.builder().success(false).status(400).message("Username must not be empty").build();
        }

        if (updateDTO == null) {
        return Response.builder().success(false).status(400) .message("Request body is missing").build();
        }

        if (updateDTO.getCurrentPassword() == null || updateDTO.getCurrentPassword().isBlank()) {
            return Response.builder().success(false).status(400).message("Current password must not be empty").build();
        }

        if (updateDTO.getNewPassword() == null || updateDTO.getNewPassword().isBlank()) {
          return Response.builder().success(false).status(400).message("New password must not be empty").build();
        }

        if (updateDTO.getConfirmPassword() == null || updateDTO.getConfirmPassword().isBlank()) {
         return Response.builder().success(false).status(400).message("Confirm password must not be empty").build();
        }

        if (!updateDTO.getNewPassword().equals(updateDTO.getConfirmPassword())) {
         return Response.builder().success(false).status(400).message("New password and confirm password do not match").build();
        }

        if (updateDTO.getNewPassword().length() < 6) {
         return Response.builder().success(false).status(400).message("Password must be at least 6 characters").build();
        }

        return null; 
    }

    @Override
    public Response changePassword(String username, ChangeDoctorPasswordDTO updateDTO) {
        Response validationResponse = validateChangePasswordRequest(username, updateDTO);
        if (validationResponse != null) {
            return validationResponse;
        }

        try {
            
            return clinicAdminServiceClient.changePassword(token.access_token,  username, updateDTO);

        } catch (Exception ex) {
   
            return Response.builder().success(false).status(500).message("Failed to change password " ).build();
        }
    }


	@Override
	public Response updateDoctorAvailability(String doctorId, DoctorAvailabilityStatusDTO availabilityDTO) {
	    if(doctorId==null || doctorId.isBlank()) {
	    	return Response.builder().success(false).status(400) .message("Doctor ID must not be empty").build();
	    }

    	if(availabilityDTO == null) {
    		return Response.builder().success(false).status(400).message("Availability status is missing").build();
    	}
    	try {
    		return clinicAdminServiceClient.updateDoctorAvailability(token.access_token,  doctorId, availabilityDTO);
    	}
    	catch (Exception ex) {
			return Response.builder().success(false).status(500).message("Failed to update doctor availability status").build();
					
		}
    }
	
	
	///NEW DOCTOR APIS
	
			public ResponseEntity<?> getAllDoctors(){
				try {
				return clinicAdminServiceClient.getAllDoctors(token.access_token);
				}catch(Exception e) {
					return ResponseEntity.status(500).body(e.getMessage());
				}
			}
			
			
			public ResponseEntity<?> getDoctorById(String id){
				try {
				return clinicAdminServiceClient.getDoctorById(token.access_token, id);
				}catch(Exception e) {
					return ResponseEntity.status(500).body(e.getMessage());
				}
			}
			
			
			public ResponseEntity<?> getDoctorByClinicAndDoctorId(String clinicId,
					String doctorId){
				try {
				return clinicAdminServiceClient.getDoctorByClinicAndDoctorId(token.access_token, clinicId, doctorId);
				}catch(Exception e) {
					return ResponseEntity.status(500).body(e.getMessage());
				}
			}
			
			public ResponseEntity<?> getDoctorsByHospitalById(String clinicId){
				try {
				return clinicAdminServiceClient.getDoctorsByHospitalById(token.access_token, clinicId);
				}catch(Exception e) {
					return ResponseEntity.status(500).body(e.getMessage());
				}
			}
			
			
			public ResponseEntity<?> getDoctorsBySubServiceId(String hsptlId,String subServiceId){
				try {
				return clinicAdminServiceClient.getDoctorsBySubServiceId(token.access_token,hsptlId, subServiceId);
				}catch(Exception e) {
					return ResponseEntity.status(500).body(e.getMessage());
				}
			}
			
			
			public ResponseEntity<?> getAllDoctorsBySubServiceId(String subServiceId){
				try {
				return clinicAdminServiceClient.getAllDoctorsBySubServiceId(token.access_token, subServiceId);
				}catch(Exception e) {
					return ResponseEntity.status(500).body(e.getMessage());
				}}
				
				
	public ResponseEntity<?> getDoctorDeviceId(String doctorId){
		Response res = new Response();
    	try {
    		DoctorDeviceIdsEntity obj = doctorDeviceIdsRepository.findByDoctorId(doctorId);
    		DoctorDeviceIdDto dto = new ObjectMapper().convertValue(obj,DoctorDeviceIdDto.class );
    		res.setData(dto);
    		res.setStatus(200);
    		res.setSuccess(true);
    	}catch(Exception e) {
    		res.setData(null);
    		res.setStatus(500);
    		res.setSuccess(false);
    	}
    	return ResponseEntity.status(res.getStatus()).body(res);
	}
				
}

