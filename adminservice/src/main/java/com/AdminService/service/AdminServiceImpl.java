package com.AdminService.service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import com.AdminService.dto.BookingResponse;
import com.AdminService.dto.BranchDTO;
import com.AdminService.dto.CategoryDto;
import com.AdminService.dto.ClinicCredentialsDTO;
import com.AdminService.dto.ClinicDTO;
import com.AdminService.dto.CustomerDTO;
import com.AdminService.dto.DoctorsDTO;
import com.AdminService.dto.DoctortInfo;
import com.AdminService.dto.ServicesDto;
import com.AdminService.dto.SubServicesDto;
import com.AdminService.dto.SubServicesInfoDto;
import com.AdminService.dto.UpdateClinicCredentials;
import com.AdminService.entity.Branch;
import com.AdminService.entity.Clinic;
import com.AdminService.entity.ClinicCredentials;
import com.AdminService.feign.BookingFeign;
import com.AdminService.feign.ClinicAdminFeign;
import com.AdminService.feign.CssFeign;
import com.AdminService.feign.CustomerFeign;
import com.AdminService.repository.BranchRepository;
import com.AdminService.repository.ClinicCredentialsRepository;
import com.AdminService.repository.ClinicRep;
import com.AdminService.util.AutoCheckJwtToken;
import com.AdminService.util.ExtractFeignMessage;
import com.AdminService.util.PermissionsUtil;
import com.AdminService.util.Response;
import com.AdminService.util.ResponseStructure;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import feign.FeignException.FeignClientException;

@Service
public class AdminServiceImpl implements AdminService {

	@Autowired
	private ClinicRep clinicRep;

	@Autowired

	private ClinicCredentialsRepository clinicCredentialsRepository;

	@Autowired

	private CssFeign cssFeign;

	@Autowired

	private CustomerFeign customerFeign;

	@Autowired
	private ClinicAdminFeign clinicAdminFeign;

	@Autowired
	private BookingFeign bookingFeign;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private AutoCheckJwtToken token;
	
	@Autowired
	private BranchRepository branchRepository;
//	@Autowired
//	private QuetionsAndAnswerForAddClinicRepository quetionsAndAnswerForAddClinicRepository;

	// CLINIC MANAGEMENT

	// Create Clinic
	@Override

	public Response createClinic(ClinicDTO clinic) {

		Response response = new Response();
		try {
			Clinic existingClinic = clinicRep.findByContactNumber(clinic.getContactNumber());

			if (existingClinic != null) {

				response.setMessage("ContactNumber is already exist");

				response.setSuccess(false);

				response.setStatus(409);

				return response;
			}

			Clinic savedClinic = new ObjectMapper().convertValue(clinic, Clinic.class);
			savedClinic.setHospitalId(generateHospitalId());
			// Pharmacist Info
			savedClinic.setHasPharmacist(clinic.getHasPharmacist());

			if ("Yes".equalsIgnoreCase(clinic.getHasPharmacist())) {

				if (clinic.getPharmacistCertificate() != null && !clinic.getPharmacistCertificate().isEmpty()) {

					try {

						savedClinic.setPharmacistCertificate(
								Base64.getDecoder().decode(clinic.getPharmacistCertificate()));

					} catch (Exception e) {

						throw new IllegalArgumentException("Invalid Base64 in pharmacistCertificate");

					}
				} else {
					throw new IllegalArgumentException("Pharmacist Certificate is required when hasPharmacist is Yes");
				}
			} else {
				savedClinic.setPharmacistCertificate(null);
			}

			// Medicines Handling
			savedClinic.setMedicinesSoldOnSite(clinic.getMedicinesSoldOnSite());

			if ("Yes".equalsIgnoreCase(clinic.getMedicinesSoldOnSite())) {

				if (clinic.getDrugLicenseCertificate() != null && !clinic.getDrugLicenseCertificate().isEmpty()) {

					try {

						savedClinic.setDrugLicenseCertificate(
								Base64.getDecoder().decode(clinic.getDrugLicenseCertificate()));

					} catch (Exception e) {

						throw new IllegalArgumentException("Invalid Base64 in drugLicenseCertificate");

					}

				} else {

					throw new IllegalArgumentException(
							"Drug License Certificate is required when medicinesSoldOnSite is Yes");

				}
				if (clinic.getDrugLicenseFormType() != null && !clinic.getDrugLicenseFormType().isEmpty()) {

					try {

						savedClinic.setDrugLicenseFormType(Base64.getDecoder().decode(clinic.getDrugLicenseFormType()));

					} catch (Exception e) {

						throw new IllegalArgumentException("Invalid Base64 in drugLicenseFormType");

					}
				} else {
					throw new IllegalArgumentException(
							"Drug License Form Type is required when medicinesSoldOnSite is Yes");
				}
			} else {
				savedClinic.setDrugLicenseCertificate(null);
				savedClinic.setDrugLicenseFormType(null);
			}
			Clinic saved = clinicRep.save(savedClinic);
			 if (saved != null) {
				 // 🔹 Auto-create default branch
		            Branch branch = new Branch();
		            branch.setClinicId(saved.getHospitalId());  // e.g., H_1
		            branch.setBranchId(saved.getHospitalId() + "-B_1"); // e.g., H_1-B_1
		            branch.setBranchName(saved.getName() + " Main Branch");
		            branch.setAddress(saved.getAddress());
		            branch.setCity(saved.getCity());
		            branch.setContactNumber(saved.getContactNumber());
		            branch.setEmail(saved.getEmailAddress());
		            branch.setLatitude(String.valueOf(saved.getLatitude()));
		            branch.setLongitude(String.valueOf(saved.getLongitude()));
		            branch.setVirtualClinicTour(saved.getWalkthrough());
		            branch.setRole(saved.getRole());
		            branch.setPermissions(saved.getPermissions());
		            branch.setVirtualClinicTour(saved.getWalkthrough());
		            branch.setRole("ADMIN");
		            branch.setPermissions(PermissionsUtil.getAdminPermissions());

		            Branch savedBranch = branchRepository.save(branch); 
								 
		            // Generate clinic credentials
		        	 ClinicCredentials credentials = new ClinicCredentials();
		        	   Map<String, Object> data = new HashMap<>();
			            data.put("clinicUsername", saved.getHospitalId());
			            data.put("branchId", savedBranch.getBranchId()); // ✅ Only return branchId, no passwords
		            credentials.setUserName(savedBranch.getBranchId());
		            credentials.setPassword(generatePassword(9));
		            data.put("clinicTemporaryPassword",credentials.getPassword());
		            credentials.setHospitalName(saved.getName());
		            credentials.setPassword(passwordEncoder.encode(credentials.getPassword()));						 
					 List<String> roles = new ArrayList<>();
					 roles.add("ROLE_CLINICADMIN");
					credentials.setRoles(roles);
					credentials.setHospitalId(saved.getHospitalId());
		            clinicCredentialsRepository.save(credentials);
	            
		            // attach to clinic
		            saved.setBranches(List.of(savedBranch));
		            clinicRep.save(saved);
		            response.setData(data);
		            response.setMessage("Clinic and default branch created successfully");
		            response.setSuccess(true);
		            response.setStatus(200);
		            return response;
		        }

		    } catch (Exception e) {
		        response.setMessage("Error occurred while creating the clinic: " + e.getMessage());
		        response.setSuccess(false);
		        response.setStatus(500);
		    }

		    return response;
		}
	
	
	@Override
	public Response getClinicById(String clinicId) {

		Response response = new Response();
		try {
			Clinic clinic = clinicRep.findByHospitalId(clinicId);

			if (clinic != null) {

				ClinicDTO clnc = new ObjectMapper().convertValue(clinic, ClinicDTO.class);

				response.setMessage("Clinic fetched successfully");

				response.setSuccess(true);

				response.setStatus(200);

				response.setData(clnc);

				return response;
			} else {
				response.setMessage("Clinic not found");

				response.setSuccess(false);

				response.setStatus(404);

				return response;

			}

		} catch (Exception e) {

			response.setMessage("Error occurred while fetching clinic: " + e.getMessage());

			response.setSuccess(false);

			response.setStatus(500);

			return response;

		}

	}

