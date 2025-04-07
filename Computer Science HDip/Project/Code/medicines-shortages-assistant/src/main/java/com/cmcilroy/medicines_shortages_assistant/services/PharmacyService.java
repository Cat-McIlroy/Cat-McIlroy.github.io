package com.cmcilroy.medicines_shortages_assistant.services;

import java.util.Optional;

import com.cmcilroy.medicines_shortages_assistant.domain.dto.AccountCredentialsDto;
import com.cmcilroy.medicines_shortages_assistant.domain.entities.PharmacyEntity;

public interface PharmacyService {

    PharmacyEntity registerNewPharmacy(PharmacyEntity pharmacy);

    Optional<PharmacyEntity> signIn(AccountCredentialsDto credentials);

    PharmacyEntity updatePharmacy(String password, PharmacyEntity pharmacy);

    boolean isPresent(Integer psiRegNo);

    void delete(String password, PharmacyEntity pharmacy);

}
