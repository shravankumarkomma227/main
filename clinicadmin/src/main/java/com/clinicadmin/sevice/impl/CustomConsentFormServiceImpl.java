package com.clinicadmin.sevice.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.clinicadmin.dto.CustomConsentFormDTO;
import com.clinicadmin.dto.Response;
import com.clinicadmin.dto.ResponseStructure;
import com.clinicadmin.dto.SubServicesDto;
import com.clinicadmin.entity.CustomConsentForm;
import com.clinicadmin.feignclient.ServiceFeignClient;
import com.clinicadmin.repository.CustomConsentFormRepository;
import com.clinicadmin.service.CustomConsentFormService;
import com.clinicadmin.utils.AutoCheckJwtToken;


@Service
public class CustomConsentFormServiceImpl implements CustomConsentFormService {

    @Autowired
    private CustomConsentFormRepository customConsentFormRepository;

    @Autowired
    private ServiceFeignClient serviceFeignClient;
    
    @Autowired
	private AutoCheckJwtToken token;
	

    @Override
    public Response addCustomConsentForm(String hospitalId, String consentFormType, CustomConsentFormDTO dto) {
        Response response = new Response();

        // -------------------------------
        // Basic Validations
        // -------------------------------
        if (hospitalId == null || hospitalId.trim().isEmpty()) {
            response.setSuccess(false);
            response.setMessage("Hospital ID cannot be null or empty");
            response.setStatus(400);
            return response;
        }

        if (consentFormType == null || (!consentFormType.equals("1") && !consentFormType.equals("2"))) {
            response.setSuccess(false);
            response.setMessage("Invalid Consent Form Type. Allowed values: 1 (Generic), 2 (Procedure)");
            response.setStatus(400);
            return response;
        }
//
//        if (dto == null || dto.getConsentFormQuetions() == null || dto.getConsentFormQuetions().isEmpty()) {
//            response.setSuccess(false);
//            response.setMessage("Consent form questions cannot be empty");
//            response.setStatus(400);
//            return response;
//        }

        try {
            // -------------------------------
            // Generic Consent Form (only one allowed per hospital)
            // -------------------------------
            if (consentFormType.equals("1")) {
                boolean exists = customConsentFormRepository
                        .findByHospitalIdAndConsentFormType(hospitalId, "1")
                        .isPresent();

                if (exists) {
                    response.setSuccess(false);
                    response.setMessage("Generic Consent Form already exists for this hospital");
                    response.setStatus(409); // Conflict
                    return response;
                }

                CustomConsentForm newForm = new CustomConsentForm();
                newForm.setConsentFormType(consentFormType);
                newForm.setHospitalId(hospitalId);
                newForm.setConsentFormQuetions(dto.getConsentFormQuetions());

                CustomConsentForm savedForm = customConsentFormRepository.save(newForm);

                CustomConsentFormDTO resDTO = mapToDTO(savedForm);
                response.setSuccess(true);
                response.setData(resDTO);
                response.setMessage("Generic Consent Questions added successfully");
                response.setStatus(200);
            }

            // -------------------------------
            // Procedure Consent Form (only one per subService)
            // -------------------------------
            else if (consentFormType.equals("2")) {
                if (dto.getSubServiceid() == null) {
                    response.setSuccess(false);
                    response.setMessage("SubService ID is required for Procedure Consent Form");
                    response.setStatus(400);
                    return response;
                }

                ResponseEntity<ResponseStructure<SubServicesDto>> subServiceResponse =
                        serviceFeignClient.getSubServiceBySubServiceId(token.access_token,hospitalId, dto.getSubServiceid());

                if (subServiceResponse == null || subServiceResponse.getBody() == null || subServiceResponse.getBody().getData() == null) {
                    response.setSuccess(false);
                    response.setMessage("SubService not found for ID: " + dto.getSubServiceid());
                    response.setStatus(404);
                    return response;
                }

                SubServicesDto subDTO = subServiceResponse.getBody().getData();

                boolean exists = customConsentFormRepository
                        .findByHospitalIdAndSubServiceid(hospitalId, subDTO.getSubServiceId())
                        .isPresent();

                if (exists) {
                    response.setSuccess(false);
                    response.setMessage("Procedure Consent Form already exists for SubService: " + subDTO.getSubServiceName());
                    response.setStatus(409); // Conflict
                    return response;
                }

                CustomConsentForm newForm = new CustomConsentForm();
                newForm.setHospitalId(hospitalId);
                newForm.setConsentFormType(consentFormType);
                newForm.setSubServiceid(subDTO.getSubServiceId());
                newForm.setSubServiceName(subDTO.getSubServiceName());
                newForm.setConsentFormQuetions(dto.getConsentFormQuetions());

                CustomConsentForm savedForm = customConsentFormRepository.save(newForm);

                CustomConsentFormDTO resDTO = mapToDTO(savedForm);
                response.setSuccess(true);
                response.setData(resDTO);
                response.setMessage("Procedure Consent Questions added successfully");
                response.setStatus(200);
            }
        } catch (Exception ex) {
            response.setSuccess(false);
            response.setMessage("Error while saving Consent Form: " + ex.getMessage());
            response.setStatus(500);
        }

        return response;
    }