	@Override
	public Response getAllClinics() {

		Response response = new Response();

		try {

			List<Clinic> clinics = clinicRep.findAll();

			if (!clinics.isEmpty()) {
				List<ClinicDTO> list = new ObjectMapper().convertValue(clinics, new TypeReference<List<ClinicDTO>>() {
				});

				response.setData(list);

				response.setMessage("Clinics fetched successfully");

				response.setSuccess(true);

				response.setStatus(200);

			} else {

				response.setData(null);

				response.setMessage("Clinics Not Found");

				response.setSuccess(true); // Still success, but no data

				response.setStatus(200);

			}

		} catch (Exception e) {

			response.setData(null);

			response.setMessage("Error: " + e.getMessage());

			response.setSuccess(false);

			response.setStatus(500);

		}

		return response;

	}

	@Override
	public Response updateClinic(String clinicId, ClinicDTO clinic) {

		Response response = new Response();

		try {

			Clinic savedClinic = clinicRep.findByHospitalId(clinicId);

			if (savedClinic != null) {
				if (clinic.getAddress() != null)
					savedClinic.setAddress(clinic.getAddress());

				if (clinic.getCity() != null)
					savedClinic.setCity(clinic.getCity());

				if (clinic.getName() != null) {

					savedClinic.setName(clinic.getName());
					// Update hospital name in credentials

					List<ClinicCredentials> credsList = clinicCredentialsRepository
							.findAllByUserName(savedClinic.getHospitalId());

					for (ClinicCredentials creds : credsList) {

						creds.setHospitalName(clinic.getName());

						clinicCredentialsRepository.save(creds);

					}

				}
				// Hospital Logo

				if (clinic.getHospitalLogo() != null && !clinic.getHospitalLogo().isEmpty()) {

					savedClinic.setHospitalLogo(Base64.getDecoder().decode(clinic.getHospitalLogo()));

				}
				// Hospital Documents

				if (clinic.getHospitalDocuments() != null && !clinic.getHospitalDocuments().isEmpty()) {

					savedClinic.setHospitalDocuments(Base64.getDecoder().decode(clinic.getHospitalDocuments()));

				}
				// Contractor Documents

				if (clinic.getContractorDocuments() != null && !clinic.getContractorDocuments().isEmpty()) {

					savedClinic.setContractorDocuments(Base64.getDecoder().decode(clinic.getContractorDocuments()));

				}
				if (clinic.getHospitalOverallRating() != 0.0) {

					savedClinic.setHospitalOverallRating(clinic.getHospitalOverallRating());

				}
				if (clinic.getClosingTime() != null)
					savedClinic.setClosingTime(clinic.getClosingTime());

				if (clinic.getOpeningTime() != null)
					savedClinic.setOpeningTime(clinic.getOpeningTime());

				if (clinic.getContactNumber() != null)
					savedClinic.setContactNumber(clinic.getContactNumber());

				if (clinic.getEmailAddress() != null)
					savedClinic.setEmailAddress(clinic.getEmailAddress());

				if (clinic.getWebsite() != null)
					savedClinic.setWebsite(clinic.getWebsite());

				if (clinic.getLicenseNumber() != null)
					savedClinic.setLicenseNumber(clinic.getLicenseNumber());

				if (clinic.getIssuingAuthority() != null)
					savedClinic.setIssuingAuthority(clinic.getIssuingAuthority());

				// Optional hospital ID update (not recommended usually)

				if (clinic.getHospitalId() != null && !clinic.getHospitalId().equals(clinicId)) {

					savedClinic.setHospitalId(clinic.getHospitalId());

				}
			  			
				// Medicines Sold On Site

				savedClinic.setMedicinesSoldOnSite(clinic.getMedicinesSoldOnSite());

				if ("Yes".equalsIgnoreCase(clinic.getMedicinesSoldOnSite())) {

					if (clinic.getDrugLicenseCertificate() != null && !clinic.getDrugLicenseCertificate().isEmpty()) {

						savedClinic.setDrugLicenseCertificate(
								Base64.getDecoder().decode(clinic.getDrugLicenseCertificate()));

					}

					if (clinic.getDrugLicenseFormType() != null && !clinic.getDrugLicenseFormType().isEmpty()) {

						savedClinic.setDrugLicenseFormType(Base64.getDecoder().decode(clinic.getDrugLicenseFormType()));

					}

				} else {

					savedClinic.setDrugLicenseCertificate(null);

					savedClinic.setDrugLicenseFormType(null);

				}
				// Pharmacist Section

				savedClinic.setHasPharmacist(clinic.getHasPharmacist());

				if ("Yes".equalsIgnoreCase(clinic.getHasPharmacist())) {

					if (clinic.getPharmacistCertificate() != null && !clinic.getPharmacistCertificate().isEmpty()) {

						savedClinic.setPharmacistCertificate(
								Base64.getDecoder().decode(clinic.getPharmacistCertificate()));}

				} else {
					savedClinic.setPharmacistCertificate(null);

				}
				// Other Certificates

				if (clinic.getClinicType() != null)
					savedClinic.setClinicType(clinic.getClinicType());

				if (clinic.getClinicalEstablishmentCertificate() != null
						&& !clinic.getClinicalEstablishmentCertificate().isEmpty())

					savedClinic.setClinicalEstablishmentCertificate(
							Base64.getDecoder().decode(clinic.getClinicalEstablishmentCertificate()));

				if (clinic.getBusinessRegistrationCertificate() != null
						&& !clinic.getBusinessRegistrationCertificate().isEmpty())

					savedClinic.setBusinessRegistrationCertificate(
							Base64.getDecoder().decode(clinic.getBusinessRegistrationCertificate()));

				if (clinic.getBiomedicalWasteManagementAuth() != null
						&& !clinic.getBiomedicalWasteManagementAuth().isEmpty())

					savedClinic.setBiomedicalWasteManagementAuth(
							Base64.getDecoder().decode(clinic.getBiomedicalWasteManagementAuth()));

				if (clinic.getTradeLicense() != null && !clinic.getTradeLicense().isEmpty())

					savedClinic.setTradeLicense(Base64.getDecoder().decode(clinic.getTradeLicense()));

				if (clinic.getFireSafetyCertificate() != null && !clinic.getFireSafetyCertificate().isEmpty())

					savedClinic.setFireSafetyCertificate(Base64.getDecoder().decode(clinic.getFireSafetyCertificate()));

				if (clinic.getProfessionalIndemnityInsurance() != null
						&& !clinic.getProfessionalIndemnityInsurance().isEmpty())

					savedClinic.setProfessionalIndemnityInsurance(
							Base64.getDecoder().decode(clinic.getProfessionalIndemnityInsurance()));

				if (clinic.getGstRegistrationCertificate() != null && !clinic.getGstRegistrationCertificate().isEmpty())

					savedClinic.setGstRegistrationCertificate(
							Base64.getDecoder().decode(clinic.getGstRegistrationCertificate()));

				// Others - List<byte[]>

				if (clinic.getOthers() != null) {

					List<byte[]> othersList = new ArrayList<>();

					for (String base64File : clinic.getOthers()) {

						if (base64File != null && !base64File.isEmpty()) {

							othersList.add(Base64.getDecoder().decode(base64File));

						}

					}

					savedClinic.setOthers(othersList);
				}
				savedClinic.setFreeFollowUps(clinic.getFreeFollowUps());
				savedClinic.setLatitude(clinic.getLatitude());
				savedClinic.setLongitude(clinic.getLongitude());
				if (clinic.getWalkthrough() != null)
					savedClinic.setWalkthrough(clinic.getWalkthrough());
				savedClinic.setNabhScore(clinic.getNabhScore());

				if (clinic.getConsultationExpiration() != null && !clinic.getConsultationExpiration().isEmpty()) {

					savedClinic.setConsultationExpiration(clinic.getConsultationExpiration());

				}

				// Social Media

				if (clinic.getInstagramHandle() != null)
					savedClinic.setInstagramHandle(clinic.getInstagramHandle());

				if (clinic.getTwitterHandle() != null)
					savedClinic.setTwitterHandle(clinic.getTwitterHandle());

				if (clinic.getFacebookHandle() != null)
					savedClinic.setFacebookHandle(clinic.getFacebookHandle());

				// Recommended

				savedClinic.setRecommended(clinic.isRecommended());
				// Save updates

				clinicRep.save(savedClinic);
				response.setMessage("Clinic updated successfully");

				response.setSuccess(true);

				response.setStatus(200);

			} else {

				response.setMessage("Clinic not found for update");

				response.setSuccess(false);

				response.setStatus(404);

			}

		} catch (Exception e) {

			response.setMessage("Error occurred while updating the clinic: " + e.getMessage());

			response.setSuccess(false);

			response.setStatus(500);

		}

		return response;

	}

