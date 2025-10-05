package com.AdminService.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.AdminService.dto.BookingResponse;
import com.AdminService.dto.CategoryDto;
import com.AdminService.dto.ClinicDTO;
import com.AdminService.dto.CustomerDTO;
import com.AdminService.dto.ServicesDto;
import com.AdminService.dto.SubServicesDto;
import com.AdminService.dto.SubServicesInfoDto;
import com.AdminService.dto.UpdateClinicCredentials;
import com.AdminService.service.AdminService;
import com.AdminService.util.Response;
import com.AdminService.util.ResponseStructure;
import jakarta.validation.Valid;


@RestController

@RequestMapping("/admin")

//@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})

public class AdminController {
	
	@Autowired
	private AdminService serviceImpl;
	

	@PostMapping("/CreateClinic")

	public ResponseEntity<?> clinicRegistration(@RequestBody @Valid ClinicDTO clinic) {

	    Response response = serviceImpl.createClinic(clinic);

	    if (response != null && response.getStatus() != 0) {

	        return ResponseEntity.status(response.getStatus()).body(response);

	    } else {

	        return null;

	    }

	}

    // Get Clinic by ID

    @GetMapping("/getClinicById/{clinicId}")

    public Response getClinicById(@PathVariable String clinicId) {

    	Response response = serviceImpl.getClinicById(clinicId);

    	return response;

    }

  

    //GET ALL CUSTOMERS

    @GetMapping("/getAllClinics")

    public ResponseEntity<?> getAllClinics(){

    	Response response =   serviceImpl.getAllClinics();

    	if(response != null && response.getStatus() != 0) {

			 return ResponseEntity.status(response.getStatus()).body(response);

		 }else {

				return null;}

    }


    // Update Clinic

    @PutMapping("/updateClinic/{clinicId}")

    public Response updateClinic(@PathVariable String clinicId, @RequestBody ClinicDTO clinic) {

    	Response response = serviceImpl.updateClinic(clinicId, clinic);

    	return response;

    }


    // Delete Clinic

    @DeleteMapping("/deleteClinic/{clinicId}")

    public ResponseEntity<?> deleteClinic(@PathVariable String clinicId) {

    	Response response = serviceImpl.deleteClinic(clinicId);

    	if(response != null && response.getStatus() != 0) {

			 return ResponseEntity.status(response.getStatus()).body(response);

		 }else {

				return null;}

    }
 
   /// CLINIC CREDENTIALS

    // Get clinic credentials by hospitalId

    @GetMapping("/getClinicCredentials/{userName}")

    public Response getClinicCredentials(@PathVariable String userName) {

    	Response response = serviceImpl.getClinicCredentials(userName);

    	return response;
  	

    }

    // Update clinic credentials

    @PutMapping("/updateClinicCredentials/{userName}")

    public Response updateClinicCredentials(@RequestBody UpdateClinicCredentials updatedCredentials

    		,@PathVariable String userName) {

    	Response response = serviceImpl.updateClinicCredentials(updatedCredentials, userName);

    	return response;

    }

    // Delete clinic credentials

    @DeleteMapping("/deleteClinicCredentials/{userName}")

    public ResponseEntity<?> deleteClinicCredentials(@PathVariable String userName) {

    	Response response = serviceImpl.deleteClinicCredentials(userName);

    	if(response != null && response.getStatus() != 0) {

			 return ResponseEntity.status(response.getStatus()).body(response);

		 }else {

				return null;}

    }
  
    /// Category Management
    @PostMapping("/addCategory")

	public ResponseEntity<?> addNewCategory(@RequestBody CategoryDto dto) {

    	return serviceImpl.addNewCategory(dto);

    }

    
    @GetMapping("/getCategories")

	public ResponseEntity<?> getAllCategory() {

    	Response response = serviceImpl.getAllCategory();

    	if(response != null && response.getData() == null) {

			 return ResponseEntity.status(response.getStatus()).body(response);

		 }else if(response != null && response.getData() != null ) {

			 return ResponseEntity.status(response.getStatus()).body(response.getData());

		 }

		else {

				return null;}

    

    }

    
    @GetMapping("/getcategoryById/{categoryId}")

