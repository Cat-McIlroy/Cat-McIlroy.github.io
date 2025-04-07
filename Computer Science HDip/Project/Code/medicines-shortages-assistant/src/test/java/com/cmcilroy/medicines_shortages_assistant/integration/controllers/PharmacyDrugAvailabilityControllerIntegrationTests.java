package com.cmcilroy.medicines_shortages_assistant.integration.controllers;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.cmcilroy.medicines_shortages_assistant.TestData;
import com.cmcilroy.medicines_shortages_assistant.cleaner.DatabaseCleaner;
import com.cmcilroy.medicines_shortages_assistant.domain.dto.PharmacyDto;
import com.cmcilroy.medicines_shortages_assistant.domain.entities.PharmacyDrugAvailabilityEntity;
import com.cmcilroy.medicines_shortages_assistant.repositories.DrugRepository;
import com.cmcilroy.medicines_shortages_assistant.repositories.PharmacyRepository;
import com.cmcilroy.medicines_shortages_assistant.services.PharmacyDrugAvailabilityService;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class PharmacyDrugAvailabilityControllerIntegrationTests {
    
    // inject MockMvc
    private MockMvc mockMvc;

    // and Service
    private PharmacyDrugAvailabilityService availabilityService;

    // and Repositories
    private PharmacyRepository pharmacyRepository;
    private DrugRepository drugRepository;

    // and DatabaseCleaner
    private DatabaseCleaner databaseCleaner;

    @Autowired
    public PharmacyDrugAvailabilityControllerIntegrationTests(
        MockMvc mockMvc, 
        PharmacyDrugAvailabilityService availabilityService,
        PharmacyRepository pharmacyRepository,
        DrugRepository drugRepository,
        DatabaseCleaner databaseCleaner
    ) {
        this.mockMvc = mockMvc;
        this.availabilityService = availabilityService;
        this.pharmacyRepository = pharmacyRepository;
        this.drugRepository = drugRepository;
        this.databaseCleaner = databaseCleaner;
    }


///////////////////////////////////////////////// CLEAR DATABASE BEFORE EACH TEST ////////////////////////////////////////////////////

    @BeforeEach
    public void clearDatabase() {
        databaseCleaner.clearDatabase();
    }

///////////////////////////////////////////////// CLEAR SECURITY CONTEXT AFTER EACH TEST ////////////////////////////////////////////////////

    @AfterEach
    public void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

///////////////////////////////////////////////////// listPharmacyDrugAvailabilitiesByDrug() METHOD TESTS //////////////////////////////////////////////////////////

    // test for listPharmacyDrugAvailabilitiesByDrug() method when results are returned
    @Test
    public void testListPharmacyDrugAvailabilitiesByDrugHasResults() throws Exception {
        // create test licence number
        String licenceNo = "PA0749/067/001";
        // persist required pharmacy and drug entities
        pharmacyRepository.save(TestData.createTestPharmacyA());
        drugRepository.save(TestData.createTestDrugA());
        // create test availability entity
        PharmacyDrugAvailabilityEntity availabilityEntity = TestData.createTestPharmacyDrugAvailabilityA(
            TestData.createTestPharmacyA(), TestData.createTestDrugA()
        );
        // persist entity in database
        availabilityService.savePharmacyDrugAvailability(availabilityEntity);

        // perform GET request and validate the response
        mockMvc.perform(MockMvcRequestBuilders.get("/pharmacy-drug-availabilities/search-for-stock")
                .accept(MediaType.APPLICATION_JSON)
                .param("licenceNo", licenceNo)
                .param("page", "0")
                .param("size", "10"))
                // expect HTTP Status 200 OK
                .andExpect(status().isOk())
                // expect availability details to match those of the test availability
                .andExpect(jsonPath("$.content[0].pharmacy.psiRegNo", is(availabilityEntity.getPharmacy().getPsiRegNo())))
                .andExpect(jsonPath("$.content[0].drug.licenceNo", is(availabilityEntity.getDrug().getLicenceNo())))
                .andExpect(jsonPath("$.content[0].isAvailable", is(true)));

    }

    // test for listPharmacyDrugAvailabilitiesByDrug() method when no results are returned
    @Test
    public void testListPharmacyDrugAvailabilitiesByDrugNoResults() throws Exception {
        // create test licence number
        String licenceNo = "PA0749/067/001";

        // perform GET request and validate the response
        mockMvc.perform(MockMvcRequestBuilders.get("/pharmacy-drug-availabilities/search-for-stock")
                .accept(MediaType.APPLICATION_JSON)
                .param("licenceNo", licenceNo)
                .param("page", "0")
                .param("size", "10"))
                // expect HTTP Status 404 Not Found
                .andExpect(status().isNotFound())
                // expect that the response is an empty string
                .andExpect(content().string(""));

    }

/////////////////////////////////////// listAllPharmacyDrugAvailabilities METHOD TESTS /////////////////////////////////////////////////////////

    // test for listAllPharmacyDrugAvailabilities() method when results are returned
    @Test
    public void testListAllPharmacyDrugAvailabilitiesHasResults() throws Exception {
        // create test pharmacyDto and authentication
        PharmacyDto pharmacyDto = TestData.createTestPharmacyDtoA();
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
            pharmacyDto, 
            null, 
            List.of(new SimpleGrantedAuthority("ROLE_PHARMACY_USER"))
        );
        // store authentication in the SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // persist required pharmacy and drug entities
        pharmacyRepository.save(TestData.createTestPharmacyA());
        drugRepository.save(TestData.createTestDrugA());
        // create test availabilityEntity
        PharmacyDrugAvailabilityEntity availabilityEntity = TestData.createTestPharmacyDrugAvailabilityA(
            TestData.createTestPharmacyA(), TestData.createTestDrugA()
        );
        // persist entity in database
        availabilityService.savePharmacyDrugAvailability(availabilityEntity);

        // perform GET request and validate the response
        mockMvc.perform(MockMvcRequestBuilders.get("/pharmacy-drug-availabilities/view-all")
                .accept(MediaType.APPLICATION_JSON)
                .param("page", "0")
                .param("size", "10"))
                // expect HTTP Status 200 OK
                .andExpect(status().isOk())
                // expect availability details to match those of the test availability
                .andExpect(jsonPath("$.content[0].pharmacy.psiRegNo", is(pharmacyDto.getPsiRegNo())))
                .andExpect(jsonPath("$.content[0].drug.licenceNo", is(availabilityEntity.getDrug().getLicenceNo())))
                .andExpect(jsonPath("$.content[0].isAvailable", is(true)));

    }

    // test for listAllPharmacyDrugAvailabilities() method when no results are returned
    @Test
    public void testListAllPharmacyDrugAvailabilitiesNoResults() throws Exception {
        // create test pharmacyDto and authentication
        PharmacyDto pharmacyDto = TestData.createTestPharmacyDtoA();
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
            pharmacyDto, 
            null, 
            List.of(new SimpleGrantedAuthority("ROLE_PHARMACY_USER"))
        );
        // store authentication in the SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // perform GET request and validate the response
        mockMvc.perform(MockMvcRequestBuilders.get("/pharmacy-drug-availabilities/view-all")
                .accept(MediaType.APPLICATION_JSON)
                .param("page", "0")
                .param("size", "10"))
                // expect HTTP Status 204 No Content
                .andExpect(status().isNoContent())
                // expect that the response is an empty string
                .andExpect(content().string(""));

    }

