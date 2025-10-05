package com.AdminService.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.AdminService.dto.BookingResponse;
import com.AdminService.dto.CategoryDto;
import com.AdminService.dto.ClinicDTO;
import com.AdminService.dto.CustomerDTO;
import com.AdminService.dto.ServicesDto;
import com.AdminService.dto.SubServicesDto;
import com.AdminService.dto.SubServicesInfoDto;
import com.AdminService.dto.UpdateClinicCredentials;
import com.AdminService.util.ExtractFeignMessage;
import com.AdminService.util.Response;
import com.AdminService.util.ResponseStructure;

import feign.FeignException;
import feign.FeignException.FeignClientException;



public interface AdminService {

	//CLINIC MANAGEMENT
	public Response createClinic(ClinicDTO clinic);
	Response getClinicById(String clinicId);
	public Response getAllClinics();
	Response updateClinic(String clinicId, ClinicDTO clinic);
	Response deleteClinic(String clinicId);

//CLINIC CREDENTIALS
public Response getClinicCredentials(String userName);

public Response updateClinicCredentials(UpdateClinicCredentials credentials,String userName) ;

public Response deleteClinicCredentials(String userName );

//category
public ResponseEntity<ResponseStructure<CategoryDto>> addNewCategory(CategoryDto dto);

public Response getAllCategory();

public Response deleteCategoryById(
		 String categoryId);

public Response updateCategory(String categoryId,CategoryDto updatedCategory);
public Response getCategoryById(String CategoryId);

//SERVICE MANAGEMENT
public Response addService( ServicesDto dto);
public Response getServiceById( String categoryId);
public Response getServiceByServiceId( String serviceId);
public Response deleteService( String serviceId);
public Response updateByServiceId( String serviceId,
	 ServicesDto domainServices);
public Response getAllServices();

//SUBSERVICE MANAGEMENT
public  Response addSubService( SubServicesInfoDto dto);
public Response getSubServiceByIdCategory(String categoryId);
public Response getSubServicesByServiceId(String serviceId);
public Response getSubServiceBySubServiceId(String subServiceId);
public Response deleteSubService(String subServiceId);
public Response updateBySubServiceId(String subServiceId, SubServicesInfoDto domainServices);
public Response getAllSubServices();

//CUSTOMER MANAGEMENT
public Response saveCustomerBasicDetails(CustomerDTO customerDTO );
public ResponseEntity<?> getCustomerByUsernameMobileEmail(String input);
public Response getCustomerBasicDetails(String mobileNumber );
public Response getAllCustomers();
public Response updateCustomerBasicDetails(CustomerDTO customerDTO,String mobileNumber );
public Response deleteCustomerBasicDetails(String mobileNumber);

//SUBSERVICES
public Response getAllSubServicesFromClincAdmin();

//BOOKINGS

public ResponseStructure<List<BookingResponse>> getAllBookedServices();
public Response deleteBookedService(String id);
public Response getBookingByDoctorId(String doctorId);

//DOCTORS
public Response getDoctorInfoByDoctorId(String doctorId);

public Response getClinicsByRecommondation();

Response getAllRecommendClinicThenAnotherClincs();


//SUBSERVICES DETAILS

public ResponseEntity<ResponseStructure<SubServicesDto>> addSubServiceDetails(String subServiceId, SubServicesDto dto);
	
public ResponseEntity<ResponseStructure<List<SubServicesDto>>> getSubServiceDetailsByIdCategory(String categoryId);
	
public ResponseEntity<ResponseStructure<List<SubServicesDto>>> getSubServicesDeatailsByServiceId(String serviceId);
	
public ResponseEntity<ResponseStructure<SubServicesDto>> getSubServiceDetailsByServiceId(String subServiceId);

public ResponseEntity<ResponseStructure<SubServicesDto>> deleteSubServiceDetails(String hospitalId, String subServiceId);
	
public ResponseEntity<ResponseStructure<SubServicesDto>> updateBySubServiceDetalsById(String hospitalId, String serviceId,
		SubServicesDto domainServices);
	
public ResponseEntity<ResponseStructure<SubServicesDto>> getSubServiceDetalilsByServiceId(String hospitalId,
		String subServiceId);
	
public ResponseEntity<ResponseStructure<List<SubServicesDto>>> getSubServiceDetailsByHospitalId(String hospitalId);
    
public ResponseEntity<ResponseStructure<List<SubServicesDto>>> getAllSubServicesDetails();
	
}