    public ResponseEntity<?> getCategoryById(@PathVariable (value= "categoryId") String categoryId){

    	Response response = serviceImpl.getCategoryById(categoryId);

    	if(response != null && response.getData() == null) {

			 return ResponseEntity.status(response.getStatus()).body(response);

		 }else if(response != null && response.getData() != null ) {

			 return ResponseEntity.status(response.getStatus()).body(response.getData());

		 }

		else {

				return null;}
 
    }

  
    @DeleteMapping("/deleteCategory/{categoryId}")

	public ResponseEntity<?> deleteCategoryById(

			@PathVariable(value = "categoryId") String categoryId){

    	Response response = serviceImpl.deleteCategoryById(categoryId); 

    	if(response != null && response.getData() == null) {

			 return ResponseEntity.status(response.getStatus()).body(response);

		 }else if(response != null && response.getData() != null ) {

			 return ResponseEntity.status(response.getStatus()).body(response.getData());

		 }

		else {

				return null;}

    }

   
@PutMapping("updateCategory/{categoryId}")

public ResponseEntity<?> updateCategory(@PathVariable String categoryId,

		@RequestBody CategoryDto updatedCategory){

	Response response = serviceImpl.updateCategory(categoryId, updatedCategory);

	if(response != null && response.getData() == null) {

		 return ResponseEntity.status(response.getStatus()).body(response);

	 }else if(response != null && response.getData() != null ) {

		 return ResponseEntity.status(response.getStatus()).body(response.getData());

	 }

	else {

			return null;}

}

//SERVICE MANAGEMENT
@PostMapping("/addService")

public ResponseEntity<?> addService(@RequestBody ServicesDto dto) {

	Response response = serviceImpl.addService(dto);

	if(response != null && response.getData() == null) {

		 return ResponseEntity.status(response.getStatus()).body(response);

	 }else if(response != null && response.getData() != null ) {

		 return ResponseEntity.status(response.getStatus()).body(response.getData());

	 }

	else {

			return null;}

}


@GetMapping("/getServiceById/{categoryId}")

public ResponseEntity<Object> getServiceById(@PathVariable String categoryId) {

	Response response = serviceImpl.getServiceById(categoryId);

	if(response != null && response.getData() == null) {

		 return ResponseEntity.status(response.getStatus()).body(response);

	 }else if(response != null && response.getData() != null ) {

		 return ResponseEntity.status(response.getStatus()).body(response.getData());

	 }

	else {

			return null;}

}





@GetMapping("/getServiceByServiceId/{serviceId}")

public ResponseEntity<?> getServiceByServiceId(@PathVariable String serviceId) {

	Response response = serviceImpl.getServiceByServiceId(serviceId);

	if(response != null && response.getData() == null) {

		 return ResponseEntity.status(response.getStatus()).body(response);

	 }else if(response != null && response.getData() != null ) {

		 return ResponseEntity.status(response.getStatus()).body(response.getData());

	 }

	else {

			return null;}

}


@PutMapping("/updateByServiceId/{serviceId}")

public ResponseEntity<?> updateByServiceId(@PathVariable String serviceId,

		@RequestBody ServicesDto domainServices){

	Response response = serviceImpl.updateByServiceId(serviceId, domainServices);

	if(response != null && response.getData() == null) {

		 return ResponseEntity.status(response.getStatus()).body(response);

	 }else if(response != null && response.getData() != null ) {

		 return ResponseEntity.status(response.getStatus()).body(response.getData());

	 }

	else {

			return null;}

}


@GetMapping("/getAllServices")

public ResponseEntity<?> getAllServices(){

	Response response = serviceImpl.getAllServices();

	if(response != null && response.getData() == null) {

		 return ResponseEntity.status(response.getStatus()).body(response);

	 }else if(response != null && response.getData() != null ) {

		 return ResponseEntity.status(response.getStatus()).body(response.getData());

	 }

	else {

			return null;}

}

@DeleteMapping("/deleteService/{serviceId}")

public ResponseEntity<?> deleteService(@PathVariable String serviceId){

	Response response = serviceImpl.deleteService(serviceId);

	if(response != null && response.getData() == null) {

		 return ResponseEntity.status(response.getStatus()).body(response);

	 }else if(response != null && response.getData() != null ) {

		 return ResponseEntity.status(response.getStatus()).body(response.getData());

	 }

	else {

			return null;}

}



//SUBSERVICE MANAGEMENT
@PostMapping("/addSubService")

public ResponseEntity<?> addSubService(@RequestBody SubServicesInfoDto dto) {

	Response response = serviceImpl.addSubService(dto);

	 if(response != null && response.getStatus() != 0) {

		 return ResponseEntity.status(response.getStatus()).body(response);

	 }else {

			return null;}

}



@GetMapping("/getSubServiceByIdCategory/{categoryId}")

public ResponseEntity<?> getSubServiceByIdCategory(@PathVariable String categoryId) {

	Response response = serviceImpl.getSubServiceByIdCategory(categoryId);

	 if(response != null && response.getStatus() != 0) {

		 return ResponseEntity.status(response.getStatus()).body(response);

	 }else {

			return null;}

    }



@GetMapping("/getSubServicesByServiceId/{serviceId}")

public ResponseEntity<?> getSubServicesByServiceId(@PathVariable String serviceId){

	Response response = serviceImpl.getSubServicesByServiceId(serviceId);

	 if(response != null && response.getStatus() != 0) {

		 return ResponseEntity.status(response.getStatus()).body(response);

	 }else {

			return null;}

    }





@GetMapping("/getSubServiceBySubServiceId/{subServiceId}")

public ResponseEntity<?> getSubServiceBySubServiceId(@PathVariable String subServiceId){

	Response response = serviceImpl.getSubServiceBySubServiceId(subServiceId);

	 if(response != null && response.getStatus() != 0) {

		 return ResponseEntity.status(response.getStatus()).body(response);

	 }else {

			return null;}

    }



@GetMapping("/getAllSubServices")

public ResponseEntity<?> getAllSubServices(){

	Response response = serviceImpl.getAllSubServices();

	 if(response != null && response.getStatus() != 0) {

		 return ResponseEntity.status(response.getStatus()).body(response);

	 }else {

			return null;}

    }



@PutMapping("/updateBySubServiceId/{subServiceId}")

public ResponseEntity<?> updateBySubServiceId(@PathVariable String subServiceId,

		@RequestBody SubServicesInfoDto domainServices){

	Response response = serviceImpl.updateBySubServiceId(subServiceId, domainServices);

	 if(response != null && response.getStatus() != 0) {

		 return ResponseEntity.status(response.getStatus()).body(response);

	 }else {

			return null;}

    }





@DeleteMapping("/deleteSubService/{subServiceId}")

public ResponseEntity<?> deleteSubService(@PathVariable String subServiceId){

	Response response = serviceImpl.deleteSubService(subServiceId);

	 if(response != null && response.getStatus() != 0) {

		 return ResponseEntity.status(response.getStatus()).body(response);

	 }else {

			return null;}

    }

/// CUSTOMER MANAGEMENT

@PostMapping("/saveBasicDetails")

public ResponseEntity<Response> saveCustomerBasicDetails(@RequestBody CustomerDTO customerDTO ){

	Response response = serviceImpl.saveCustomerBasicDetails(customerDTO);

	if(response != null && response.getData() == null) {

		 return ResponseEntity.status(response.getStatus()).body(response);

	 }else if(response != null && response.getData() != null ) {

		 return ResponseEntity.status(response.getStatus()).body(response);

	 }

	else {

			return null;}

}



@GetMapping("/getBasicDetails/{mobileNumber}")

public ResponseEntity<Response> getCustomerBasicDetails(@PathVariable String mobileNumber ){

	Response response = serviceImpl.getCustomerBasicDetails(mobileNumber);

	if(response != null && response.getData() == null) {

		 return ResponseEntity.status(response.getStatus()).body(response);

	 }else if(response != null && response.getData() != null ) {

		 return ResponseEntity.status(response.getStatus()).body(response);

	 }

	else {

			return null;}

}



@GetMapping("/getAllCustomers")

public ResponseEntity<Response> getAllCustomers(){

	Response response = serviceImpl.getAllCustomers();

	if(response != null && response.getData() == null) {

		 return ResponseEntity.status(response.getStatus()).body(response);

	 }else if(response != null && response.getData() != null ) {

		 return ResponseEntity.status(response.getStatus()).body(response);

	 }

	else {

			return null;}

}



@PutMapping("/updateCustomerBasicDetails/{mobileNumber}")

public ResponseEntity<Response> updateCustomerBasicDetails(@RequestBody CustomerDTO customerDTO,

		@PathVariable String mobileNumber ){

	Response response = serviceImpl.updateCustomerBasicDetails(customerDTO, mobileNumber);

	if(response != null && response.getData() == null) {

		 return ResponseEntity.status(response.getStatus()).body(response);

	 }else if(response != null && response.getData() != null ) {

		 return ResponseEntity.status(response.getStatus()).body(response);

	 }

	else {

			return null;}

}



@DeleteMapping("/deleteCustomerBasicDetails/{mobileNumber}")

public ResponseEntity<Response> deleteCustomerBasicDetails(@PathVariable String mobileNumber ){

	Response response = serviceImpl.deleteCustomerBasicDetails(mobileNumber);

	if(response != null && response.getData() == null) {

		 return ResponseEntity.status(response.getStatus()).body(response);

	 }else if(response != null && response.getData() != null ) {

		 return ResponseEntity.status(response.getStatus()).body(response);

	 }

	else {
			return null;
			}

}



@GetMapping("/getCustomerByInput/{input}")

