package com.cmcilroy.medicines_shortages_assistant.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.cmcilroy.medicines_shortages_assistant.domain.entities.PharmacyEntity;

@Repository
public interface PharmacyRepository extends CrudRepository<PharmacyEntity, Integer>{

}