	@Override

	public Response deleteClinic(String clinicId) {

		Response response = new Response();

		try {

			Clinic clinic = clinicRep.findByHospitalId(clinicId);

			if (clinic != null) {

				// Delete Clinic

				clinicRep.deleteByHospitalId(clinicId);

				// Delete associated credentials

				clinicCredentialsRepository.deleteByUserName(clinicId);

				response.setMessage("Clinic deleted successfully");

				response.setSuccess(true);

				response.setStatus(200); // OK

			} else {

				response.setMessage("Clinic not found for deletion");

				response.setSuccess(false);

				response.setStatus(404); // Not Found

			}

		} catch (Exception e) {

			response.setMessage("Error occurred while deleting the clinic: " + e.getMessage());

			response.setSuccess(false);

			response.setStatus(500); // Internal Server Error

		}

		return response;

	}

	// GENERATE RANDOM PASSWORD

	private static String generatePassword(int length) {

		if (length < 4) {

			throw new IllegalArgumentException("Password length must be at least 4.");

		}

		String upperCaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

		String lowerCaseLetters = "abcdefghijklmnopqrstuvwxyz";

		String digits = "0123456789";

		String specialChars = "!@#$&_";

		Random random = new Random();

		// First character - must be uppercase

		char firstChar = upperCaseLetters.charAt(random.nextInt(upperCaseLetters.length()));

		// Ensure at least one special character and one digit

		char specialChar = specialChars.charAt(random.nextInt(specialChars.length()));

		char digit = digits.charAt(random.nextInt(digits.length()));

		// Remaining characters pool

		String allChars = upperCaseLetters + lowerCaseLetters + digits + specialChars;

		StringBuilder remaining = new StringBuilder();

		for (int i = 0; i < length - 3; i++) {

			remaining.append(allChars.charAt(random.nextInt(allChars.length())));

		}

		// Build the password and shuffle to randomize the positions (except first char)

		List<Character> passwordChars = new ArrayList<>();

		for (char c : remaining.toString().toCharArray()) {

			passwordChars.add(c);

		}

		// Add guaranteed special and digit

		passwordChars.add(specialChar);

		passwordChars.add(digit);

		// Shuffle rest except first character

		Collections.shuffle(passwordChars);

		StringBuilder password = new StringBuilder();

		password.append(firstChar);

		for (char c : passwordChars) {

			password.append(c);

		}

		return password.toString();

	}

	// METHOD TO GENERATE SEQUANTIAL HOSPITAL ID

	public String generateHospitalId() {

		List<Clinic> allClinics = clinicRep.findAll(); // not optimal for huge DB

		int maxId = 0;

		Pattern pattern = Pattern.compile("H_(\\d+)");

		for (Clinic clinic : allClinics) {

			String id = clinic.getHospitalId();

			Matcher matcher = pattern.matcher(id);

			if (matcher.find()) {

				int num = Integer.parseInt(matcher.group(1));

				if (num > maxId) {

					maxId = num;

				}

			}

		}

		int nextId = maxId + 1;

		return "H_" + nextId;

	}

// CLINIC CREDENTIALS CRUD

	@Override

