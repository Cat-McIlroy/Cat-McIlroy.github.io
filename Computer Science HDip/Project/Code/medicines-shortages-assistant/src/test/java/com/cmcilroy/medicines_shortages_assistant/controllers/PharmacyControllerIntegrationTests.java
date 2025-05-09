package com.cmcilroy.medicines_shortages_assistant.controllers;

import org.springframework.http.MediaType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import com.cmcilroy.medicines_shortages_assistant.TestData;
import com.cmcilroy.medicines_shortages_assistant.cleaner.DatabaseCleaner;
import com.cmcilroy.medicines_shortages_assistant.domain.dto.PharmacyDto;
import com.cmcilroy.medicines_shortages_assistant.domain.entities.PharmacyEntity;
import com.cmcilroy.medicines_shortages_assistant.services.PharmacyService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc // creates an instance of MockMvc and places it into the application context ready for use
public class PharmacyControllerIntegrationTests {

    // inject MockMvc
    private MockMvc mockMvc;

    // and ObjectMapper
    private ObjectMapper objectMapper;

    // and PharmacyService
    private PharmacyService pharmacyService;

    // and DatabaseCleaner 
    private DatabaseCleaner databaseCleaner;

    @Autowired
    public PharmacyControllerIntegrationTests(MockMvc mockMvc, PharmacyService pharmacyService, DatabaseCleaner databaseCleaner) {
        this.mockMvc = mockMvc;
        this.objectMapper = new ObjectMapper();
        this.pharmacyService = pharmacyService;
        this.databaseCleaner = databaseCleaner;
    }


///////////////////////////////////////////////// CLEAR DATABASE BEFORE EACH TEST ////////////////////////////////////////////////////

    @BeforeEach
    public void clearDatabase() {
        databaseCleaner.clearDatabase();
    }    

///////////////////////////////////////////////// CREATE & UPDATE METHOD TESTS ///////////////////////////////////////////////////////

    // test to check that createUpdatePharmacy method returns a HTTP 201 Created code on creating new record
    @Test
    public void testThatCreatePharmacyReturnsHttp201Created() throws Exception{
        PharmacyDto pharmacy = TestData.createTestPharmacyDtoA();
        String createPharmacyJson = objectMapper.writeValueAsString(pharmacy);
        mockMvc.perform(
        MockMvcRequestBuilders.post("/pharmacies/register" + pharmacy.getPsiRegNo())
            .contentType(MediaType.APPLICATION_JSON)
            .content(createPharmacyJson)
        ).andExpect(
            MockMvcResultMatchers.status().isCreated()
        );
    }

    // test to check that createUpdatePharmacy method returns a HTTP 200 Ok code on updating existing record
    @Test
    public void testThatUpdatePharmacyReturnsHttp200Ok() throws Exception{
        PharmacyEntity pharmacyEntity = TestData.createTestPharmacyA();
        PharmacyEntity savedPharmacyEntity = pharmacyService.updatePharmacy(pharmacyEntity.getPassword(), pharmacyEntity);
        PharmacyDto pharmacyDto = TestData.createTestPharmacyDtoA();
        // make sure PSI reg numbers of savedPharmacyEntity and pharmacyDto match 
        pharmacyDto.setPsiRegNo(savedPharmacyEntity.getPsiRegNo());
        String pharmacyJson = objectMapper.writeValueAsString(pharmacyDto);
        mockMvc.perform(
        MockMvcRequestBuilders.patch("/pharmacies/" + pharmacyDto.getPsiRegNo())
            .contentType(MediaType.APPLICATION_JSON)
            .content(pharmacyJson)
        ).andExpect(
            MockMvcResultMatchers.status().isOk()
        );
    }

    // test to check that createUpdatePharmacy method returns expected created pharmacy
    @Test
    public void testThatCreatePharmacyReturnsSavedPharmacy() throws Exception{
        PharmacyDto pharmacy = TestData.createTestPharmacyDtoA();
        String createPharmacyJson = objectMapper.writeValueAsString(pharmacy);
        mockMvc.perform(
        MockMvcRequestBuilders.post("/pharmacies/register" + pharmacy.getPsiRegNo())
            .contentType(MediaType.APPLICATION_JSON)
            .content(createPharmacyJson)
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.psiRegNo").value(pharmacy.getPsiRegNo())
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.pharmacyName").value(pharmacy.getPharmacyName())
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.eircode").value(pharmacy.getEircode())
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.phoneNo").value(pharmacy.getPhoneNo())
        );
    }

