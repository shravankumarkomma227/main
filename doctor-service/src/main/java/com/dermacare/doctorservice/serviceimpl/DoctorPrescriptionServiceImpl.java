package com.dermacare.doctorservice.serviceimpl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.dermacare.doctorservice.dto.DoctorPrescriptionDTO;
import com.dermacare.doctorservice.dto.MedicineDTO;
import com.dermacare.doctorservice.dto.Response;
import com.dermacare.doctorservice.model.DoctorPrescription;
import com.dermacare.doctorservice.model.Medicine;
import com.dermacare.doctorservice.repository.DoctorPrescriptionRepository;
import com.dermacare.doctorservice.service.DoctorPrescriptionService;

@Service
public class DoctorPrescriptionServiceImpl implements DoctorPrescriptionService {

    @Autowired
    private DoctorPrescriptionRepository repository;
    @Override
    public Response createPrescription(DoctorPrescriptionDTO dto) {
        try {
            // ✅ 1. Validate input
            if (dto == null || dto.getMedicines() == null || dto.getMedicines().isEmpty()) {
                return new Response(false, null,
                        "Prescription must have at least one medicine",
                        HttpStatus.BAD_REQUEST.value(),null,null);
            }

            if (dto.getClinicId() == null || dto.getClinicId().isBlank()) {
                return new Response(false, null,
                        "Clinic ID is required",
                        HttpStatus.BAD_REQUEST.value(),null,null);
            }

            // ✅ 2. Fetch existing prescription for this clinic
            List<DoctorPrescription> prescriptions = repository.findByClinicId(dto.getClinicId());

            DoctorPrescription entity = prescriptions.isEmpty()
                    ? new DoctorPrescription()
                    : prescriptions.get(0); // only one prescription per clinic

            entity.setClinicId(dto.getClinicId());

            List<Medicine> existingMedicines = Optional.ofNullable(entity.getMedicines())
                    .orElse(new ArrayList<>());

            boolean updatedExistingMedicine = false;
            boolean addedNewMedicine = false;

            // ✅ 3. Loop through incoming medicines
            for (MedicineDTO incomingMed : dto.getMedicines()) {
                if (incomingMed == null || incomingMed.getName() == null || incomingMed.getName().isBlank()) {
                    continue;
                }

                String normalizedName = incomingMed.getName().trim().replaceAll("\\s+", " ").toLowerCase();

                Optional<Medicine> existingMedOpt = existingMedicines.stream()
                        .filter(m -> m.getName() != null &&
                                m.getName().trim().replaceAll("\\s+", " ").toLowerCase().equals(normalizedName))
                        .findFirst();

                if (existingMedOpt.isPresent()) {
                    // ✅ Update existing medicine
                    Medicine existingMed = existingMedOpt.get();
                    existingMed.setDose(incomingMed.getDose());
                    existingMed.setDuration(incomingMed.getDuration());
                    existingMed.setNote(incomingMed.getNote());
                    existingMed.setFood(incomingMed.getFood());
                    existingMed.setRemindWhen(incomingMed.getRemindWhen());
                    existingMed.setTimes(incomingMed.getTimes());

                    updatedExistingMedicine = true;
                } else {
                    // ✅ Add new medicine
                    existingMedicines.add(new Medicine(
                            UUID.randomUUID().toString(),
                            incomingMed.getName().trim(),
                            incomingMed.getDose(),
                            incomingMed.getDuration(),
                            incomingMed.getNote(),
                            incomingMed.getFood(),
                            incomingMed.getRemindWhen(),
                            incomingMed.getTimes()
                    ));
                    addedNewMedicine = true;
                }
            }

            // ✅ 4. Save back the prescription
            entity.setMedicines(existingMedicines);
            DoctorPrescription saved = repository.save(entity);

            // ✅ 5. Build response DTO
            DoctorPrescriptionDTO responseDTO = new DoctorPrescriptionDTO();
            responseDTO.setId(saved.getId());
            responseDTO.setClinicId(saved.getClinicId());
            responseDTO.setMedicines(saved.getMedicines().stream()
                    .map(m -> new MedicineDTO(
                            m.getId(), m.getName(), m.getDose(), m.getDuration(),
                            m.getNote(), m.getFood(), m.getRemindWhen(), m.getTimes()
                    ))
                    .collect(Collectors.toList())
            );

            String finalMessage;
            if (updatedExistingMedicine && addedNewMedicine) {
                finalMessage = "Prescription updated with new and existing medicines";
            } else if (updatedExistingMedicine) {
                finalMessage = "Existing medicines updated successfully";
            } else if (addedNewMedicine) {
                finalMessage = "Prescription created successfully";
            } else {
                finalMessage = "No changes were made to the prescription";
            }

            return new Response(true, responseDTO, finalMessage, HttpStatus.CREATED.value(),null,null);

        } catch (Exception e) {
            return new Response(false, null,
                    "Failed to create/update prescription: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),null,null);
        }
    }


    // Helper method to update medicine details
//    private void updateMedicine(Medicine existing, MedicineDTO incoming) {
//        existing.setName(incoming.getName().trim());
//        existing.setDose(incoming.getDose());
//        existing.setDuration(incoming.getDuration());
//        existing.setNote(incoming.getNote());
//        existing.setFood(incoming.getFood());
//        existing.setRemindWhen(incoming.getRemindWhen());
//        existing.setTimes(incoming.getTimes());
//    }

    @Override
    public Response getAllPrescriptions() {
        try {
            List<DoctorPrescriptionDTO> dtos = repository.findAll().stream().map(p -> {
                DoctorPrescriptionDTO dto = new DoctorPrescriptionDTO();
                dto.setId(p.getId());
                List<MedicineDTO> meds = Optional.ofNullable(p.getMedicines()).orElse(List.of()).stream().map(m -> new MedicineDTO(
                    m.getId(), m.getName(), m.getDose(), m.getDuration(), m.getNote(), m.getFood(), m.getRemindWhen(), m.getTimes()
                )).collect(Collectors.toList());
                dto.setMedicines(meds);
                return dto;
            }).collect(Collectors.toList());

            return new Response(true, dtos, "Fetched all prescriptions successfully", HttpStatus.OK.value(),null,null);

        } catch (Exception e) {
            return new Response(false, null, "Error fetching prescriptions: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(),null,null);
        }
    }

    @Override
    public Response getPrescriptionById(String id) {
        try {
            Optional<DoctorPrescription> optional = repository.findById(id);
            if (optional.isPresent()) {
                DoctorPrescription p = optional.get();
                DoctorPrescriptionDTO dto = new DoctorPrescriptionDTO();
                dto.setId(p.getId());
                List<MedicineDTO> meds = Optional.ofNullable(p.getMedicines()).orElse(List.of()).stream().map(m -> new MedicineDTO(
                    m.getId(), m.getName(), m.getDose(), m.getDuration(), m.getNote(), m.getFood(), m.getRemindWhen(), m.getTimes()
                )).collect(Collectors.toList());
                dto.setMedicines(meds);

                return new Response(true, dto, "Prescription found", HttpStatus.OK.value(),null,null);
            } else {
                return new Response(false, null, "Prescription not found", HttpStatus.NOT_FOUND.value(),null,null);
            }
        } catch (Exception e) {
            return new Response(false, null, "Error retrieving prescription: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(),null,null);
        }
    }

    @Override
    public Response getMedicineById(String medicineId) {
        try {
            List<Medicine> matches = repository.findAll().stream()
                .flatMap(p -> Optional.ofNullable(p.getMedicines()).orElse(List.of()).stream())
                .filter(m -> m.getId() != null && m.getId().equals(medicineId))
                .distinct()
                .collect(Collectors.toList());

            if (!matches.isEmpty()) {
                List<MedicineDTO> dtos = matches.stream().map(m -> new MedicineDTO(
                    m.getId(), m.getName(), m.getDose(), m.getDuration(), m.getNote(), m.getFood(), m.getRemindWhen(), m.getTimes()
                )).collect(Collectors.toList());

                return new Response(true, dtos, "Medicine found", HttpStatus.OK.value(),null,null);
            } else {
                return new Response(false, null, "No medicine found with given ID", HttpStatus.NOT_FOUND.value(),null,null);
            }
        } catch (Exception e) {
            return new Response(false, null, "Error while fetching medicine: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(),null,null);
        }
    }

    @Override
    public Response deletePrescription(String id) {
        try {
            if (repository.existsById(id)) {
                repository.deleteById(id);
                return new Response(true, null, "Prescription deleted successfully", HttpStatus.OK.value(),null,null);
            } else {
                return new Response(false, null, "Prescription not found", HttpStatus.NOT_FOUND.value(),null,null);
            }
        } catch (Exception e) {
            return new Response(false, null, "Failed to delete: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(),null,null);
        }
    }



    @Override
    public Response searchMedicinesByName(String keyword) {
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                return new Response(false, null,
                        "Keyword must not be empty",
                        HttpStatus.BAD_REQUEST.value(),null,null);
            }

            String normalizedKeyword = keyword.trim().replaceAll("\\s+", " ").toLowerCase();

            // Flatten all medicines, filter exact match
            Optional<Medicine> latestMedicine = repository.findAll().stream()
                .flatMap(prescription -> Optional.ofNullable(prescription.getMedicines())
                                                 .orElse(List.of())
                                                 .stream())
                .filter(medicine -> medicine.getName() != null &&
                    medicine.getName().trim().replaceAll("\\s+", " ").toLowerCase().equals(normalizedKeyword))
                // pick latest one by ID (assuming higher ID = newer)
                .max(Comparator.comparing(Medicine::getId));

            if (latestMedicine.isEmpty()) {
                return new Response(false, null,
                        "No medicine found with exact name: " + keyword,
                        HttpStatus.NOT_FOUND.value(),null,null);
            }

            Medicine m = latestMedicine.get();
            MedicineDTO dto = new MedicineDTO(
                    m.getId(),
                    m.getName(),
                    m.getDose(),
                    m.getDuration(),
                    m.getNote(),
                    m.getFood(),
                    m.getRemindWhen(),
                    m.getTimes()
            );

            return new Response(true, List.of(dto), "Medicine found", HttpStatus.OK.value(),null,null);

        } catch (Exception e) {
            return new Response(false, null,
                    "Error searching medicine: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),null,null);
        }
    }
    
    @Override
    public Response deleteMedicineById(String medicineId) {
        try {
            List<DoctorPrescription> allPrescriptions = repository.findAll();
            boolean medicineDeleted = false;

            for (DoctorPrescription prescription : allPrescriptions) {
                List<Medicine> medicines = prescription.getMedicines() != null
                    ? new ArrayList<>(prescription.getMedicines())
                    : new ArrayList<>();

                boolean removed = medicines.removeIf(med -> med.getId() != null && med.getId().equals(medicineId));

                if (removed) {
                    prescription.setMedicines(medicines);
                    repository.save(prescription);
                    medicineDeleted = true;
                }
            }

            if (medicineDeleted) {
                return new Response(true, null, "Medicine deleted successfully", HttpStatus.OK.value(),null,null);
            } else {
                return new Response(false, null, "Medicine not found", HttpStatus.NOT_FOUND.value(),null,null);
            }

        } catch (Exception e) {
            return new Response(false, null, "Error deleting medicine: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(),null,null);
        }
    }
    @Override
    public Response getPrescriptionsByClinicId(String clinicId) {
        try {
            List<DoctorPrescription> prescriptions = repository.findByClinicId(clinicId);
            List<DoctorPrescriptionDTO> dtos = prescriptions.stream().map(p -> {
                DoctorPrescriptionDTO dto = new DoctorPrescriptionDTO();
                dto.setId(p.getId());
                dto.setClinicId(p.getClinicId());  

                List<MedicineDTO> meds = Optional.ofNullable(p.getMedicines()).orElse(List.of()).stream()
                    .map(m -> new MedicineDTO(
                        m.getId(), m.getName(), m.getDose(), m.getDuration(), m.getNote(), m.getFood(), m.getRemindWhen(), m.getTimes()
                    )).collect(Collectors.toList());
                dto.setMedicines(meds);
                return dto;
            }).collect(Collectors.toList());

            return new Response(true, dtos, "Prescriptions fetched successfully for clinicId: " + clinicId, HttpStatus.OK.value(),null,null);

        } catch (Exception e) {
            return new Response(false, null, "Error fetching prescriptions by clinicId: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(),null,null);
        }
    }

}