	public Response getClinicCredentials(String userName) {

		Response response = new Response();

		try {

			ClinicCredentials clinicCredentials = clinicCredentialsRepository.findByUserName(userName);

			if (clinicCredentials != null) {

				ClinicCredentialsDTO clinicCredentialsDTO = new ClinicCredentialsDTO();

				clinicCredentialsDTO.setUserName(clinicCredentials.getUserName());

				clinicCredentialsDTO.setPassword(clinicCredentials.getPassword());

				clinicCredentialsDTO.setHospitalName(clinicCredentials.getHospitalName());

				response.setSuccess(true);

				response.setData(clinicCredentialsDTO);

				response.setMessage("Clinic Credentials Found.");

				response.setStatus(200); // HTTP status for OK

				return response;

			} else {

				response.setSuccess(true);

				response.setMessage("Clinic Credentials Are Not Found.");

				response.setStatus(200); // HTTP status for Not Found

				return response;

			}

		} catch (Exception e) {

			response.setSuccess(false);

			response.setMessage("Error Retrieving Clinic Credentials: " + e.getMessage());

			response.setStatus(500); // Internal server error

		}

		return response;

	}

	@Override

	public Response updateClinicCredentials(UpdateClinicCredentials credentials, String userName) {

		Response response = new Response();

		try {

			ClinicCredentials existingCredentials = clinicCredentialsRepository.

					findByUserNameAndPassword(userName, credentials.getPassword());

			ClinicCredentials existUserName = clinicCredentialsRepository.findByUserName(userName);

			if (existUserName == null) {

				response.setSuccess(false);

				response.setMessage("Incorrect UserName");

				response.setStatus(401);

				return response;

			}

			if (existingCredentials != null) {

				if (credentials.getNewPassword().equalsIgnoreCase(credentials.getConfirmPassword())) {

					existingCredentials.setPassword(credentials.getNewPassword());

					ClinicCredentials c = clinicCredentialsRepository.save(existingCredentials);

					if (c != null) {

						response.setSuccess(true);

						response.setData(null);

						response.setMessage("Clinic Credentials Updated Successfully.");

						response.setStatus(200);

						return response;

					} else {

						response.setSuccess(false);

						response.setMessage("Failed To Upddate Clinic Credentials.");

						response.setStatus(404);

						return response;// HTTP status for Not Found

					}
				} else {

					response.setSuccess(false);

					response.setMessage("New password and confirm password do not match.");

					response.setStatus(401);

					return response;

				}
			} else {

				response.setSuccess(false);

				response.setMessage("Incorrect Password.");

				response.setStatus(401);

				return response;

			}
		}

		catch (Exception e) {

			response.setSuccess(false);

			response.setMessage("Error updating clinic credentials: " + e.getMessage());

			response.setStatus(500); // Internal server error

			return response;
		}

	}

	@Override
	public Response deleteClinicCredentials(String userName) {
		Response response = new Response();

		try {

			ClinicCredentials clinicCredentials = clinicCredentialsRepository.findByUserName(userName);

			if (clinicCredentials != null) {

				clinicCredentialsRepository.delete(clinicCredentials);

				clinicRep.deleteByHospitalId(userName);

				response.setSuccess(true);

				response.setMessage("Clinic Credentials Deleted Successfully.");

				response.setStatus(200); // HTTP status for OK

				return response;

			} else {

				response.setSuccess(false);

				response.setMessage("Clinic Credentials Are Not Found.");

				response.setStatus(404); // HTTP status for Not Found

				return response;

			}

		} catch (Exception e) {

			response.setSuccess(false);

			response.setMessage("Error Deleting Clinic Credentials: " + e.getMessage());

			response.setStatus(500); // Internal server error

		}

		return response;

	}

	// Category Management

	@Override

	public ResponseEntity<ResponseStructure<CategoryDto>> addNewCategory(CategoryDto dto) {

		ResponseStructure<CategoryDto> response = new ResponseStructure<CategoryDto>();

		try {
			return cssFeign.addNewCategory(token.access_token, dto);
			
		}catch (FeignException e) {
			response = new ResponseStructure<CategoryDto>(null,e.getMessage(),null,e.status());
		return ResponseEntity.status(response.getStatusCode()).body(response);
	}}

	@Override

	public Response getAllCategory() {

		Response response = new Response();

		   try {
			ResponseEntity<ResponseStructure<List<CategoryDto>>> res = cssFeign.getAllCategory(token.access_token);
			if (res.hasBody()) {

				ResponseStructure<List<CategoryDto>> rs = res.getBody();

				response.setData(rs);

				response.setStatus(rs.getStatusCode());

			}

		} catch (FeignException e) {

			response.setStatus(e.status());

			response.setMessage(ExtractFeignMessage.clearMessage(e));

			response.setSuccess(false);

		}

		return response;

	}

	@Override

	public Response getCategoryById(String CategoryId) {

		Response response = new Response();

		try {

			ResponseEntity<ResponseStructure<CategoryDto>> res = cssFeign.getCategoryById(token.access_token,CategoryId);

			if (res.hasBody()) {

				ResponseStructure<CategoryDto> rs = res.getBody();

				response.setData(rs);

				response.setStatus(rs.getStatusCode());

			}

		} catch (FeignException e) {

			response.setStatus(e.status());

			response.setMessage(ExtractFeignMessage.clearMessage(e));

			response.setSuccess(false);

		}

		return response;

	}

	@Override

	public Response deleteCategoryById(

			String categoryId) {

		Response response = new Response();

		try {

			ResponseEntity<ResponseStructure<String>> res = cssFeign.deleteCategory(token.access_token,new ObjectId(categoryId));

			if (res.hasBody()) {

				ResponseStructure<String> rs = res.getBody();

				response.setData(rs);

				response.setStatus(rs.getStatusCode());

			}

		} catch (FeignException e) {

			response.setStatus(e.status());

			response.setMessage(ExtractFeignMessage.clearMessage(e));

			response.setSuccess(false);

		}

		return response;

	}

	@Override

	public Response updateCategory(String categoryId, CategoryDto updatedCategory) {

		Response response = new Response();

		try {

			ResponseEntity<ResponseStructure<CategoryDto>> res = cssFeign.updateCategory(token.access_token,new ObjectId(categoryId),
					updatedCategory);

			if (res.hasBody()) {

				ResponseStructure<CategoryDto> rs = res.getBody();

				response.setData(rs);

				response.setStatus(rs.getStatusCode());

			}

		} catch (FeignException e) {

			response.setStatus(e.status());

			response.setMessage(ExtractFeignMessage.clearMessage(e));

			response.setSuccess(false);

		}

		return response;

	}

	// SERVICES MANAGEMENT

	@Override

	public Response addService(ServicesDto dto) {

		Response response = new Response();

		try {

			ResponseEntity<ResponseStructure<ServicesDto>> res = cssFeign.addService(token.access_token,dto);

			if (res.hasBody()) {

				ResponseStructure<ServicesDto> rs = res.getBody();

				response.setData(rs);

				response.setStatus(rs.getStatusCode());

			}

		} catch (FeignException e) {

			response.setStatus(e.status());

			response.setMessage(ExtractFeignMessage.clearMessage(e));

			response.setSuccess(false);

		}

		return response;

	}

	@Override

