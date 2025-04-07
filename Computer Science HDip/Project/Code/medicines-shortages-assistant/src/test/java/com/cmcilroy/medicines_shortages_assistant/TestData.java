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
                    .manufacturer("Teva Pharma B.V")
                    .strength("5 mg")
                    .dosageForm("Tablet")
                    .activeSubstance("Amlodipine")
                    .isAvailable(true)
                    .build();
    }

    public static DrugEntity createTestDrugB() {
        return DrugEntity.builder()
                    .licenceNo("PA0865/017/001")
                    .productName("Konverge 20 mg/5 mg film-coated tablets")
                    .manufacturer("Menarini International Operations Luxembourg S.A.")
                    .strength("20 mg/5 mg")
                    .dosageForm("Film-coated tablet")
                    .activeSubstance("Olmesartan medoxomil, Amlodipine")
                    .isAvailable(true)
                    .build();
    }

    public static DrugEntity createTestDrugC() {
        return DrugEntity.builder()
                    .licenceNo("EU/1/17/1251/002")
                    .productName("Ozempic")
                    .manufacturer("Novo Nordisk A/S")
                    .strength("0.25 mg")
                    .dosageForm("Solution for injection in pre-filled pen")
                    .activeSubstance("Semaglutide")
                    .isAvailable(false)
                    .build();
    }

    public static DrugEntity createTestDrugD() {
        return DrugEntity.builder()
                    .licenceNo("PA1410/037/001")
                    .productName("Aspirin 300mg Effervescent tablets")
                    .manufacturer("Bayer Limited")
                    .strength("300mg")
                    .dosageForm("Effervescent tablet")
                    .activeSubstance("Aspirin")
                    .isAvailable(true)
                    .build();
    }      

    public static DrugEntity createTestDrugE() {
        return DrugEntity.builder()
                    .licenceNo("PA2315/189/001")
                    .productName("Nuprin 75mg gastro-resistant tablets")
                    .manufacturer("Accord Healthcare Ireland Ltd.")
                    .strength("75mg")
                    .dosageForm("Gastro-resistant tablet")
                    .activeSubstance("Acetylsalicylic acid")
                    .isAvailable(true)
                    .build();
    }   

    public static DrugEntity createTestDrugF() {
        return DrugEntity.builder()
                    .licenceNo("PA1744/002/003")
                    .productName("Trinomia 100 mg/20 mg/10 mg hard capsules")
                    .manufacturer("Ferrer Internacional, S.A")
                    .strength("100 mg/20 mg/10 mg")
                    .dosageForm("Capsule, hard")
                    .activeSubstance("Acetylsalicylic acid, Ramipril, Atorvastatin calcium trihydrate")
                    .isAvailable(true)
                    .build();
    }

    public static DrugEntity createTestDrugG() {
        return DrugEntity.builder()
                    .licenceNo("PA1744/002/001")
                    .productName("Trinomia 100 mg/20 mg/2.5 mg hard capsules")
                    .manufacturer("Ferrer Internacional, S.A")
                    .strength("100 mg/20 mg/2.5 mg")
                    .dosageForm("Capsule, hard")
                    .activeSubstance("Aspirin, Ramipril, Atorvastatin calcium trihydrate")
                    .isAvailable(true)
                    .build();
    }

    public static DrugEntity createTestDrugH() {
        return DrugEntity.builder()
                    .licenceNo("PA23055/012/003")
                    .productName("Istin 5 mg hard capsule")
                    .manufacturer("Upjohn EESV")
                    .strength("5 mg")
                    .dosageForm("Capsule, hard")
                    .activeSubstance("Amlodipine")
                    .isAvailable(true)
                    .build();
    }

    // Pharmacy Entity Test Methods
    public static PharmacyEntity createTestPharmacyA() {
        return PharmacyEntity.builder()
                    .psiRegNo(1234)
                    .pharmacyName("Pharmacy A")
                    .address("Test Address")
                    .eircode("AAAAAAA")
                    .phoneNo("0123456789")
                    .email("test@email.com")
                    .password("password")
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
                    .manufacturer("Teva Pharma B.V")
                    .strength("5 mg")
                    .dosageForm("Tablet")
                    .activeSubstance("Amlodipine")
                    .isAvailable(true)
                    .build();
    }

    public static DrugDto createTestDrugDtoB() {
        return DrugDto.builder()
                    .licenceNo("PA0865/017/001")
                    .productName("Konverge 20 mg/5 mg film-coated tablets")
                    .manufacturer("Menarini International Operations Luxembourg S.A.")
                    .strength("20 mg/5 mg")
                    .dosageForm("Film-coated tablet")
                    .activeSubstance("Olmesartan medoxomil, Amlodipine")
                    .isAvailable(true)
                    .build();
    }

    public static DrugDto createTestDrugDtoC() {
        return DrugDto.builder()
                    .licenceNo("EU/1/17/1251/002")
                    .productName("Ozempic")
                    .manufacturer("Novo Nordisk A/S")
                    .strength("0.25 mg")
                    .dosageForm("Solution for injection in pre-filled pen")
                    .activeSubstance("Semaglutide")
                    .isAvailable(false)
                    .build();
    }    

    public static DrugDto createTestDrugDtoD() {
        return DrugDto.builder()
                    .licenceNo("PA1410/037/001")
                    .productName("Aspirin 300mg Effervescent tablets")
                    .manufacturer("Bayer Limited")
                    .strength("300mg")
                    .dosageForm("Effervescent tablet")
                    .activeSubstance("Aspirin")
                    .isAvailable(true)
                    .build();
    }  

    public static DrugDto createTestDrugDtoE() {
        return DrugDto.builder()
                    .licenceNo("PA2315/189/001")
                    .productName("Nuprin 75mg gastro-resistant tablets")
                    .manufacturer("Accord Healthcare Ireland Ltd.")
                    .strength("75mg")
                    .dosageForm("Gastro-resistant tablet")
                    .activeSubstance("Acetylsalicylic acid")
                    .isAvailable(true)
                    .build();
    }   

    public static DrugDto createTestDrugDtoF() {
        return DrugDto.builder()
                    .licenceNo("PA1744/002/003")
                    .productName("Trinomia 100 mg/20 mg/10 mg hard capsules")
                    .manufacturer("Ferrer Internacional, S.A")
                    .strength("100 mg/20 mg/10 mg")
                    .dosageForm("Capsule, hard")
                    .activeSubstance("Acetylsalicylic acid, Ramipril, Atorvastatin calcium trihydrate")
                    .isAvailable(true)
                    .build();
    }   

    public static DrugDto createTestDrugDtoG() {
        return DrugDto.builder()
                    .licenceNo("PA1744/002/001")
                    .productName("Trinomia 100 mg/20 mg/2.5 mg hard capsules")
                    .manufacturer("Ferrer Internacional, S.A")
                    .strength("100 mg/20 mg/2.5 mg")
                    .dosageForm("Capsule, hard")
                    .activeSubstance("Aspirin, Ramipril, Atorvastatin calcium trihydrate")
                    .isAvailable(true)
                    .build();
    }   

    public static DrugDto createTestDrugDtoH() {
        return DrugDto.builder()
                    .licenceNo("PA23055/012/003")
                    .productName("Istin 5 mg hard capsule")
                    .manufacturer("Upjohn EESV")
                    .strength("5 mg")
                    .dosageForm("Capsule, hard")
                    .activeSubstance("Amlodipine")
                    .isAvailable(true)
                    .build();
    }   

    // Pharmacy Entity Test Methods
    public static PharmacyDto createTestPharmacyDtoA() {
        return PharmacyDto.builder()
                    .psiRegNo(1234)
                    .pharmacyName("Pharmacy A")
                    .address("Test Address")
                    .eircode("AAAAAAA")
                    .phoneNo("0123456789")
                    .email("test@email.com")
                    .password("password")
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