    // test to check that createUpdatePharmacy method returns expected updated pharmacy
    @Test
    public void testThatUpdatePharmacyReturnsUpdatedPharmacy() throws Exception{
        PharmacyEntity pharmacyEntity = TestData.createTestPharmacyA();
        PharmacyEntity savedPharmacyEntity = pharmacyService.updatePharmacy(pharmacyEntity.getPassword(), pharmacyEntity);
        PharmacyDto pharmacyDto = TestData.createTestPharmacyDtoA();
        // make sure licence numbers of savedPharmacyEntity and pharmacyDto match 
        pharmacyDto.setPsiRegNo(savedPharmacyEntity.getPsiRegNo());
        // change pharmacy name property, to test update functionality
        pharmacyDto.setPharmacyName("UPDATED PHARMACY NAME");
        String pharmacyJson = objectMapper.writeValueAsString(pharmacyDto);
        mockMvc.perform(
        MockMvcRequestBuilders.patch("/pharmacies/" + pharmacyDto.getPsiRegNo())
            .contentType(MediaType.APPLICATION_JSON)
            .content(pharmacyJson)
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.psiRegNo").value(pharmacyDto.getPsiRegNo())
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.pharmacyName").value(pharmacyDto.getPharmacyName())
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.eircode").value(pharmacyDto.getEircode())
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.phoneNo").value(pharmacyDto.getPhoneNo())
        );
    }

//////////////////////////////////////////////////////// PARTIAL UPDATE METHOD TESTS /////////////////////////////////////////////////////////