	public Response getServiceById(String categoryId) {

		Response response = new Response();

		try {

			ResponseEntity<ResponseStructure<List<ServicesDto>>> res = cssFeign.getServiceById(token.access_token,categoryId);

			if (res.getBody() != null) {

				ResponseStructure<List<ServicesDto>> rs = res.getBody();

				response.setData(rs);

				response.setStatus(rs.getStatusCode());

			}

		} catch (FeignException e) {

			response.setStatus(e.status());

			response.setMessage(ExtractFeignMessage.clearMessage(e));

			response.setSuccess(false);

		}

		return response;

	}

	@Override

	public Response getServiceByServiceId(String serviceId) {

		Response response = new Response();

		try {

			ResponseEntity<ResponseStructure<ServicesDto>> res = cssFeign.getServiceByServiceId(token.access_token,serviceId);

			if (res.hasBody()) {

				ResponseStructure<ServicesDto> rs = res.getBody();

				response.setData(rs);

				response.setStatus(rs.getStatusCode());

			}

		} catch (FeignException e) {

			response.setStatus(e.status());

			response.setMessage(ExtractFeignMessage.clearMessage(e));

			response.setSuccess(false);

		}

		return response;

	}

	@Override

	public Response deleteService(String serviceId) {

		Response response = new Response();

		try {

			ResponseEntity<ResponseStructure<String>> res = cssFeign.deleteService(token.access_token,serviceId);

			if (res.hasBody()) {

				ResponseStructure<String> rs = res.getBody();

				response.setData(rs);

				response.setStatus(rs.getStatusCode());

			}

		} catch (FeignException e) {

			response.setStatus(e.status());

			response.setMessage(ExtractFeignMessage.clearMessage(e));

			response.setSuccess(false);

		}

		return response;

	}

	@Override

	public Response updateByServiceId(String serviceId,

			@RequestBody ServicesDto domainServices) {

		Response response = new Response();

		try {

			ResponseEntity<ResponseStructure<ServicesDto>> res = cssFeign.

					updateByServiceId(token.access_token,serviceId, domainServices);

			if (res.hasBody()) {

				ResponseStructure<ServicesDto> rs = res.getBody();

				response.setData(rs);

				response.setStatus(rs.getStatusCode());

			}

		} catch (FeignException e) {

			response.setStatus(e.status());

			response.setMessage(ExtractFeignMessage.clearMessage(e));

			response.setSuccess(false);

		}

		return response;

	}

	@Override

	public Response getAllServices() {

		Response response = new Response();

		try {

			ResponseEntity<ResponseStructure<List<ServicesDto>>> res = cssFeign.getAllServices(token.access_token);

			if (res.hasBody()) {

				ResponseStructure<List<ServicesDto>> rs = res.getBody();

				response.setData(rs);

				response.setStatus(rs.getStatusCode());

			}

		} catch (FeignException e) {

			response.setStatus(e.status());

			response.setMessage(ExtractFeignMessage.clearMessage(e));

			response.setSuccess(false);

		}

		return response;

	}

	// SUBSERVICE MANAGEMENT

	@Override

	public Response addSubService(SubServicesInfoDto dto) {

		Response response = new Response();

		try {

			ResponseEntity<Response> res = cssFeign.addSubService(token.access_token,dto);

			return res.getBody();

		} catch (FeignException e) {

			response.setStatus(500);

			response.setMessage(ExtractFeignMessage.clearMessage(e));

			response.setSuccess(false);

			return response;

		}

	}

	@Override

	public Response getSubServiceByIdCategory(String categoryId) {

		Response response = new Response();

		try {

			ResponseEntity<Response> res = cssFeign.getSubServiceInfoByIdCategory(token.access_token,categoryId);

			return res.getBody();

		} catch (FeignException e) {

			response.setStatus(500);

			response.setMessage(ExtractFeignMessage.clearMessage(e));

			response.setSuccess(false);

			return response;

		}

	}

	@Override

	public Response getSubServicesByServiceId(String serviceId) {

		Response response = new Response();

		try {

			ResponseEntity<Response> res = cssFeign.getSubServicesInfoByServiceId(token.access_token,serviceId);

			return res.getBody();

		} catch (FeignException e) {

			response.setStatus(500);

			response.setMessage(ExtractFeignMessage.clearMessage(e));

			response.setSuccess(false);

			return response;

		}

	}

	@Override

	public Response getSubServiceBySubServiceId(String subServiceId) {

		Response response = new Response();

		try {

			ResponseEntity<Response> res = cssFeign.getSubServiceBySubServiceId(token.access_token,subServiceId);

			return res.getBody();

		} catch (FeignException e) {

			response.setStatus(500);

			response.setMessage(ExtractFeignMessage.clearMessage(e));

			response.setSuccess(false);

			return response;

		}

	}

	@Override

	public Response deleteSubService(String subServiceId) {

		Response response = new Response();

		try {

			ResponseEntity<Response> res = cssFeign.deleteSubServiceInfo(token.access_token,subServiceId);

			return res.getBody();

		} catch (FeignException e) {

			response.setStatus(500);

			response.setMessage(ExtractFeignMessage.clearMessage(e));

			response.setSuccess(false);

			return response;

		}

	}

	@Override

	public Response updateBySubServiceId(String subServiceId, SubServicesInfoDto domainServices) {

		Response response = new Response();

		try {

			ResponseEntity<Response> res = cssFeign.updateBySubServiceId(token.access_token,subServiceId, domainServices);

			return res.getBody();

		} catch (FeignException e) {

			response.setStatus(500);

			response.setMessage(ExtractFeignMessage.clearMessage(e));

			response.setSuccess(false);

			return response;

		}

	}

	@Override

	public Response getAllSubServices() {

		Response response = new Response();

		try {

			ResponseEntity<Response> res = cssFeign.getAllSubServicesInfo(token.access_token);

			return res.getBody();

		} catch (FeignException e) {

			response.setStatus(500);

			response.setMessage(ExtractFeignMessage.clearMessage(e));

			response.setSuccess(false);

			return response;

		}

	}

	// CUSTOMER MANAGEMENT

	@Override

	public Response saveCustomerBasicDetails(CustomerDTO customerDTO) {

		Response response = new Response();

		try {

			ResponseEntity<Response> res = customerFeign.saveCustomerBasicDetails(token.access_token,customerDTO);

			if (res != null) {

				Response rs = res.getBody();

				return rs;

			}
		} catch (FeignException e) {

			response.setStatus(e.status());

			response.setMessage(ExtractFeignMessage.clearMessage(e));

			response.setSuccess(false);

		}

		return response;
	}

	@Override

	public ResponseEntity<?> getCustomerByUsernameMobileEmail(String input) {

		Response response = new Response();

		try {

			ResponseEntity<?> res = customerFeign.getCustomerByUsernameMobileEmail(token.access_token,input);

			if (res.getBody() != null) {

				response.setData(res.getBody());

				response.setStatus(res.getStatusCode().value());

				return ResponseEntity.status(res.getStatusCode().value()).body(res.getBody());
			}

			else {

				response.setMessage("Customer Details Not Found");

				response.setStatus(200);

				response.setSuccess(true);

				return ResponseEntity.status(200).body(response);
			}

		} catch (FeignException e) {

			response.setMessage(e.getMessage());

			response.setStatus(e.status());

			response.setSuccess(false);

			return ResponseEntity.status(e.status()).body(response);

		}
	}