	public ResponseEntity<?> getCustomerByUsernameMobileEmail(@PathVariable String input){

	return serviceImpl.getCustomerByUsernameMobileEmail(input);

}



//GETALLSUBSERVICES

@GetMapping("/getAllSubservicesByClinicAdmin")

public ResponseEntity<Object> getAllSubservicesByClinicAdmin(){

	Response response = serviceImpl.getAllSubServicesFromClincAdmin();

	if(response != null && response.getData() == null) {

		 return ResponseEntity.status(response.getStatus()).body(response);

	 }else if(response != null && response.getData() != null ) {

		 return ResponseEntity.status(response.getStatus()).body(response.getData());

	 }

	else {

	     return null;}

}

///GETALLBOOKINGS

@GetMapping("/getAllBookedServices")

public ResponseEntity<ResponseStructure<List<BookingResponse>>> getAllBookedServices() {

    ResponseStructure<List<BookingResponse>> response = serviceImpl.getAllBookedServices();



    // Fallback if httpStatus is null

    HttpStatus status = response.getHttpStatus() != null ? response.getHttpStatus() : HttpStatus.INTERNAL_SERVER_ERROR;



    return ResponseEntity.status(status.value()).body(response);

}



///DELETEBOOKINGBYID



@DeleteMapping("/deleteServiceByBookingId/{id}")

public ResponseEntity<Object> deleteBookedService(@PathVariable String id){

	Response response = serviceImpl.deleteBookedService(id);

	if(response != null && response.getData() == null) {

		 return ResponseEntity.status(response.getStatus()).body(response);

	 }else if(response != null && response.getData() != null) {

		 return ResponseEntity.status(response.getStatus()).body(response.getData());}

		 else {

			 return null;

		 }

}

@GetMapping("/getBookingByDoctorId/{doctorId}")

public ResponseEntity<Object> getBookingByDoctorId(@PathVariable String doctorId){

	Response response = serviceImpl.getBookingByDoctorId(doctorId);

	if(response != null && response.getData() == null) {

		 return ResponseEntity.status(response.getStatus()).body(response);

	 }else if(response != null && response.getData() != null) {

		 return ResponseEntity.status(response.getStatus()).body(response.getData());}

		 else {

			 return null;

		 }

}



//GETDOCTORINFOBYDOCTORID

@GetMapping("/getDoctorInfoByDoctorId/{doctorId}")

public ResponseEntity<Object> getDoctorInfoByDoctorId(@PathVariable String doctorId){

	Response response = serviceImpl.getDoctorInfoByDoctorId(doctorId);

	if(response != null) {

		 return ResponseEntity.status(response.getStatus()).body(response);}

		 else {

			 return null;

		 }

	}

