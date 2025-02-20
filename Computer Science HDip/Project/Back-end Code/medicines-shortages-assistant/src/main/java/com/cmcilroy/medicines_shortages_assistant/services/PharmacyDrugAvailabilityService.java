package com.cmcilroy.medicines_shortages_assistant.services;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.cmcilroy.medicines_shortages_assistant.domain.entities.PharmacyDrugAvailabilityEntity;

public interface PharmacyDrugAvailabilityService {

    PharmacyDrugAvailabilityEntity savePharmacyDrugAvailability(PharmacyDrugAvailabilityEntity pharmacyDrugAvailability);

    Page<PharmacyDrugAvailabilityEntity> findAll(Pageable pageable);

    Optional<PharmacyDrugAvailabilityEntity> findOne(Long id);

    boolean isPresent(Long id);

    PharmacyDrugAvailabilityEntity partialUpdate(Long id, PharmacyDrugAvailabilityEntity pharmacyDrugAvailability);

    void delete(Long id);

    void initialUpdate();

    Page<PharmacyDrugAvailabilityEntity> findAllByLicenceNo(String licenceNo, Pageable pageable);

}