	@Override

	public Response getCustomerBasicDetails(String mobileNumber) {

		Response response = new Response();

		try {

			ResponseEntity<Response> res = customerFeign.getCustomerBasicDetails(token.access_token, mobileNumber);

			if (res != null) {

				Response rs = res.getBody();

				return rs;

			}
		} catch (FeignException e) {

			response.setStatus(e.status());

			response.setMessage(ExtractFeignMessage.clearMessage(e));

			response.setSuccess(false);

		}

		return response;

	}

	@Override

	public Response getAllCustomers() {

		Response response = new Response();

		try {

			ResponseEntity<Response> res = customerFeign.getAllCustomers(token.access_token);

			if (res != null) {

				Response rs = res.getBody();

				return rs;

			}
		} catch (FeignException e) {

			response.setStatus(e.status());

			response.setMessage(ExtractFeignMessage.clearMessage(e));

			response.setSuccess(false);

		}

		return response;

	}

	@Override

	public Response updateCustomerBasicDetails(CustomerDTO customerDTO, String mobileNumber) {

		Response response = new Response();

		try {

			ResponseEntity<Response> res = customerFeign.updateCustomerBasicDetails(token.access_token,customerDTO, mobileNumber);

			if (res != null) {

				Response rs = res.getBody();

				return rs;

			}
		} catch (FeignException e) {

			response.setStatus(e.status());

			response.setMessage(ExtractFeignMessage.clearMessage(e));

			response.setSuccess(false);

		}

		return response;

	}

	@Override

	public Response deleteCustomerBasicDetails(String mobileNumber) {

		Response response = new Response();

		try {

			ResponseEntity<Response> res = customerFeign.deleteCustomerBasicDetails(token.access_token,mobileNumber);

			if (res != null) {

				Response rs = res.getBody();

				return rs;

			}
		} catch (FeignException e) {

			response.setStatus(e.status());

			response.setMessage(ExtractFeignMessage.clearMessage(e));

			response.setSuccess(false);

		}

		return response;

	}

//GETALLSUBSERVICES

	@Override

	public Response getAllSubServicesFromClincAdmin() {

		Response response = new Response();

		try {

			ResponseEntity<ResponseStructure<List<SubServicesDto>>> res = clinicAdminFeign.getAllSubServices(token.access_token);

			if (res.getBody().getData() != null) {

				response.setStatus(res.getBody().getHttpStatus().value());

				response.setData(res.getBody());

				return response;

			}
		} catch (FeignException e) {

			response.setStatus(e.status());

			response.setMessage(ExtractFeignMessage.clearMessage(e));

			response.setSuccess(false);

		}

		return response;

	}

	/// GETALLBOOKINGS

	public ResponseStructure<List<BookingResponse>> getAllBookedServices() {

		try {
System.out.println(token.access_token);
			ResponseEntity<ResponseStructure<List<BookingResponse>>> responseEntity = bookingFeign
					.getAllBookedService(token.access_token);
			System.out.println(responseEntity);

			ResponseStructure<List<BookingResponse>> res = responseEntity.getBody();

			if (res.getData() != null && !res.getData().isEmpty()) {

				return new ResponseStructure<>(

						res.getData(),

						res.getMessage(),

						res.getHttpStatus(),

						res.getStatusCode()

				);

			} else {

				return new ResponseStructure<>(

						new ArrayList<>(), // ✅ Return empty list instead of null

						"Bookings Not Found",

						res.getHttpStatus() != null ? res.getHttpStatus() : HttpStatus.NO_CONTENT,

						res.getStatusCode() != null ? res.getStatusCode() : HttpStatus.NO_CONTENT.value()

				);

			}

		} catch (FeignException e) {

			HttpStatus fallbackStatus = HttpStatus.resolve(e.status());

			if (fallbackStatus == null) {

				fallbackStatus = HttpStatus.INTERNAL_SERVER_ERROR;

			}

			return new ResponseStructure<>(

					new ArrayList<>(), // ✅ Even in error case, return empty list

					ExtractFeignMessage.clearMessage(e),

					fallbackStatus,

					fallbackStatus.value()

			);

		}

	}

	// DELETEBOOKINGBYID

	public Response deleteBookedService(String id) {

		Response response = new Response();

		try {

			ResponseEntity<ResponseStructure<BookingResponse>> res = bookingFeign.deleteBookedService(token.access_token,id);

			Object bookingResponse = res.getBody();

			if (bookingResponse != null) {

				response.setData(res.getBody());

				response.setStatus(res.getBody().getStatusCode());

				return response;

			}

			else {

				response.setStatus(404);

				response.setMessage("Unable To Delete Bookedservice");

				response.setSuccess(false);

				return response;

			}

		} catch (FeignException e) {

			response.setStatus(e.status());

			response.setMessage(ExtractFeignMessage.clearMessage(e));

			response.setSuccess(false);

			return response;

		}

	}

	// GETBOOKSERVICEBYDOCTORID

	public Response getBookingByDoctorId(String doctorId) {

		Response response = new Response();

		try {

			ResponseEntity<ResponseStructure<List<BookingResponse>>> res = bookingFeign.getBookingByDoctorId(token.access_token,doctorId);

			if (res.getBody() != null) {

				response.setData(res.getBody());

				response.setStatus(res.getBody().getStatusCode());

			} else {

				response.setStatus(200);

				response.setMessage("No Bookedservices Found For This DoctorId");

				response.setSuccess(true);

			}

		} catch (FeignException e) {

			response.setStatus(e.status());

			response.setMessage(ExtractFeignMessage.clearMessage(e));

			response.setSuccess(false);

			return response;

		}

		return response;

	}

	/// GETDOCTORINFO

	public Response getDoctorInfoByDoctorId(String doctorId) {

		Response response = new Response();

		try {

			ResponseEntity<Response> res = clinicAdminFeign.getDoctorById(token.access_token,doctorId);

			if (res.getBody() != null) {

				if (res.getBody().getData() != null) {

					DoctorsDTO dto = new ObjectMapper().convertValue(res.getBody().getData(), DoctorsDTO.class);

					DoctortInfo doctortInfo = new DoctortInfo();

					doctortInfo.setDoctorPicture(dto.getDoctorPicture());

					doctortInfo.setDoctorName(dto.getDoctorName());

					doctortInfo.setExperience(dto.getExperience());

					doctortInfo.setProfileDescription(dto.getProfileDescription());

					doctortInfo.setSpecialization(dto.getSpecialization());

					response.setData(doctortInfo);

					response.setStatus(200);

					response.setMessage("Doctor Details Fetched Successfully");

					response.setSuccess(true);
				}

			} else {

				response.setData(res.getBody());

				response.setStatus(res.getBody().getStatus());
			}

		} catch (FeignException e) {

			response.setStatus(e.status());

			response.setMessage(ExtractFeignMessage.clearMessage(e));

			response.setSuccess(false);
		}

		return response;

	}

