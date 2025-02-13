package com.cmcilroy.medicines_shortages_assistant.controllers;

import org.springframework.http.MediaType;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.cmcilroy.medicines_shortages_assistant.TestData;
import com.cmcilroy.medicines_shortages_assistant.domain.dto.PharmacyDto;
import com.cmcilroy.medicines_shortages_assistant.domain.entities.DrugEntity;
import com.cmcilroy.medicines_shortages_assistant.domain.entities.PharmacyEntity;
import com.cmcilroy.medicines_shortages_assistant.services.PharmacyService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc // creates an instance of MockMvc and places it into the application context ready for use
public class PharmacyControllerIntegrationTests {

    // inject MockMvc
    private MockMvc mockMvc;

    // and ObjectMapper
    private ObjectMapper objectMapper;

    // and PharmacyService
    private PharmacyService pharmacyService;

    @Autowired
    public PharmacyControllerIntegrationTests(MockMvc mockMvc, PharmacyService pharmacyService) {
        this.mockMvc = mockMvc;
        this.objectMapper = new ObjectMapper();
        this.pharmacyService = pharmacyService;
    }

    // test to check that createPharmacy method returns a HTTP 201 Created code
    @Test
    public void testThatCreatePharmacyReturnsHttp201Created() throws Exception{
        PharmacyDto pharmacy = TestData.createTestPharmacyDtoA();
        String createPharmacyJson = objectMapper.writeValueAsString(pharmacy);
        mockMvc.perform(
        MockMvcRequestBuilders.put("/pharmacies/" + pharmacy.getPsiRegNo())
            .contentType(MediaType.APPLICATION_JSON)
            .content(createPharmacyJson)
        ).andExpect(
            MockMvcResultMatchers.status().isCreated()
        );
    }

    // test to check that createPharmacy method returns expected saved pharmacy
    @Test
    public void testThatCreatePharmacyReturnsSavedPharmacy() throws Exception{
        PharmacyDto pharmacy = TestData.createTestPharmacyDtoA();
        String createPharmacyJson = objectMapper.writeValueAsString(pharmacy);
        mockMvc.perform(
        MockMvcRequestBuilders.put("/pharmacies/" + pharmacy.getPsiRegNo())
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
        pharmacyService.createPharmacy(pharmacy.getPsiRegNo(),pharmacy);
        mockMvc.perform(
        MockMvcRequestBuilders.get("/pharmacies")
            .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$[0].psiRegNo").value(1234)
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$[0].pharmacyName").value("Pharmacy A")
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$[0].eircode").value("AAAAAAA")
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$[0].phoneNo").value("0123456789")
        );
    }

    // test to check that GetPharmacy method returns a HTTP 200 Ok code when record is found
    @Test
    public void testThatGetPharmacyReturnsHttp200WhenRecordExists() throws Exception {
        PharmacyEntity pharmacy = TestData.createTestPharmacyA();
        // persist Pharmacy entity in the test database
        pharmacyService.createPharmacy(pharmacy.getPsiRegNo(), pharmacy);
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
        pharmacyService.createPharmacy(pharmacy.getPsiRegNo(), pharmacy);
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
}