    //------------------ helper method to map entity → DTO---------------------------------------------------
    
    private CustomConsentFormDTO mapToDTO(CustomConsentForm form) {
        CustomConsentFormDTO dto = new CustomConsentFormDTO();
        dto.setId(form.getId());
        dto.setHospitalId(form.getHospitalId());
        dto.setConsentFormType(form.getConsentFormType());
        dto.setConsentFormQuetions(form.getConsentFormQuetions());
        dto.setSubServiceid(form.getSubServiceid());
        dto.setSubServiceName(form.getSubServiceName());
        return dto;
    }
    
    @Override
    public Response updateCustomConsentForm(String hospitalId, String consentFormType, CustomConsentFormDTO dto) {
        Response response = new Response();

        // -------------------------------
        // Basic Validations
        // -------------------------------
        if (hospitalId == null || hospitalId.trim().isEmpty()) {
            response.setSuccess(false);
            response.setMessage("Hospital ID cannot be null or empty");
            response.setStatus(400);
            return response;
        }

        if (consentFormType == null || (!consentFormType.equals("1") && !consentFormType.equals("2"))) {
            response.setSuccess(false);
            response.setMessage("Invalid Consent Form Type. Allowed values: 1 (Generic), 2 (Procedure)");
            response.setStatus(400);
            return response;
        }

        if (dto == null || dto.getConsentFormQuetions() == null || dto.getConsentFormQuetions().isEmpty()) {
            response.setSuccess(false);
            response.setMessage("Consent form questions cannot be empty");
            response.setStatus(400);
            return response;
        }

        try {
            // -------------------------------
            // Update Generic Consent Form
            // -------------------------------
            if (consentFormType.equals("1")) {
                CustomConsentForm existingForm = customConsentFormRepository
                        .findByHospitalIdAndConsentFormType(hospitalId, "1")
                        .orElse(null);

                if (existingForm == null) {
                    response.setSuccess(false);
                    response.setMessage("Generic Consent Form not found for this hospital");
                    response.setStatus(404);
                    return response;
                }

                existingForm.setConsentFormQuetions(dto.getConsentFormQuetions());
                CustomConsentForm updatedForm = customConsentFormRepository.save(existingForm);

                CustomConsentFormDTO resDTO = mapToDTO(updatedForm);
                response.setSuccess(true);
                response.setData(resDTO);
                response.setMessage("Generic Consent Form updated successfully");
                response.setStatus(200);
            }

            // -------------------------------
            // Update Procedure Consent Form
            // -------------------------------
            else if (consentFormType.equals("2")) {
                if (dto.getSubServiceid() == null) {
                    response.setSuccess(false);
                    response.setMessage("SubService ID is required for Procedure Consent Form");
                    response.setStatus(400);
                    return response;
                }

                ResponseEntity<ResponseStructure<SubServicesDto>> subServiceResponse =
                        serviceFeignClient.getSubServiceBySubServiceId(token.access_token,hospitalId, dto.getSubServiceid());

                if (subServiceResponse == null || subServiceResponse.getBody() == null || subServiceResponse.getBody().getData() == null) {
                    response.setSuccess(false);
                    response.setMessage("SubService not found for ID: " + dto.getSubServiceid());
                    response.setStatus(404);
                    return response;
                }

                SubServicesDto subDTO = subServiceResponse.getBody().getData();

                CustomConsentForm existingForm = customConsentFormRepository
                        .findByHospitalIdAndSubServiceid(hospitalId, subDTO.getSubServiceId())
                        .orElse(null);

                if (existingForm == null) {
                    response.setSuccess(false);
                    response.setMessage("Procedure Consent Form not found for SubService: " + subDTO.getSubServiceName());
                    response.setStatus(404);
                    return response;
                }

                existingForm.setConsentFormQuetions(dto.getConsentFormQuetions());
                existingForm.setSubServiceName(subDTO.getSubServiceName());

                CustomConsentForm updatedForm = customConsentFormRepository.save(existingForm);

                CustomConsentFormDTO resDTO = mapToDTO(updatedForm);
                response.setSuccess(true);
                response.setData(resDTO);
                response.setMessage("Procedure Consent Form updated successfully");
                response.setStatus(200);
            }
        } catch (Exception ex) {
            response.setSuccess(false);
            response.setMessage("Error while updating Consent Form: " + ex.getMessage());
            response.setStatus(500);
        }

        return response;
    }
    @Override
    public Response getConsentForm(String hospitalId, String consentFormType) {
        Response response = new Response();

        try {
            if (hospitalId == null || hospitalId.trim().isEmpty()) {
                response.setSuccess(false);
                response.setMessage("Hospital ID cannot be null or empty");
                response.setStatus(400);
                return response;
            }

            if (consentFormType == null || (!consentFormType.equals("1") && !consentFormType.equals("2"))) {
                response.setSuccess(false);
                response.setMessage("Invalid Consent Form Type. Allowed values: 1 (Generic), 2 (Procedure)");
                response.setStatus(400);
                return response;
            }

            // Generic Consent Form
            if (consentFormType.equals("1")) {
                CustomConsentForm form = customConsentFormRepository
                        .findByHospitalIdAndConsentFormType(hospitalId, "1")
                        .orElse(null);

                if (form == null) {
                    response.setSuccess(false);
                    response.setMessage("Generic Consent Form not found for this hospital");
                    response.setStatus(404);
                    return response;
                }

                response.setSuccess(true);
                response.setData(mapToDTO(form));
                response.setMessage("Generic Consent Form retrieved successfully");
                response.setStatus(200);
            }
        } catch (Exception ex) {
            response.setSuccess(false);
            response.setMessage("Error while fetching Consent Form: " + ex.getMessage());
            response.setStatus(500);
        }

        return response;
    }

    @Override
    public Response getProcedureConsentForm(String hospitalId, String subServiceId) {
        Response response = new Response();

        try {
            if (hospitalId == null || hospitalId.trim().isEmpty()) {
                response.setSuccess(false);
                response.setMessage("Hospital ID cannot be null or empty");
                response.setStatus(400);
                return response;
            }

            if (subServiceId == null || subServiceId.trim().isEmpty()) {
                response.setSuccess(false);
                response.setMessage("SubService ID cannot be null or empty");
                response.setStatus(400);
                return response;
            }

            CustomConsentForm form = customConsentFormRepository
                    .findByHospitalIdAndSubServiceid(hospitalId, subServiceId)
                    .orElse(null);

            if (form == null) {
                response.setSuccess(false);
                response.setMessage("Procedure Consent Form not found for SubService: " + subServiceId);
                response.setStatus(404);
                return response;
            }

            response.setSuccess(true);
            response.setData(mapToDTO(form));
            response.setMessage("Procedure Consent Form retrieved successfully");
            response.setStatus(200);

        } catch (Exception ex) {
            response.setSuccess(false);
            response.setMessage("Error while fetching Procedure Consent Form: " + ex.getMessage());
            response.setStatus(500);
        }

        return response;
    }

}