	// -----------------------------GET CLINICS BUY RECOMMONDATION ==
	// TRUE---------------------------------

	@Override

	public Response getClinicsByRecommondation() {

		List<Clinic> clinics = clinicRep.findByRecommendedTrue();

		List<ClinicDTO> clinicsDTO = new ArrayList<>();

		for (Clinic clinic : clinics) {

			ClinicDTO toDto = new ClinicDTO();

			toDto.setHospitalId(clinic.getHospitalId());

			toDto.setName(clinic.getName());

			toDto.setAddress(clinic.getAddress());

			toDto.setCity(clinic.getCity());

			toDto.setContactNumber(clinic.getContactNumber());

			toDto.setHospitalOverallRating(clinic.getHospitalOverallRating());

			toDto.setOpeningTime(clinic.getOpeningTime());

			toDto.setClosingTime(clinic.getClosingTime());

			toDto.setEmailAddress(clinic.getEmailAddress());

			toDto.setWebsite(clinic.getWebsite());

			toDto.setLicenseNumber(clinic.getLicenseNumber());

			toDto.setIssuingAuthority(clinic.getIssuingAuthority());

			// Hospital Logo

			toDto.setHospitalLogo(

					clinic.getHospitalLogo() != null ? Base64.getEncoder().encodeToString(clinic.getHospitalLogo())

							: "");

			// Hospital Documents — single binary

			toDto.setHospitalDocuments(

					clinic.getHospitalDocuments() != null

							? Base64.getEncoder().encodeToString(clinic.getHospitalDocuments())

							: ""

			);

			toDto.setRecommended(clinic.isRecommended());

			clinicsDTO.add(toDto);

		}

		Response response = new Response();

		response.setSuccess(true);

		response.setData(clinicsDTO);

		response.setStatus(200);

		response.setMessage("Clinics Retrive successfully");

		return response;

	}

//	---------------------------get All Clincs first recommonded then another clincs----------------------------------
	@Override
	public Response getAllRecommendClinicThenAnotherClincs() {
		Response response = new Response();
		try {
			List<Clinic> clinics = clinicRep.findAllByOrderByRecommendedDescNameAsc();

			List<ClinicDTO> dtoList = clinics.stream().map(clinic -> {
				ClinicDTO dto = new ClinicDTO();

				dto.setHospitalId(clinic.getHospitalId());
				dto.setName(clinic.getName());
				dto.setAddress(clinic.getAddress());
				dto.setCity(clinic.getCity());
				dto.setHospitalOverallRating(clinic.getHospitalOverallRating());
				dto.setContactNumber(clinic.getContactNumber());
				dto.setOpeningTime(clinic.getOpeningTime());
				dto.setClosingTime(clinic.getClosingTime());

				// Convert byte[] → Base64
				dto.setHospitalLogo(
						clinic.getHospitalLogo() != null ? Base64.getEncoder().encodeToString(clinic.getHospitalLogo())
								: null);
				dto.setEmailAddress(clinic.getEmailAddress());
				dto.setWebsite(clinic.getWebsite());
				dto.setLicenseNumber(clinic.getLicenseNumber());
				dto.setIssuingAuthority(clinic.getIssuingAuthority());

				dto.setContractorDocuments(clinic.getContractorDocuments() != null
						? Base64.getEncoder().encodeToString(clinic.getContractorDocuments())
						: null);
				dto.setHospitalDocuments(clinic.getHospitalDocuments() != null
						? Base64.getEncoder().encodeToString(clinic.getHospitalDocuments())
						: null);

				dto.setRecommended(clinic.isRecommended());
				dto.setClinicalEstablishmentCertificate(clinic.getClinicalEstablishmentCertificate() != null
						? Base64.getEncoder().encodeToString(clinic.getClinicalEstablishmentCertificate())
						: null);
				dto.setBusinessRegistrationCertificate(clinic.getBusinessRegistrationCertificate() != null
						? Base64.getEncoder().encodeToString(clinic.getBusinessRegistrationCertificate())
						: null);

				dto.setClinicType(clinic.getClinicType());
				dto.setMedicinesSoldOnSite(clinic.getMedicinesSoldOnSite());
				dto.setDrugLicenseCertificate(clinic.getDrugLicenseCertificate() != null
						? Base64.getEncoder().encodeToString(clinic.getDrugLicenseCertificate())
						: null);
				dto.setDrugLicenseFormType(clinic.getDrugLicenseFormType() != null
						? Base64.getEncoder().encodeToString(clinic.getDrugLicenseFormType())
						: null);

				dto.setHasPharmacist(clinic.getHasPharmacist());
				dto.setPharmacistCertificate(clinic.getPharmacistCertificate() != null
						? Base64.getEncoder().encodeToString(clinic.getPharmacistCertificate())
						: null);

				dto.setBiomedicalWasteManagementAuth(clinic.getBiomedicalWasteManagementAuth() != null
						? Base64.getEncoder().encodeToString(clinic.getBiomedicalWasteManagementAuth())
						: null);
				dto.setTradeLicense(
						clinic.getTradeLicense() != null ? Base64.getEncoder().encodeToString(clinic.getTradeLicense())
								: null);
				dto.setFireSafetyCertificate(clinic.getFireSafetyCertificate() != null
						? Base64.getEncoder().encodeToString(clinic.getFireSafetyCertificate())
						: null);
				dto.setProfessionalIndemnityInsurance(clinic.getProfessionalIndemnityInsurance() != null
						? Base64.getEncoder().encodeToString(clinic.getProfessionalIndemnityInsurance())
						: null);
				dto.setGstRegistrationCertificate(clinic.getGstRegistrationCertificate() != null
						? Base64.getEncoder().encodeToString(clinic.getGstRegistrationCertificate())
						: null);

				dto.setConsultationExpiration(clinic.getConsultationExpiration());
				dto.setSubscription(clinic.getSubscription());

				// Convert List<byte[]> → List<String>
				dto.setOthers(clinic.getOthers() != null ? clinic.getOthers().stream()
						.map(b -> Base64.getEncoder().encodeToString(b)).collect(Collectors.toList()) : null);

				dto.setFreeFollowUps(clinic.getFreeFollowUps());
				dto.setLatitude(clinic.getLatitude());
				dto.setLongitude(clinic.getLongitude());
				dto.setNabhScore(clinic.getNabhScore());				
				dto.setWalkthrough(clinic.getWalkthrough());
				dto.setInstagramHandle(clinic.getInstagramHandle());
				dto.setTwitterHandle(clinic.getTwitterHandle());
				dto.setFacebookHandle(clinic.getFacebookHandle());

				return dto;
			}).collect(Collectors.toList());

			response.setSuccess(true);
			response.setData(dtoList);
			response.setMessage("Clinics fetched successfully (Recommended first).");
			response.setStatus(200);

		} catch (Exception e) {
			response.setSuccess(false);
			response.setMessage("Error occurred while fetching clinics: " + e.getMessage());
			response.setStatus(500);
		}
		return response;
	}
	
	
	
///PROCEDURE CRUD
  	
