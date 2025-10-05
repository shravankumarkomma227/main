package com.dermacare.doctorservice.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.dermacare.doctorservice.dermacaredoctorutils.AutoCheckJwtToken;
import com.dermacare.doctorservice.feignclient.BookingFeignClient;
import com.dermacare.doctorservice.service.BookingService;
import feign.FeignException;

@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    private BookingFeignClient bookingFeignClient;
    
    @Autowired
   	private AutoCheckJwtToken token;
   	

    @Override
    public final ResponseEntity<?> getAppointmentsByPatientId(String patientId) {
        try {
            return bookingFeignClient.getAppointmentByPatientId(token.access_token,patientId);
        } catch (FeignException ex) {
            return ResponseEntity.status(ex.status()).body(ex.contentUTF8());
        }
    }

    @Override
    public final ResponseEntity<?> searchAppointmentsByInput(String input) {
        try {
            return bookingFeignClient.getAppointsByInput(token.access_token,input);
        } catch (FeignException ex) {
            return ResponseEntity.status(ex.status()).body(ex.contentUTF8());
        }
    }

    @Override
    public final ResponseEntity<?> getTodaysAppointments(String clinicId, String doctorId) {
        try {
            return bookingFeignClient.getTodayDoctorAppointmentsByDoctorId(token.access_token,clinicId, doctorId);
        } catch (FeignException ex) {
            return ResponseEntity.status(ex.status()).body(ex.contentUTF8());
        }
    }

    @Override
    public final ResponseEntity<?> getFilteredAppointments(String clinicId, String doctorId, String number) {
        try {
            return bookingFeignClient.filterDoctorAppointmentsByDoctorId(clinicId, doctorId, number);
        } catch (FeignException ex) {
            return ResponseEntity.status(ex.status()).body(ex.contentUTF8());
        }
    }

    @Override
    public final ResponseEntity<?> getCompletedAppointments(String clinicId, String doctorId) {
        try {
            return bookingFeignClient.filterDoctorAppointmentsByDoctorId(token.access_token,clinicId, doctorId);
        } catch (FeignException ex) {
            return ResponseEntity.status(ex.status()).body(ex.contentUTF8());
        }
    }

    @Override
    public final ResponseEntity<?> getConsultationTypeCounts(String clinicId, String doctorId) {
        try {
            return bookingFeignClient.getSizeOfConsultationTypesByDoctorId(token.access_token,clinicId, doctorId);
        } catch (FeignException ex) {
            return ResponseEntity.status(ex.status()).body(ex.contentUTF8());
        }
    }
}

