package com.cmcilroy.medicines_shortages_assistant.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import com.cmcilroy.medicines_shortages_assistant.domain.entities.PharmacyEntity;

@Repository
public interface PharmacyRepository extends CrudRepository<PharmacyEntity, Integer>, PagingAndSortingRepository<PharmacyEntity, Integer> {

    // find records matching the exact entered email
    Optional<PharmacyEntity> findByEmail(String email);

}
