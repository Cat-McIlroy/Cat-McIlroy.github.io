package com.cmcilroy.medicines_shortages_assistant.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.cmcilroy.medicines_shortages_assistant.domain.entities.DrugEntity;
import com.cmcilroy.medicines_shortages_assistant.domain.entities.PharmacyDrugAvailabilityEntity;
import com.cmcilroy.medicines_shortages_assistant.domain.entities.PharmacyEntity;

@Repository
public interface PharmacyDrugAvailabilityRepository extends CrudRepository<PharmacyDrugAvailabilityEntity, Long>, 
PagingAndSortingRepository<PharmacyDrugAvailabilityEntity, Long> {

    // find records matching the exact entered licence no
    Page<PharmacyDrugAvailabilityEntity> findAllByDrug_LicenceNo(String licenceNo, Pageable pageable);

    // find records associated with the entered pharmacy object
    List<PharmacyDrugAvailabilityEntity> findAllByPharmacy(PharmacyEntity pharmacy);

    // find existing records for specific pharmacy and drug combination
    boolean existsByPharmacyAndDrug(PharmacyEntity pharmacy, DrugEntity drug);

    // delete all availability records associated with a specific pharmacy
    void deleteAllByPharmacy(PharmacyEntity pharmacy);

}
