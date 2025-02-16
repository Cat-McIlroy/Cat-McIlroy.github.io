package com.cmcilroy.medicines_shortages_assistant.services;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.cmcilroy.medicines_shortages_assistant.domain.entities.PharmacyEntity;

public interface PharmacyService {

    PharmacyEntity savePharmacy(Integer psiRegNo, PharmacyEntity pharmacy);

    Page<PharmacyEntity> findAll(Pageable pageable);

    Optional<PharmacyEntity> findOne(Integer psiRegNo);

    boolean isPresent(Integer psiRegNo);

    PharmacyEntity partialUpdate(Integer psiRegNo, PharmacyEntity pharmacy);

    void delete(Integer psiRegNo);

}