/////////////////////////////////////// createPharmacyDrugAvailability METHOD TESTS /////////////////////////////////////////////////////////

    // test for createPharmacyDrugAvailability() method when record created successfully
    @Test
    public void testCreatePharmacyDrugAvailabilityCreatedSuccessfully() throws Exception {
        // create test pharmacyDto and authentication
        PharmacyDto pharmacyDto = TestData.createTestPharmacyDtoA();
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
            pharmacyDto, 
            null, 
            List.of(new SimpleGrantedAuthority("ROLE_PHARMACY_USER"))
        );
        // store authentication in the SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // persist required pharmacy and drug entities
        pharmacyRepository.save(TestData.createTestPharmacyA());
        drugRepository.save(TestData.createTestDrugC());
        // create test availabilityEntity
        PharmacyDrugAvailabilityEntity availabilityEntity = TestData.createTestPharmacyDrugAvailabilityA(
            TestData.createTestPharmacyA(), TestData.createTestDrugC()
        );

        // declare test drug licence number and pharmacy stock availability status
        String licenceNo = "EU/1/17/1251/002";
        boolean isAvailable = true;

        // perform POST request and validate the response
        mockMvc.perform(post("/pharmacy-drug-availabilities/create")
                .contentType(MediaType.APPLICATION_JSON)
                .param("licenceNo", licenceNo)
                .param("isAvailable", String.valueOf(isAvailable))
                .accept(MediaType.APPLICATION_JSON))
                // expect HTTP Status 201 created
                .andExpect(status().isCreated())
                // expect that the availabilityDto is returned in the response
                .andExpect(jsonPath("$.pharmacy.psiRegNo", is(pharmacyDto.getPsiRegNo())))
                .andExpect(jsonPath("$.pharmacy.pharmacyName", is(pharmacyDto.getPharmacyName())))
                .andExpect(jsonPath("$.pharmacy.address", is(pharmacyDto.getAddress())))
                .andExpect(jsonPath("$.pharmacy.eircode", is(pharmacyDto.getEircode())))
                .andExpect(jsonPath("$.pharmacy.phoneNo", is(pharmacyDto.getPhoneNo())))
                .andExpect(jsonPath("$.pharmacy.email", is(pharmacyDto.getEmail())))
                .andExpect(jsonPath("$.pharmacy.password", is(pharmacyDto.getPassword())))
                .andExpect(jsonPath("$.drug.licenceNo", is(availabilityEntity.getDrug().getLicenceNo())))
                .andExpect(jsonPath("$.drug.productName", is(availabilityEntity.getDrug().getProductName())))
                .andExpect(jsonPath("$.drug.manufacturer", is(availabilityEntity.getDrug().getManufacturer())))
                .andExpect(jsonPath("$.drug.strength", is(availabilityEntity.getDrug().getStrength())))
                .andExpect(jsonPath("$.drug.dosageForm", is(availabilityEntity.getDrug().getDosageForm())))
                .andExpect(jsonPath("$.drug.activeSubstance", is(availabilityEntity.getDrug().getActiveSubstance())))
                .andExpect(jsonPath("$.drug.isAvailable", is(availabilityEntity.getDrug().getIsAvailable())))
                .andExpect(jsonPath("$.isAvailable", is(true)));

    }

    // test for createPharmacyDrugAvailability() method when record already exists
    @Test
    public void testCreatePharmacyDrugAvailabilityRecordAlreadyExists() throws Exception {
        // create test pharmacyDto and authentication
        PharmacyDto pharmacyDto = TestData.createTestPharmacyDtoA();
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
            pharmacyDto, 
            null, 
            List.of(new SimpleGrantedAuthority("ROLE_PHARMACY_USER"))
        );
        // store authentication in the SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // persist required pharmacy and drug entities
        pharmacyRepository.save(TestData.createTestPharmacyA());
        drugRepository.save(TestData.createTestDrugC());
        // create test availabilityEntity
        PharmacyDrugAvailabilityEntity availabilityEntity = TestData.createTestPharmacyDrugAvailabilityA(
            TestData.createTestPharmacyA(), TestData.createTestDrugC()
        );
        // persist the Entity in the database
        availabilityService.savePharmacyDrugAvailability(availabilityEntity);

        // declare test drug licence number and pharmacy stock availability status
        String licenceNo = "EU/1/17/1251/002";
        boolean isAvailable = true;

        // perform POST request and validate the response
        mockMvc.perform(post("/pharmacy-drug-availabilities/create")
                .contentType(MediaType.APPLICATION_JSON)
                .param("licenceNo", licenceNo)
                .param("isAvailable", String.valueOf(isAvailable))
                .accept(MediaType.APPLICATION_JSON))
                // expect HTTP Status 409 Conflict
                .andExpect(status().isConflict())
                // expect that the response is an empty string
                .andExpect(content().string(""));

    }

/////////////////////////////////////// deletePharmacyDrugAvailability METHOD TEST /////////////////////////////////////////////////////////

    // test for deletePharmacyDrugAvailability() method
    @Test
    @WithMockUser(username = "testuser", roles = {"PHARMACY_USER"})
    public void testDeletePharmacyDrugAvailability() throws Exception {
        // persist required pharmacy and drug entities
        pharmacyRepository.save(TestData.createTestPharmacyA());
        drugRepository.save(TestData.createTestDrugC());
        // create test availabilityEntity
        PharmacyDrugAvailabilityEntity availabilityEntity = TestData.createTestPharmacyDrugAvailabilityA(
            TestData.createTestPharmacyA(), TestData.createTestDrugC()
        );
        // persist the Entity in the database
        PharmacyDrugAvailabilityEntity savedEntity = availabilityService.savePharmacyDrugAvailability(availabilityEntity);

        // perform DELETE request and validate the response
        mockMvc.perform(delete("/pharmacy-drug-availabilities/delete/{id}", savedEntity.getId()))
                // expect HTTP Status 204 No Content
                .andExpect(status().isNoContent())
                // expect that the response is an empty string
                .andExpect(content().string(""));

    }

}