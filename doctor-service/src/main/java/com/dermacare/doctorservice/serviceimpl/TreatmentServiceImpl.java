package com.dermacare.doctorservice.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.dermacare.doctorservice.dermacaredoctorutils.AutoCheckJwtToken;
import com.dermacare.doctorservice.dto.Response;
import com.dermacare.doctorservice.dto.TreatmentDTO;
import com.dermacare.doctorservice.feignclient.ClinicAdminServiceClient;
import com.dermacare.doctorservice.service.TreatmentService;

import feign.FeignException;

@Service
public class TreatmentServiceImpl implements TreatmentService {

    @Autowired
    private ClinicAdminServiceClient clinicAdminServiceClient;
    
    @Autowired
	  private AutoCheckJwtToken token;
	   	
    @Override
    public ResponseEntity<Response> addTreatment(TreatmentDTO dto) {
        try {
            return clinicAdminServiceClient.addTreatment(token.access_token,dto);
        } catch (FeignException ex) {
            return ResponseEntity.status(ex.status())
                                 .body(new Response(false, null, ex.getMessage(), ex.status(),null,null));
        }
    }

    @Override
    public ResponseEntity<Response> getAllTreatments() {
        try {
            return clinicAdminServiceClient.getAllTreatments(token.access_token);
        } catch (FeignException ex) {
            return ResponseEntity.status(ex.status())
                                 .body(new Response(false, null, ex.getMessage(), ex.status(),null,null));
        }
    }

    @Override
    public ResponseEntity<Response> getTreatmentById(String id, String hospitalId) {
        try {
            return clinicAdminServiceClient.getTreatmentById(token.access_token,id, hospitalId);
        } catch (FeignException ex) {
            return ResponseEntity.status(ex.status())
                                 .body(new Response(false, null, ex.getMessage(), ex.status(),null,null));
        }
    }

    @Override
    public ResponseEntity<Response> deleteTreatmentById(String id, String hospitalId) {
        try {
            return clinicAdminServiceClient.deleteTreatmentById(token.access_token,id, hospitalId);
        } catch (FeignException ex) {
            return ResponseEntity.status(ex.status())
                                 .body(new Response(false, null, ex.getMessage(), ex.status(),null,null));
        }
    }

    @Override
    public ResponseEntity<Response> updateTreatmentById(String id, String hospitalId, TreatmentDTO dto) {
        try {
            return clinicAdminServiceClient.updateTreatmentById(token.access_token,id, hospitalId, dto);
        } catch (FeignException ex) {
            return ResponseEntity.status(ex.status())
                                 .body(new Response(false, null, ex.getMessage(), ex.status(),null,null));
        }
    }
}
