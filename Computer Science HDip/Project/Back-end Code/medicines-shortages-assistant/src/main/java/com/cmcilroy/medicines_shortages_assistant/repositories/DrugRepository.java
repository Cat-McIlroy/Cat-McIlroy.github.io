package com.cmcilroy.medicines_shortages_assistant.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.cmcilroy.medicines_shortages_assistant.domain.entities.DrugEntity;

@Repository
public interface DrugRepository extends CrudRepository<DrugEntity, String>{

}
