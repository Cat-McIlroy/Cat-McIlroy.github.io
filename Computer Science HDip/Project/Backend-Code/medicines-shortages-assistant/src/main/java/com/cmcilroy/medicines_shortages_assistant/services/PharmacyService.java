package com.cmcilroy.medicines_shortages_assistant.services;

import java.util.Optional;

import com.cmcilroy.medicines_shortages_assistant.domain.dto.AccountCredentialsDto;
import com.cmcilroy.medicines_shortages_assistant.domain.entities.PharmacyEntity;

public interface PharmacyService {

    PharmacyEntity registerNewPharmacy(PharmacyEntity pharmacy);

    Optional<PharmacyEntity> signIn(AccountCredentialsDto credentials);

    PharmacyEntity updatePharmacy(String password, PharmacyEntity pharmacy);

    // Page<PharmacyEntity> findAll(Pageable pageable);

    // Optional<PharmacyEntity> findOne(Integer psiRegNo);

    boolean isPresent(Integer psiRegNo);

    // PharmacyEntity partialUpdate(Integer psiRegNo, PharmacyEntity pharmacy);

    void delete(String password, PharmacyEntity pharmacy);

    // void initialUpdate();

}
