package com.cmcilroy.medicines_shortages_assistant.services;

import java.util.List;
import java.util.Optional;

import com.cmcilroy.medicines_shortages_assistant.domain.entities.PharmacyEntity;

public interface PharmacyService {

    PharmacyEntity createPharmacy(Integer psiRegNo, PharmacyEntity pharmacy);

    List<PharmacyEntity> findAll();

    Optional<PharmacyEntity> findOne(Integer psiRegNo);
}
