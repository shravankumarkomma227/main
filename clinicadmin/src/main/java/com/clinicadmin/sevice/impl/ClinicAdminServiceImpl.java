package com.clinicadmin.sevice.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import com.clinicadmin.dto.ClinicAdminDeviceIdDto;
import com.clinicadmin.dto.ClinicDTO;
import com.clinicadmin.dto.ClinicLoginRequestDTO;
import com.clinicadmin.dto.Response;
import com.clinicadmin.dto.UpdateClinicLoginCredentialsDTO;
import com.clinicadmin.entity.ClinicAdminDeviceTokenEntity;
import com.clinicadmin.feignclient.AdminServiceClient;
import com.clinicadmin.repository.ClinicAdminWebFcmTokenRepository;
import com.clinicadmin.service.ClinicAdminService;
import com.clinicadmin.utils.AutoCheckJwtToken;
import com.clinicadmin.utils.ExtractFeignMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;

@Service
public class ClinicAdminServiceImpl implements ClinicAdminService {

    @Autowired
    private AdminServiceClient adminServiceClient;
    
    
    @Autowired
	private AutoCheckJwtToken token;
    
    @Autowired
	private ClinicAdminWebFcmTokenRepository clinicAdminWebFcmTokenRepository;

	
    
    @Override
    public Response updateClinicCredentials(UpdateClinicLoginCredentialsDTO updatedCredentials, String userName) {
    	try {
        	Response response=adminServiceClient.updateClinicCredentials(token.access_token,updatedCredentials, userName);
        	return response;
        	}catch(FeignException e) {
        	Response res = new Response();
        	res.setStatus(e.status());
        	res.setMessage(ExtractFeignMessage.clearMessage(e));
        	res.setSuccess(false);
           return res;}
        }

    @Override
    public Response getClinicById(String hospitalId) {
    	try {
        	ResponseEntity<Response> response=adminServiceClient.getClinicById(token.access_token,hospitalId);
        	return response.getBody();
        	}catch(FeignException e) {
        	Response res = new Response();
        	res.setStatus(e.status());
        	res.setMessage(ExtractFeignMessage.clearMessage(e));
        	res.setSuccess(false);
           return res;}
        }

    @Override
    public Response updateClinic(String hospitalId, ClinicDTO dto) {
    	try {
        	Response response=adminServiceClient.updateClinic(token.access_token,hospitalId, dto);
        	return response;
        	}catch(FeignException e) {
        	Response res = new Response();
        	res.setStatus(e.status());
        	res.setMessage(ExtractFeignMessage.clearMessage(e));
        	res.setSuccess(false);
           return res;}
        }


    @Override
    public Response deleteClinic(String hospitalId) {
    	try {
        	Response response=adminServiceClient.deleteClinic(token.access_token,hospitalId);
        	return response;
        	}catch(FeignException e) {
        	Response res = new Response();
        	res.setStatus(e.status());
        	res.setMessage(ExtractFeignMessage.clearMessage(e));
        	res.setSuccess(false);
           return res;}
        }

    
    @Override
    public ResponseEntity<Response> getClincFcmToken(String clinicId,String branchId ){
    	Response res = new Response();
    	try {
    		ClinicAdminDeviceTokenEntity obj = clinicAdminWebFcmTokenRepository.findByClinicIdAndBranchId(clinicId, branchId);
    		ClinicAdminDeviceIdDto dto = new ObjectMapper().convertValue(obj,ClinicAdminDeviceIdDto.class );
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
