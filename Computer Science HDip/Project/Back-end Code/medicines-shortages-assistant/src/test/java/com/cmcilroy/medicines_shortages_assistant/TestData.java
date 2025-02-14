// this class contains methods and data for use in testing

package com.cmcilroy.medicines_shortages_assistant;

import com.cmcilroy.medicines_shortages_assistant.domain.dto.DrugDto;
import com.cmcilroy.medicines_shortages_assistant.domain.dto.PharmacyDrugAvailabilityDto;
import com.cmcilroy.medicines_shortages_assistant.domain.dto.PharmacyDto;
import com.cmcilroy.medicines_shortages_assistant.domain.entities.DrugEntity;
import com.cmcilroy.medicines_shortages_assistant.domain.entities.PharmacyDrugAvailabilityEntity;
import com.cmcilroy.medicines_shortages_assistant.domain.entities.PharmacyEntity;

public final class TestData {

    // the constructor should be private to prevent this class from being instantiated
    private TestData(){

    }

/////////////////////////////////////////////// ENTITIES ////////////////////////////////////////////////////

    // Drug Entity Test Methods

    public static DrugEntity createTestDrugA() {
        return DrugEntity.builder()
                    .licenceNo("PA0749/067/001")
                    .productName("Amlodipine Teva 5 mg Tablets")
                    .strength("5 mg")
                    .activeSubstance("Amlodipine")
                    .isAvailable(true)
                    .build();
    }

    public static DrugEntity createTestDrugB() {
        return DrugEntity.builder()
                    .licenceNo("PA2242/013/003")
                    .productName("Nexium 40 mg gastro-resistant tablets")
                    .strength("40 mg")
                    .activeSubstance("Esomeprazole")
                    .isAvailable(true)
                    .build();
    }

    public static DrugEntity createTestDrugC() {
        return DrugEntity.builder()
                    .licenceNo("EU/1/17/1251/002")
                    .productName("Ozempic")
                    .strength("0.25 mg")
                    .activeSubstance("Semaglutide")
                    .isAvailable(false)
                    .build();
    }    

    // Pharmacy Entity Test Methods
    public static PharmacyEntity createTestPharmacyA() {
        return PharmacyEntity.builder()
                    .psiRegNo(1234)
                    .pharmacyName("Pharmacy A")
                    .eircode("AAAAAAA")
                    .phoneNo("0123456789")
                    .build();
    }

    public static PharmacyEntity createTestPharmacyB() {
        return PharmacyEntity.builder()
                    .psiRegNo(10000789)
                    .pharmacyName("Pharmacy B")
                    .eircode("BBBBBBB")
                    .phoneNo("+353135628927")
                    .build();
    }

    public static PharmacyEntity createTestPharmacyC() {
        return PharmacyEntity.builder()
                    .psiRegNo(80056)
                    .pharmacyName("Pharmacy C")
                    .eircode("CCCCCCC")
                    .phoneNo("01852 2117")
                    .build();
    }

    // PharmacyDrugAvailability Entity Test Methods
    public static PharmacyDrugAvailabilityEntity createTestPharmacyDrugAvailabilityA(final PharmacyEntity pharmacy, final DrugEntity drug) {
        return PharmacyDrugAvailabilityEntity.builder()
                    // id is automatically generated so don't declare here, it causes ObjectOptimisticLockingFailure
                    .pharmacy(pharmacy)
                    .drug(drug)
                    .isAvailable(true)
                    .build();
    }

    public static PharmacyDrugAvailabilityEntity createTestPharmacyDrugAvailabilityB(final PharmacyEntity pharmacy, final DrugEntity drug) {
        return PharmacyDrugAvailabilityEntity.builder()
                    // id is automatically generated so don't declare here, it causes ObjectOptimisticLockingFailure
                    .pharmacy(pharmacy)
                    .drug(drug)
                    .isAvailable(false)
                    .build();
    }

    public static PharmacyDrugAvailabilityEntity createTestPharmacyDrugAvailabilityC(final PharmacyEntity pharmacy, final DrugEntity drug) {
        return PharmacyDrugAvailabilityEntity.builder()
                    // id is automatically generated so don't declare here, it causes ObjectOptimisticLockingFailure
                    .pharmacy(pharmacy)
                    .drug(drug)
                    .isAvailable(true)
                    .build();
    }

////////////////////////////////////////////////////// DTOs ////////////////////////////////////////

    // Drug DTO Test methods

    public static DrugDto createTestDrugDtoA() {
        return DrugDto.builder()
                    .licenceNo("PA0749/067/001")
                    .productName("Amlodipine Teva 5 mg Tablets")
                    .strength("5 mg")
                    .activeSubstance("Amlodipine")
                    .isAvailable(true)
                    .build();
    }

    public static DrugDto createTestDrugDtoB() {
        return DrugDto.builder()
                    .licenceNo("PA2242/013/003")
                    .productName("Nexium 40 mg gastro-resistant tablets")
                    .strength("40 mg")
                    .activeSubstance("Esomeprazole")
                    .isAvailable(true)
                    .build();
    }

    public static DrugDto createTestDrugDtoC() {
        return DrugDto.builder()
                    .licenceNo("EU/1/17/1251/002")
                    .productName("Ozempic")
                    .strength("0.25 mg")
                    .activeSubstance("Semaglutide")
                    .isAvailable(false)
                    .build();
    }    

    // Pharmacy Entity Test Methods
    public static PharmacyDto createTestPharmacyDtoA() {
        return PharmacyDto.builder()
                    .psiRegNo(1234)
                    .pharmacyName("Pharmacy A")
                    .eircode("AAAAAAA")
                    .phoneNo("0123456789")
                    .build();
    }

    public static PharmacyDto createTestPharmacyDtoB() {
        return PharmacyDto.builder()
                    .psiRegNo(10000789)
                    .pharmacyName("Pharmacy B")
                    .eircode("BBBBBBB")
                    .phoneNo("+353135628927")
                    .build();
    }

    public static PharmacyDto createTestPharmacyDtoC() {
        return PharmacyDto.builder()
                    .psiRegNo(80056)
                    .pharmacyName("Pharmacy C")
                    .eircode("CCCCCCC")
                    .phoneNo("01852 2117")
                    .build();
    }

    // PharmacyDrugAvailability Entity Test Methods
    public static PharmacyDrugAvailabilityDto createTestPharmacyDrugAvailabilityDtoA(final PharmacyDto pharmacy, final DrugDto drug) {
        return PharmacyDrugAvailabilityDto.builder()
                    // id is automatically generated so don't declare here, it causes ObjectOptimisticLockingFailure
                    .pharmacy(pharmacy)
                    .drug(drug)
                    .isAvailable(true)
                    .build();
    }

    public static PharmacyDrugAvailabilityDto createTestPharmacyDrugAvailabilityDtoB(final PharmacyDto pharmacy, final DrugDto drug) {
        return PharmacyDrugAvailabilityDto.builder()
                    // id is automatically generated so don't declare here, it causes ObjectOptimisticLockingFailure
                    .pharmacy(pharmacy)
                    .drug(drug)
                    .isAvailable(false)
                    .build();
    }

    public static PharmacyDrugAvailabilityDto createTestPharmacyDrugAvailabilityDtoC(final PharmacyDto pharmacy, final DrugDto drug) {
        return PharmacyDrugAvailabilityDto.builder()
                    // id is automatically generated so don't declare here, it causes ObjectOptimisticLockingFailure
                    .pharmacy(pharmacy)
                    .drug(drug)
                    .isAvailable(true)
                    .build();
    }
}
