package com.cmcilroy.medicines_shortages_assistant.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import com.cmcilroy.medicines_shortages_assistant.domain.entities.DrugEntity;

@Repository
public interface DrugRepository extends CrudRepository<DrugEntity, String>, PagingAndSortingRepository<DrugEntity, String>{
    
    // find active substance containing the active substance of the entered product name
    // this allows for variations, i.e. "Amlodipine", "Amlodipine besilate" are the same active
    // don't retrieve active substances with commas as these are combination drugs
    @Query("SELECT d FROM DrugEntity d WHERE LOWER(d.activeSubstance) LIKE LOWER(CONCAT('%', :activeSubstance, '%')) " +
        "AND d.activeSubstance NOT LIKE '%,%'")
    Page<DrugEntity> findAllByActiveSubstance(String activeSubstance, Pageable pageable);

    // find active substance containing the active substance of the entered product name
    // this allows for variations, i.e. "Amlodipine", "Amlodipine besilate" are the same active
    // include results with commas as this method is looking for matches for a combination drug
    @Query("SELECT d FROM DrugEntity d WHERE LOWER(d.activeSubstance) LIKE LOWER(CONCAT('%', :activeSubstance, '%'))")
    Page<DrugEntity> findAllByComboActiveSubstances(String activeSubstance, Pageable pageable);

    // find record containing the entered product name
    @Query("SELECT d FROM DrugEntity d WHERE LOWER(d.productName) LIKE LOWER(CONCAT('%', :productName, '%'))")
    List<DrugEntity> findByContainsProductName(String productName);

    // find record matching the exact entered product name
    // @Query("SELECT d FROM DrugEntity d WHERE LOWER(d.productName) = LOWER(:productName)")
    Optional<DrugEntity> findByProductName(String productName);

    // find all records with by availability
    Iterable<DrugEntity> findAllByIsAvailable(boolean isAvailable);
}
