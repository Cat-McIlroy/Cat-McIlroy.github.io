package com.cmcilroy.medicines_shortages_assistant.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.cmcilroy.medicines_shortages_assistant.domain.entities.PharmacyDrugAvailabilityEntity;

@Repository
public interface PharmacyDrugAvailabilityRepository extends CrudRepository<PharmacyDrugAvailabilityEntity, Long>{

}