	@Override
	public ResponseEntity<ResponseStructure<SubServicesDto>> addSubServiceDetails(String subServiceId, SubServicesDto dto) {
		try {
			ResponseEntity<ResponseStructure<SubServicesDto>> response = cssFeign.addService(token.access_token, subServiceId, dto);
			return ResponseEntity.status(response.getBody().getStatusCode()).body(response.getBody());

		} catch (FeignClientException ex) {
			return buildErrorResponse(ex.getMessage(), ex.status());
		} catch (FeignException e) {
			return buildErrorResponse(ExtractFeignMessage.clearMessage(e), HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}

	@Override
	public ResponseEntity<ResponseStructure<List<SubServicesDto>>> getSubServiceDetailsByIdCategory(String categoryId) {
		try {
			ResponseEntity<ResponseStructure<List<SubServicesDto>>> response = cssFeign
					.getSubServiceByIdCategory(token.access_token,categoryId);
			return ResponseEntity.status(response.getBody().getStatusCode()).body(response.getBody());

		} catch (FeignException e) {
			return buildErrorResponseList(ExtractFeignMessage.clearMessage(e), HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}

	@Override
	public ResponseEntity<ResponseStructure<List<SubServicesDto>>> getSubServicesDeatailsByServiceId(String serviceId) {
		try {
			ResponseEntity<ResponseStructure<List<SubServicesDto>>> response = cssFeign
					.getSubServicesByServiceId(token.access_token,serviceId);
			return ResponseEntity.status(response.getBody().getStatusCode()).body(response.getBody());

		} catch (FeignException ex) {
			return buildErrorResponseList(ExtractFeignMessage.clearMessage(ex), ex.status());}
		
	}

	@Override
	public ResponseEntity<ResponseStructure<SubServicesDto>> getSubServiceDetailsByServiceId(String subServiceId) {

		try {
			ResponseEntity<ResponseStructure<SubServicesDto>> response = cssFeign
					.retrieveSubServicesBySubServiceId(token.access_token,subServiceId);
			return ResponseEntity.status(response.getBody().getStatusCode()).body(response.getBody());}

		catch (FeignException e) {
			return buildErrorResponse(ExtractFeignMessage.clearMessage(e), HttpStatus.INTERNAL_SERVER_ERROR.value());
		}

	}

	@Override
	public ResponseEntity<ResponseStructure<SubServicesDto>> deleteSubServiceDetails(String hospitalId, String subServiceId) {
		try {
			ResponseEntity<ResponseStructure<SubServicesDto>> response = cssFeign.deleteSubService(token.access_token,hospitalId,
					subServiceId);
			return ResponseEntity.status(response.getBody().getStatusCode()).body(response.getBody());}

		catch (FeignException e) {
			return buildErrorResponse(ExtractFeignMessage.clearMessage(e), HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}

	@Override
	public ResponseEntity<ResponseStructure<SubServicesDto>> updateBySubServiceDetalsById(String hospitalId, String serviceId,
			SubServicesDto domainServices) {
		try {
			ResponseEntity<ResponseStructure<SubServicesDto>> response = cssFeign.updateBySubServiceId(token.access_token,hospitalId,
					serviceId, domainServices);
			return ResponseEntity.status(response.getBody().getStatusCode()).body(response.getBody());

		}catch (FeignException e) {
			return buildErrorResponse(ExtractFeignMessage.clearMessage(e), HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}

	@Override
	public ResponseEntity<ResponseStructure<SubServicesDto>> getSubServiceDetalilsByServiceId(String hospitalId,
			String subServiceId) {
		try {
			ResponseEntity<ResponseStructure<SubServicesDto>> response = cssFeign
					.getSubServiceBySubServiceId(token.access_token,hospitalId, subServiceId);

			return ResponseEntity.status(HttpStatus.OK).body(response.getBody());

		} catch (FeignException e) {
			return buildErrorResponse(ExtractFeignMessage.clearMessage(e), HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}
	
	@Override
	public ResponseEntity<ResponseStructure<List<SubServicesDto>>> getSubServiceDetailsByHospitalId(String hospitalId) {
	    try {
	        ResponseEntity<ResponseStructure<List<SubServicesDto>>> response =
	        		cssFeign.getSubServiceByHospitalId(token.access_token,hospitalId); // ✅ FIXED here

	        return ResponseEntity.status(HttpStatus.OK).body(response.getBody());

	    } catch (FeignException e) {
	        return buildErrorResponseList(ExtractFeignMessage.clearMessage(e), HttpStatus.INTERNAL_SERVER_ERROR.value());
	    }
	}


	@Override
	public ResponseEntity<ResponseStructure<List<SubServicesDto>>> getAllSubServicesDetails() {
		try {
			ResponseEntity<ResponseStructure<List<SubServicesDto>>> response = cssFeign.getAllSubServices(token.access_token);
			return ResponseEntity.status(response.getBody().getStatusCode()).body(response.getBody());

		} catch (FeignException e) {
			return buildErrorResponseList(ExtractFeignMessage.clearMessage(e), HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}

	// === Helper methods ===

	private ResponseEntity<ResponseStructure<SubServicesDto>> buildErrorResponse(String message, int statusCode) {
		ResponseStructure<SubServicesDto> errorResponse = ResponseStructure.<SubServicesDto>builder().data(null)
				.message(extractCleanMessage(message)).httpStatus(HttpStatus.valueOf(statusCode)).statusCode(statusCode)
				.build();
		return ResponseEntity.status(statusCode).body(errorResponse);
	}

	private ResponseEntity<ResponseStructure<List<SubServicesDto>>> buildErrorResponseList(String message,
			int statusCode) {
		ResponseStructure<List<SubServicesDto>> errorResponse = ResponseStructure.<List<SubServicesDto>>builder()
				.data(null) // <-- changed from null to empty list
				.message(extractCleanMessage(message)).httpStatus(HttpStatus.valueOf(statusCode)).statusCode(statusCode)
				.build();
		return ResponseEntity.status(statusCode).body(errorResponse);
	}

	private String extractCleanMessage(String rawMessage) {
		// Try to extract the "message" value from JSON string if included
		try {
			int msgStart = rawMessage.indexOf("\"message\":\"");
			if (msgStart != -1) {
				int start = msgStart + 10;
				int end = rawMessage.indexOf("\"", start);
				return rawMessage.substring(start, end);
			}
		} catch (Exception ignored) {
		}
		return rawMessage;
	}

}
