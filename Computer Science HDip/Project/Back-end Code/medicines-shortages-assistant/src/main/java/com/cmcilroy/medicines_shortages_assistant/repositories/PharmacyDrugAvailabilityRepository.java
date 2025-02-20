package com.cmcilroy.medicines_shortages_assistant.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import com.cmcilroy.medicines_shortages_assistant.domain.entities.PharmacyDrugAvailabilityEntity;

@Repository
public interface PharmacyDrugAvailabilityRepository extends CrudRepository<PharmacyDrugAvailabilityEntity, Long>, 
PagingAndSortingRepository<PharmacyDrugAvailabilityEntity, Long> {

    // find records matching the exact entered licence no
    @Query("SELECT d FROM PharmacyDrugAvailabilityEntity d WHERE d.drug.licenceNo = :licenceNo")
    Page<PharmacyDrugAvailabilityEntity> findAllByLicenceNo(String licenceNo, Pageable pageable);

}
