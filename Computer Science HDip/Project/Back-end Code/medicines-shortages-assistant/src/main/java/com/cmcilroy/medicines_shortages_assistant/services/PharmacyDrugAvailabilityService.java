package com.cmcilroy.medicines_shortages_assistant.services;

import java.util.List;
import java.util.Optional;

import com.cmcilroy.medicines_shortages_assistant.domain.entities.PharmacyDrugAvailabilityEntity;

public interface PharmacyDrugAvailabilityService {

    PharmacyDrugAvailabilityEntity createPharmacyDrugAvailability(PharmacyDrugAvailabilityEntity pharmacyDrugAvailability);

    List<PharmacyDrugAvailabilityEntity> findAll();

    Optional<PharmacyDrugAvailabilityEntity> findOne(Long id);
}
