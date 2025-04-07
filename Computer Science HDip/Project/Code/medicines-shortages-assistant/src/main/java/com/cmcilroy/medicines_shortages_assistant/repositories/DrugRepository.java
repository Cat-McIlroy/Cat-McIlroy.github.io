package com.cmcilroy.medicines_shortages_assistant.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import com.cmcilroy.medicines_shortages_assistant.domain.entities.DrugEntity;

@Repository
public interface DrugRepository extends CrudRepository<DrugEntity, String>, PagingAndSortingRepository<DrugEntity, String>{

    // find active substance containing the active substance of the entered product name
    // this allows for variations regarding the drug salts used, e.g. amlodipine besilate, amlodipine maleate etc are all amlodipine
    @Query("SELECT d FROM DrugEntity d WHERE LOWER(d.activeSubstance) LIKE LOWER(CONCAT('%', :activeSubstance, '%'))")
    List<DrugEntity> findAllByContainsActiveSubstance(String activeSubstance);

    // find record containing the entered product name
    @Query("SELECT d FROM DrugEntity d WHERE LOWER(d.productName) LIKE LOWER(CONCAT('%', :productName, '%'))")
    List<DrugEntity> findByContainsProductName(String productName);

    // find all records by availability
    Iterable<DrugEntity> findAllByIsAvailable(boolean isAvailable);
}
