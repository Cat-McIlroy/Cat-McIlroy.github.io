package com.cmcilroy.medicines_shortages_assistant.services;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.cmcilroy.medicines_shortages_assistant.domain.entities.DrugEntity;
import com.cmcilroy.medicines_shortages_assistant.domain.entities.PharmacyDrugAvailabilityEntity;
import com.cmcilroy.medicines_shortages_assistant.domain.entities.PharmacyEntity;

public interface PharmacyDrugAvailabilityService {

    PharmacyDrugAvailabilityEntity savePharmacyDrugAvailability(PharmacyDrugAvailabilityEntity pharmacyDrugAvailability);

    Page<PharmacyDrugAvailabilityEntity> findAll(Pageable pageable);

    Optional<PharmacyDrugAvailabilityEntity> findOne(Long id);

    boolean isPresent(Long id);

    boolean existsByPharmacyAndDrug(PharmacyEntity pharmacy, DrugEntity drug);

    PharmacyDrugAvailabilityEntity updateAvailability(Long id, boolean isAvailable);

    void delete(Long id);

    // void initialUpdate();

    Page<PharmacyDrugAvailabilityEntity> findAllByLicenceNo(String licenceNo, Pageable pageable);

    Page<PharmacyDrugAvailabilityEntity> findAllByPharmacy(PharmacyEntity pharmacy, Pageable pageable);

    void deleteAllByPharmacy(PharmacyEntity pharmacy);

}