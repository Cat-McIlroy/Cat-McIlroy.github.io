package com.cmcilroy.medicines_shortages_assistant.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.cmcilroy.medicines_shortages_assistant.domain.entities.DrugEntity;
import com.cmcilroy.medicines_shortages_assistant.domain.entities.PharmacyDrugAvailabilityEntity;
import com.cmcilroy.medicines_shortages_assistant.domain.entities.PharmacyEntity;

public interface PharmacyDrugAvailabilityService {

    PharmacyDrugAvailabilityEntity savePharmacyDrugAvailability(PharmacyDrugAvailabilityEntity pharmacyDrugAvailability);

    boolean existsByPharmacyAndDrug(PharmacyEntity pharmacy, DrugEntity drug);

    void delete(Long id);

    Page<PharmacyDrugAvailabilityEntity> findAllByLicenceNo(String licenceNo, Pageable pageable);

    List<PharmacyDrugAvailabilityEntity> findAllByPharmacy(PharmacyEntity pharmacy);

    void deleteAllByPharmacy(PharmacyEntity pharmacy);

}