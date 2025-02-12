package com.cmcilroy.medicines_shortages_assistant.controllers;

import org.springframework.http.MediaType;
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
import com.cmcilroy.medicines_shortages_assistant.domain.entities.PharmacyEntity;
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

    @Autowired
    public PharmacyControllerIntegrationTests(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
        this.objectMapper = new ObjectMapper();
    }

    // test to check that createPharmacy method returns a HTTP 201 Created code
    @Test
    public void testThatCreatePharmacyReturnsHttp201Created() throws Exception{
        PharmacyEntity pharmacy = TestData.createTestPharmacyA();
        String pharmacyJson = objectMapper.writeValueAsString(pharmacy);
        mockMvc.perform(
        MockMvcRequestBuilders.put("/pharmacies")
            .contentType(MediaType.APPLICATION_JSON)
            .content(pharmacyJson)
        ).andExpect(
            MockMvcResultMatchers.status().isCreated()
        );
    }

    // test to check that createPharmacy method returns expected saved pharmacy
    @Test
    public void testThatCreatePharmacyReturnsSavedPharmacy() throws Exception{
        PharmacyEntity pharmacy = TestData.createTestPharmacyA();
        String pharmacyJson = objectMapper.writeValueAsString(pharmacy);
        mockMvc.perform(
        MockMvcRequestBuilders.put("/pharmacies")
            .contentType(MediaType.APPLICATION_JSON)
            .content(pharmacyJson)
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