    // test to check that partialUpdatePharmacy method returns a HTTP 404 Not Found code when record doesn't exist
    @Test
    public void testThatPartialUpdatePharmacyReturnsHttp404WhenNoRecordExists() throws Exception {
        PharmacyDto pharmacy = TestData.createTestPharmacyDtoA();
        // no entity saved in database here, so the record should not exist, and a HTTP 404 should be returned
        String pharmacyJson = objectMapper.writeValueAsString(pharmacy);

        mockMvc.perform(
            MockMvcRequestBuilders.patch("/pharmacies/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(pharmacyJson)
        ).andExpect(
            MockMvcResultMatchers.status().isNotFound()
        );
    }

    // test to check that partialUpdatePharmacy method returns a HTTP 200 Ok code on updating existing record
    @Test
    public void testThatPartialUpdatePharmacyReturnsHttp200Ok() throws Exception{
        PharmacyEntity pharmacyEntity = TestData.createTestPharmacyA();
        pharmacyService.updatePharmacy(pharmacyEntity.getPassword(), pharmacyEntity);
        PharmacyDto pharmacyDto = TestData.createTestPharmacyDtoA();
        // update pharmacy name of Dto to be passed in
        pharmacyDto.setPharmacyName("UPDATED");
        String pharmacyJson = objectMapper.writeValueAsString(pharmacyDto);
        mockMvc.perform(
            MockMvcRequestBuilders.patch("/pharmacies/" + pharmacyEntity.getPsiRegNo())
                .contentType(MediaType.APPLICATION_JSON)
                .content(pharmacyJson)
        ).andExpect(
            MockMvcResultMatchers.status().isOk()
        );
    }

    // test to check that partialUpdatePharmacy method returns expected updated pharmacy
    @Test
    public void testThatPartialUpdatePharmacyReturnsUpdatedRecord() throws Exception{
        PharmacyEntity pharmacyEntity = TestData.createTestPharmacyA();
        pharmacyService.updatePharmacy(pharmacyEntity.getPassword(), pharmacyEntity);
        PharmacyDto pharmacyDto = TestData.createTestPharmacyDtoA();
        // update pharmacy name of Dto to be passed in
        pharmacyDto.setPharmacyName("UPDATED");
        String pharmacyJson = objectMapper.writeValueAsString(pharmacyDto);
        mockMvc.perform(
            MockMvcRequestBuilders.patch("/pharmacies/" + pharmacyEntity.getPsiRegNo())
                .contentType(MediaType.APPLICATION_JSON)
                .content(pharmacyJson)
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.psiRegNo").value(pharmacyDto.getPsiRegNo())
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.pharmacyName").value("UPDATED")
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.eircode").value(pharmacyDto.getEircode())
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.phoneNo").value(pharmacyDto.getPhoneNo())
        );
    }

////////////////////////////////////////////////////////////// FIND METHODS TESTS ////////////////////////////////////////////////////////

    // test to check that listAllPharmacies method returns a HTTP 200 Ok code
    @Test
    public void testThatListAllPharmaciesReturnsHttp200Ok() throws Exception{
        mockMvc.perform(
        MockMvcRequestBuilders.get("/pharmacies")
            .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
            MockMvcResultMatchers.status().isOk()
        );
    }

    // test to check that listAllPharmacies method returns expected pharmacy
    @Test
    public void testThatListAllPharmaciesReturnsPharmacy() throws Exception{
        PharmacyEntity pharmacy = TestData.createTestPharmacyA();
        pharmacyService.registerNewPharmacy(pharmacy);
        mockMvc.perform(
        MockMvcRequestBuilders.get("/pharmacies")
            .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.content[0].psiRegNo").value(1234)
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.content[0].pharmacyName").value("Pharmacy A")
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.content[0].eircode").value("AAAAAAA")
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.content[0].phoneNo").value("0123456789")
        );
    }

    // test to check that GetPharmacy method returns a HTTP 200 Ok code when record is found
    @Test
    public void testThatGetPharmacyReturnsHttp200WhenRecordExists() throws Exception {
        PharmacyEntity pharmacy = TestData.createTestPharmacyA();
        // persist Pharmacy entity in the test database
        pharmacyService.registerNewPharmacy(pharmacy);
        mockMvc.perform(
            MockMvcRequestBuilders.get("/pharmacies/" + pharmacy.getPsiRegNo())
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
            MockMvcResultMatchers.status().isOk()
        );
    }

    // test to check that GetPharmacy method returns a HTTP 404 Not Found code when record doesn't exist
    @Test
    public void testThatGetPharmacyReturnsHttp404WhenNoRecordExists() throws Exception {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/pharmacies/0")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
            MockMvcResultMatchers.status().isNotFound()
        );
    }

    // test to check that GetPharmacy method returns correct record when record exists
    @Test
    public void testThatGetPharmacyReturnsCorrectRecordWhenRecordExists() throws Exception {
        PharmacyEntity pharmacy = TestData.createTestPharmacyA();
        // persist Pharmacy entity in the test database
        pharmacyService.registerNewPharmacy(pharmacy);
        mockMvc.perform(
            MockMvcRequestBuilders.get("/pharmacies/" + pharmacy.getPsiRegNo())
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.psiRegNo").value(1234)
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.pharmacyName").value("Pharmacy A")
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.eircode").value("AAAAAAA")
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.phoneNo").value("0123456789")
        );
    }

//////////////////////////////////////////////////////// DELETE METHOD TESTS /////////////////////////////////////////////////////////

    // test to check that deletePharmacy method returns a HTTP 204 No Content code when non-existent record is deleted
    @Test
    public void testThatDeleteNonExistentPharmacyReturnsHttp204() throws Exception {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/pharmacies/1")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
            MockMvcResultMatchers.status().isNoContent()
        );
    }

    // test to check that deletePharmacy method returns a HTTP 204 No Content code when existing record is deleted
    @Test
    public void testThatDeleteExistingPharmacyReturnsHttp204() throws Exception {
        PharmacyEntity pharmacy = TestData.createTestPharmacyA();
        // persist Pharmacy entity in the test database
        pharmacyService.registerNewPharmacy(pharmacy);
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/pharmacies/" + pharmacy.getPsiRegNo())
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
            MockMvcResultMatchers.status().isNoContent()
        );
    }

}