	@GetMapping("/clinics/recommended")

	public ResponseEntity<Response>getHospitalUsingRecommendentaion(){

		Response response = serviceImpl.getClinicsByRecommondation();

		 return ResponseEntity.status(response.getStatus()).body(response);

	}
	
	@GetMapping("/clinics/firstRecommendedTureClincs")

	public ResponseEntity<Response>firstRecommendedTureClincs(){

		Response response = serviceImpl.getAllRecommendClinicThenAnotherClincs();

		 return ResponseEntity.status(response.getStatus()).body(response);

	}
	
	///PROCDURE 
	
	@PostMapping("/addSubServiceDetails/{subServiceId}")
	public ResponseEntity<ResponseStructure<SubServicesDto>> addSubServiceDetails(@PathVariable String subServiceId,@RequestBody SubServicesDto dto){
		 return serviceImpl.addSubServiceDetails(subServiceId, dto);
	}
	@GetMapping("/getSubServiceDetailsByIdCategory/{categoryId}")
	public ResponseEntity<ResponseStructure<List<SubServicesDto>>> getSubServiceDetailsByIdCategory(@PathVariable String categoryId){
		return serviceImpl.getSubServiceDetailsByIdCategory(categoryId);
	}
	@GetMapping("/getSubServicesDeatailsByServiceId/{serviceId}")	
	public ResponseEntity<ResponseStructure<List<SubServicesDto>>> getSubServicesDeatailsByServiceId(@PathVariable String serviceId){
		return serviceImpl.getSubServicesDeatailsByServiceId(serviceId);
	}
	@GetMapping("/getSubServiceDetailsBySubServiceId/{subServiceId}")	
	public ResponseEntity<ResponseStructure<SubServicesDto>> getSubServiceDetailsBySubServiceId(@PathVariable String subServiceId){
		return serviceImpl.getSubServiceDetailsByServiceId(subServiceId); 
	}
	@DeleteMapping("/deleteSubServiceDetails/{hospitalId}/{subServiceId}")	
	public ResponseEntity<ResponseStructure<SubServicesDto>> deleteSubServiceDetails(@PathVariable String hospitalId, @PathVariable  String subServiceId){
		return serviceImpl.deleteSubServiceDetails(hospitalId, subServiceId);
	}
	@PutMapping("/updateBySubServiceDetalsById/{hospitalId}")		
	public ResponseEntity<ResponseStructure<SubServicesDto>> updateBySubServiceDetalsById(@PathVariable String hospitalId,@PathVariable  String serviceId,
			SubServicesDto domainServices){
		return serviceImpl.updateBySubServiceDetalsById(hospitalId, serviceId, domainServices);
	}
	@GetMapping("/getSubServiceDetalilsBySubServiceIdAndHospitalId/{hospitalId}/{subServiceId}")		
	public ResponseEntity<ResponseStructure<SubServicesDto>> getSubServiceDetalilsBySubServiceIdAndHospitalId(String hospitalId,
			String subServiceId){
		return serviceImpl.getSubServiceDetalilsByServiceId(hospitalId, subServiceId);
	}
	@GetMapping("/getSubServiceDetailsByHospitalId/{hospitalId}")	
	public ResponseEntity<ResponseStructure<List<SubServicesDto>>> getSubServiceDetailsByHospitalId(String hospitalId){
		return serviceImpl.getSubServiceDetailsByHospitalId(hospitalId);
	}
	@GetMapping("/getAllSubServicesDetails")	  
	public ResponseEntity<ResponseStructure<List<SubServicesDto>>> getAllSubServicesDetails(){
		return serviceImpl.getAllSubServicesDetails();
	}
		

}


