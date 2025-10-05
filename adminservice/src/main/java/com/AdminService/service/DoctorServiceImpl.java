package com.AdminService.service;

import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.AdminService.dto.DoctorsDTO;
import com.AdminService.feign.ClinicAdminFeign;
import com.AdminService.util.AutoCheckJwtToken;
import com.AdminService.util.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

	@Autowired
	private AutoCheckJwtToken token;
	
    private final ClinicAdminFeign clinicAdminFeign;
    private final ObjectMapper objectMapper = new ObjectMapper(); 

    //--------------------------------- Add Doctor --------------------------------------------
    @Override
    public ResponseEntity<Response> addDoctor(DoctorsDTO dto) {
        try {
            return clinicAdminFeign.addDoctor(token.access_token,dto);
        } catch (FeignException ex) {
            return handleFeignException(ex);
        }
    }

    // ---------------- Get All Doctors ----------------
    @Override
    public ResponseEntity<Response> getAllDoctors() {
        try {
            return clinicAdminFeign.getAllDoctors(token.access_token);
        } catch (FeignException ex) {
            return handleFeignException(ex);
        }
    }

    // ---------------- Get Doctor By Id ----------------
    @Override
    public ResponseEntity<Response> getDoctorById(String id) {
        try {
            return clinicAdminFeign.getDoctorById(token.access_token,id);
        } catch (FeignException ex) {
            return handleFeignException(ex);
        }
    }

    // ---------------- Update Doctor By Id ----------------
    @Override
    public ResponseEntity<Response> updateDoctorById(String doctorId, DoctorsDTO dto) {
        try {
            return clinicAdminFeign.updateDoctorById(token.access_token,doctorId, dto);
        } catch (FeignException ex) {
            return handleFeignException(ex);
        }
    }

    // ---------------- Delete Doctor By Id ----------------
    @Override
    public ResponseEntity<Response> deleteDoctorById(String doctorId) {
        try {
            return clinicAdminFeign.deleteDoctorById(token.access_token,doctorId);
        } catch (FeignException ex) {
            return handleFeignException(ex);
        }
    }

    // ---------------- Delete Doctors By Clinic ----------------
    @Override
    public ResponseEntity<Response> deleteDoctorsByClinic(String clinicId) {
        try {
            return clinicAdminFeign.deleteDoctorsByClinic(token.access_token,clinicId);
        } catch (FeignException ex) {
            return handleFeignException(ex);
        }
    }

    // ---------------- Common Feign Exception Handler ----------------
    private ResponseEntity<Response> handleFeignException(FeignException ex) {
        try {
            if (ex.responseBody().isPresent()) {
                byte[] bodyBytes = ex.responseBody().get().array();
                String body = new String(bodyBytes, StandardCharsets.UTF_8);

                Response response = objectMapper.readValue(body, Response.class);
                return ResponseEntity.status(ex.status()).body(response);
            }
        } catch (Exception innerEx) {
            innerEx.printStackTrace();
        }

        
        Response fallback = new Response(false, null, ex.getMessage(), ex.status(), null, null);
        return ResponseEntity.status(ex.status()).body(fallback);
    }
}