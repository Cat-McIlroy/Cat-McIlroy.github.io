package com.cmcilroy.medicines_shortages_assistant.integration.controllers;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.cmcilroy.medicines_shortages_assistant.TestData;
import com.cmcilroy.medicines_shortages_assistant.cleaner.DatabaseCleaner;
import com.cmcilroy.medicines_shortages_assistant.domain.dto.AccountCredentialsDto;
import com.cmcilroy.medicines_shortages_assistant.domain.dto.EditAccountDto;
import com.cmcilroy.medicines_shortages_assistant.domain.dto.PharmacyDto;
import com.cmcilroy.medicines_shortages_assistant.domain.entities.PharmacyEntity;
import com.cmcilroy.medicines_shortages_assistant.repositories.PharmacyRepository;
import com.cmcilroy.medicines_shortages_assistant.services.PharmacyService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc 
public class PharmacyControllerIntegrationTests {

    // inject MockMvc
    private MockMvc mockMvc;

    // and ObjectMapper
    private ObjectMapper objectMapper;

    // and Repository
    private PharmacyRepository pharmacyRepository;

    // and DatabaseCleaner 
    private DatabaseCleaner databaseCleaner;

    // and PasswordEncoder 
    private PasswordEncoder passwordEncoder;

    // mock PharmacyService
    @MockitoBean
    private PharmacyService mockPharmacyService;

    @Autowired
    public PharmacyControllerIntegrationTests(
        MockMvc mockMvc, PharmacyService pharmacyService, PharmacyRepository pharmacyRepository, 
        DatabaseCleaner databaseCleaner, PasswordEncoder passwordEncoder
    ) {
        this.mockMvc = mockMvc;
        this.objectMapper = new ObjectMapper();
        this.pharmacyRepository = pharmacyRepository;
        this.databaseCleaner = databaseCleaner;
        this.passwordEncoder = passwordEncoder;
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

/////////////////////////////////////// checkAuthentication METHOD TESTS /////////////////////////////////////////////////////////

    // test for checkAuthentication() method when user is authenticated
    @Test
    public void testCheckAuthenticationHasAuthentication() throws Exception {
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
        mockMvc.perform(MockMvcRequestBuilders.get("/pharmacies/check-auth")
                .accept(MediaType.APPLICATION_JSON))
                // expect HTTP Status 200 OK
                .andExpect(status().isOk())
                // expect authentication to be true
                .andExpect(jsonPath("$.authenticated", is(true))) 
                // expect pharmacy details to match those of the test pharmacy
                .andExpect(jsonPath("$.pharmacyName", is(pharmacyDto.getPharmacyName())))
                .andExpect(jsonPath("$.psiRegNo", is(pharmacyDto.getPsiRegNo())))
                .andExpect(jsonPath("$.address", is(pharmacyDto.getAddress())))
                .andExpect(jsonPath("$.eircode", is(pharmacyDto.getEircode())));

    }

    // test for checkAuthentication() method when user is not authenticated
    @Test
    public void testCheckAuthenticationNoAuthentication() throws Exception {

        // perform the GET request and validate the response
        mockMvc.perform(MockMvcRequestBuilders.get("/pharmacies/check-auth")
                .accept(MediaType.APPLICATION_JSON))
                // expect HTTP Status 200 OK
                .andExpect(status().isOk())
                // expect authentication to be false
                .andExpect(jsonPath("$.authenticated", is(false)));
    }

/////////////////////////////////////// registerNewPharmacy METHOD TESTS /////////////////////////////////////////////////////////

    // test for registerNewPharmacy() method where user enters a valid PSI registration number
    @Test
    public void testRegisterNewPharmacyValidPsiRegNo() throws Exception {
        // create test pharmacyDto
        PharmacyDto pharmacyDto = TestData.createTestPharmacyDtoA();
        // set PSI reg no to a known valid PSI reg no
        pharmacyDto.setPsiRegNo(6099);

        // declare JSON string version of pharmacyDto
        String pharmacyJson = objectMapper.writeValueAsString(pharmacyDto);

        // perform POST request and validate the response
        mockMvc.perform(post("/pharmacies/register")
                .contentType(MediaType.APPLICATION_JSON)
                // pass in JSON string version of pharmacyDto
                .content(pharmacyJson)
                .accept(MediaType.APPLICATION_JSON))
                // expect HTTP Status 201 created
                .andExpect(status().isCreated())
                // expect that the pharmacyDto is returned in the response
                .andExpect(jsonPath("$.psiRegNo", is(pharmacyDto.getPsiRegNo())))
                .andExpect(jsonPath("$.pharmacyName", is(pharmacyDto.getPharmacyName())))
                .andExpect(jsonPath("$.address", is(pharmacyDto.getAddress())))
                .andExpect(jsonPath("$.eircode", is(pharmacyDto.getEircode())))
                .andExpect(jsonPath("$.phoneNo", is(pharmacyDto.getPhoneNo())))
                .andExpect(jsonPath("$.email", is(pharmacyDto.getEmail())))
                .andExpect(jsonPath("$.password", is(pharmacyDto.getPassword())));

    }

    // test for registerNewPharmacy() method where user enters an invalid PSI registration number
    @Test
    public void testRegisterNewPharmacyInvalidPsiRegNo() throws Exception {
        // create test pharmacyDto
        PharmacyDto pharmacyDto = TestData.createTestPharmacyDtoA();
        // PSI reg No of pharmacyDto is not a valid PSI reg no

        // declare JSON string version of pharmacyDto
        String pharmacyJson = objectMapper.writeValueAsString(pharmacyDto);

        // perform POST request and validate the response
        mockMvc.perform(post("/pharmacies/register")
                .contentType(MediaType.APPLICATION_JSON)
                // pass in JSON string version of pharmacyDto
                .content(pharmacyJson)
                .accept(MediaType.APPLICATION_JSON))
                // expect HTTP Status 400 Bad Request
                .andExpect(status().isBadRequest())
                // expect that the response is the error message
                .andExpect(jsonPath("$.error", is("Invalid PSI registration number.")));

    }

    // test for registerNewPharmacy() method where account already exists
    @Test
    public void testRegisterNewPharmacyAccountAlreadyExists() throws Exception {
        // create test pharmacyDto and corresponding entity
        PharmacyDto pharmacyDto = TestData.createTestPharmacyDtoA();
        PharmacyEntity pharmacyEntity = TestData.createTestPharmacyA();
        // persist the entity in the database
        pharmacyRepository.save(pharmacyEntity);

        // declare JSON string version of pharmacyDto
        String pharmacyJson = objectMapper.writeValueAsString(pharmacyDto);

        // perform POST request and validate the response
        mockMvc.perform(post("/pharmacies/register")
                .contentType(MediaType.APPLICATION_JSON)
                // pass in JSON string version of pharmacyDto
                .content(pharmacyJson)
                .accept(MediaType.APPLICATION_JSON))
                // expect HTTP Status 400 Bad Request
                .andExpect(status().isBadRequest())
                // expect that the response is the error message
                .andExpect(jsonPath("$.error", is("This account already exists.")));

    }

/////////////////////////////////////// signIn METHOD TESTS /////////////////////////////////////////////////////////

    // test for signIn() method where user enters valid account credentials
    @Test
    public void testSignInValidCredentials() throws Exception {
        // create test account credentials
        AccountCredentialsDto accountCredentials = new AccountCredentialsDto("test@email.com", "password");
        // create test pharmacyEntity and persist it in the database
        PharmacyEntity pharmacyEntity = TestData.createTestPharmacyA();
        pharmacyEntity.setPassword(passwordEncoder.encode(pharmacyEntity.getPassword()));
        pharmacyRepository.save(pharmacyEntity);

        // declare JSON string version of account credentials
        String credentialsJson = objectMapper.writeValueAsString(accountCredentials);

        // perform POST request and validate the response
        mockMvc.perform(post("/pharmacies/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                // pass in JSON string version of account credentials
                .content(credentialsJson)
                .accept(MediaType.APPLICATION_JSON))
                // expect HTTP Status 200 OK
                .andExpect(status().isOk())
                // expect that the pharmacyDto corresponding to pharmacyEntity is returned in the response
                .andExpect(jsonPath("$.psiRegNo", is(pharmacyEntity.getPsiRegNo())))
                .andExpect(jsonPath("$.pharmacyName", is(pharmacyEntity.getPharmacyName())))
                .andExpect(jsonPath("$.address", is(pharmacyEntity.getAddress())))
                .andExpect(jsonPath("$.eircode", is(pharmacyEntity.getEircode())))
                .andExpect(jsonPath("$.phoneNo", is(pharmacyEntity.getPhoneNo())))
                .andExpect(jsonPath("$.email", is(pharmacyEntity.getEmail())));

    }

    // test for signIn() method where user enters invalid email address
    @Test
    public void testSignInInvalidEmail() throws Exception {
        // create test account credentials
        AccountCredentialsDto accountCredentials = new AccountCredentialsDto("invalidEmail@email.com", "password");
        // create test pharmacyEntity and persist it in the database
        PharmacyEntity pharmacyEntity = TestData.createTestPharmacyA();
        pharmacyEntity.setPassword(passwordEncoder.encode(pharmacyEntity.getPassword()));
        pharmacyRepository.save(pharmacyEntity);

        // declare JSON string version of account credentials
        String credentialsJson = objectMapper.writeValueAsString(accountCredentials);

        // perform POST request and validate the response
        mockMvc.perform(post("/pharmacies/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                // pass in JSON string version of account credentials
                .content(credentialsJson)
                .accept(MediaType.APPLICATION_JSON))
                // expect HTTP Status 404 Not Found
                .andExpect(status().isNotFound())
                // expect that the response is an empty string
                .andExpect(content().string(""));

    }

    // test for signIn() method where user enters invalid password
    @Test
    public void testSignInInvalidPassword() throws Exception {
        // create test account credentials
        AccountCredentialsDto accountCredentials = new AccountCredentialsDto("test@email.com", "InvalidPassword");

        // create test pharmacyEntity and persist it in the database
        PharmacyEntity pharmacyEntity = TestData.createTestPharmacyA();
        pharmacyEntity.setPassword(passwordEncoder.encode(pharmacyEntity.getPassword()));
        pharmacyRepository.save(pharmacyEntity);

        // declare JSON string version of account credentials
        String credentialsJson = objectMapper.writeValueAsString(accountCredentials);

        // perform POST request and validate the response
        mockMvc.perform(post("/pharmacies/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                // pass in JSON string version of account credentials
                .content(credentialsJson)
                .accept(MediaType.APPLICATION_JSON))
                // expect HTTP Status 401 Unauthorized
                .andExpect(status().isUnauthorized())
                // expect that the response is an empty string
                .andExpect(content().string(""));
        
    }

/////////////////////////////////////// updatePharmacy METHOD TESTS /////////////////////////////////////////////////////////

    // test for updatePharmacy() method where update is successful
    @Test
    public void testUpdatePharmacyWhereUpdateSuccessful() throws Exception {
        // create test existing pharmacyDto
        PharmacyDto existingPharmacyDto = TestData.createTestPharmacyDtoA();
        // create test existing pharmacyEntity and persist it in the database
        PharmacyEntity existingPharmacyEntity = TestData.createTestPharmacyA();
        pharmacyRepository.save(existingPharmacyEntity);
        // create test updatePharmacyDto
        PharmacyDto updatePharmacyDto = TestData.createTestPharmacyDtoA();
        updatePharmacyDto.setPharmacyName("Updated Name");
        updatePharmacyDto.setPhoneNo("Updated Phone Number");
        updatePharmacyDto.setEmail("UpdatedEmail@email.com");
        updatePharmacyDto.setPassword("UpdatedPassword");
        // create test editAccountDto
        EditAccountDto editAccountDto = new EditAccountDto(updatePharmacyDto, existingPharmacyDto.getPassword());

        // create test authentication
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
            existingPharmacyDto, 
            null, 
            List.of(new SimpleGrantedAuthority("ROLE_PHARMACY_USER"))
        );
        // store authentication in the SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // declare JSON string version of editAccountDto
        String editAccountJson = objectMapper.writeValueAsString(editAccountDto);

        // perform PATCH request and validate the response
        mockMvc.perform(patch("/pharmacies/edit-account-details")
                .contentType(MediaType.APPLICATION_JSON)
                // pass in JSON string version of editAccountDto
                .content(editAccountJson)
                .accept(MediaType.APPLICATION_JSON))
                // expect HTTP Status 200 OK
                .andExpect(status().isOk())
                // expect that the updatePharmacyDto is returned in the response
                .andExpect(jsonPath("$.psiRegNo", is(updatePharmacyDto.getPsiRegNo())))
                .andExpect(jsonPath("$.pharmacyName", is(updatePharmacyDto.getPharmacyName())))
                .andExpect(jsonPath("$.address", is(updatePharmacyDto.getAddress())))
                .andExpect(jsonPath("$.eircode", is(updatePharmacyDto.getEircode())))
                .andExpect(jsonPath("$.phoneNo", is(updatePharmacyDto.getPhoneNo())))
                .andExpect(jsonPath("$.email", is(updatePharmacyDto.getEmail())))
                .andExpect(jsonPath("$.password", is(updatePharmacyDto.getPassword())));

    }

    // test for updatePharmacy() method where the PSI number of the current signed in user does not match that of the record to be updated
    @Test
    public void testUpdatePharmacyInvalidPsiRegNo() throws Exception {
        // create test existing pharmacyDto
        PharmacyDto existingPharmacyDto = TestData.createTestPharmacyDtoA();
        // create test existing pharmacyEntity and persist it in the database
        PharmacyEntity existingPharmacyEntity = TestData.createTestPharmacyA();
        pharmacyRepository.save(existingPharmacyEntity);
        // create different test updatePharmacyDto
        PharmacyDto updatePharmacyDto = TestData.createTestPharmacyDtoB();
        // create test editAccountDto
        EditAccountDto editAccountDto = new EditAccountDto(updatePharmacyDto, existingPharmacyDto.getPassword());

        // create test authentication
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
            existingPharmacyDto, 
            null, 
            List.of(new SimpleGrantedAuthority("ROLE_PHARMACY_USER"))
        );
        // store authentication in the SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // declare JSON string version of editAccountDto
        String editAccountJson = objectMapper.writeValueAsString(editAccountDto);

        // perform PATCH request and validate the response
        mockMvc.perform(patch("/pharmacies/edit-account-details")
                .contentType(MediaType.APPLICATION_JSON)
                // pass in JSON string version of editAccountDto
                .content(editAccountJson)
                .accept(MediaType.APPLICATION_JSON))
                // expect HTTP Status 403 Forbidden
                .andExpect(status().isForbidden())
                // expect that the response is an empty string
                .andExpect(content().string(""));

    }

    // test for updatePharmacy() method where the email of the current authenticated user is not found in the database
    @Test
    public void testUpdatePharmacyAccountNotFound() throws Exception {
        // create test existing pharmacyDto
        PharmacyDto existingPharmacyDto = TestData.createTestPharmacyDtoA();
        // create test updatePharmacyDto
        PharmacyDto updatePharmacyDto = TestData.createTestPharmacyDtoA();
        updatePharmacyDto.setPharmacyName("Updated Name");
        updatePharmacyDto.setPhoneNo("Updated Phone Number");
        updatePharmacyDto.setEmail("UpdatedEmail@email.com");
        updatePharmacyDto.setPassword("UpdatedPassword");
        // create test editAccountDto
        EditAccountDto editAccountDto = new EditAccountDto(updatePharmacyDto, existingPharmacyDto.getPassword());

        // create test authentication
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
            existingPharmacyDto, 
            null, 
            List.of(new SimpleGrantedAuthority("ROLE_PHARMACY_USER"))
        );
        // store authentication in the SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // declare JSON string version of editAccountDto
        String editAccountJson = objectMapper.writeValueAsString(editAccountDto);

        // perform PATCH request and validate the response
        mockMvc.perform(patch("/pharmacies/edit-account-details")
                .contentType(MediaType.APPLICATION_JSON)
                // pass in JSON string version of editAccountDto
                .content(editAccountJson)
                .accept(MediaType.APPLICATION_JSON))
                // expect HTTP Status 404 Not Found
                .andExpect(status().isNotFound())
                // expect that the response is an empty string
                .andExpect(content().string(""));

    }

    // test for updatePharmacy() method where the current password entered by the authenticated user is incorrect
    @Test
    public void testUpdatePharmacyIncorrectPassword() throws Exception {
        // create test existing pharmacyDto
        PharmacyDto existingPharmacyDto = TestData.createTestPharmacyDtoA();
        // create test existing pharmacyEntity and persist it in the database
        PharmacyEntity existingPharmacyEntity = TestData.createTestPharmacyA();
        pharmacyRepository.save(existingPharmacyEntity);
        // create test updatePharmacyDto
        PharmacyDto updatePharmacyDto = TestData.createTestPharmacyDtoA();
        updatePharmacyDto.setPharmacyName("Updated Name");
        updatePharmacyDto.setPhoneNo("Updated Phone Number");
        updatePharmacyDto.setEmail("UpdatedEmail@email.com");
        updatePharmacyDto.setPassword("UpdatedPassword");
        // create test editAccountDto
        EditAccountDto editAccountDto = new EditAccountDto(updatePharmacyDto, "IncorrectPassword");

        // create test authentication
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
            existingPharmacyDto, 
            null, 
            List.of(new SimpleGrantedAuthority("ROLE_PHARMACY_USER"))
        );
        // store authentication in the SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // declare JSON string version of editAccountDto
        String editAccountJson = objectMapper.writeValueAsString(editAccountDto);

        // perform PATCH request and validate the response
        mockMvc.perform(patch("/pharmacies/edit-account-details")
                .contentType(MediaType.APPLICATION_JSON)
                // pass in JSON string version of editAccountDto
                .content(editAccountJson)
                .accept(MediaType.APPLICATION_JSON))
                // expect HTTP Status 401 Unauthorized
                .andExpect(status().isUnauthorized())
                // expect that the response is an empty string
                .andExpect(content().string(""));

    }

/////////////////////////////////////// deletePharmacy METHOD TESTS /////////////////////////////////////////////////////////

    // test for deletePharmacy() method where deletion is successful
    @Test
    public void testDeletePharmacyWhereDeletionSuccessful() throws Exception {
        // create test pharmacyDto and corresponding pharmacyEntity
        PharmacyDto pharmacyDto = TestData.createTestPharmacyDtoA();
        PharmacyEntity pharmacyEntity = TestData.createTestPharmacyA();
        // encode password of entity and persist the entity in the database
        pharmacyEntity.setPassword(passwordEncoder.encode(pharmacyEntity.getPassword()));
        pharmacyRepository.save(pharmacyEntity);

        // create test authentication
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
            pharmacyDto, 
            null, 
            List.of(new SimpleGrantedAuthority("ROLE_PHARMACY_USER"))
        );
        // store authentication in the SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // create test request body
        Map<String, String> requestBody = Map.of("password", pharmacyDto.getPassword());

        // declare JSON string version of test request body
        String requestBodyJson = objectMapper.writeValueAsString(requestBody);

        // perform DELETE request and validate the response
        mockMvc.perform(delete("/pharmacies/delete-account")
                .contentType(MediaType.APPLICATION_JSON)
                // pass in JSON string version of editAccountDto
                .content(requestBodyJson)
                .accept(MediaType.APPLICATION_JSON))
                // expect HTTP Status 204 No Content
                .andExpect(status().isNoContent())
                // expect that the response is an empty string
                .andExpect(content().string(""));

    }

    // test for deletePharmacy() method where account is not found
    @Test
    public void testDeletePharmacyAccountNotFound() throws Exception {
        // create test pharmacyDto
        PharmacyDto pharmacyDto = TestData.createTestPharmacyDtoA();
        // do not persist it in the database

        // create test authentication
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
            pharmacyDto, 
            null, 
            List.of(new SimpleGrantedAuthority("ROLE_PHARMACY_USER"))
        );
        // store authentication in the SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // create test request body
        Map<String, String> requestBody = Map.of("password", pharmacyDto.getPassword());

        // declare JSON string version of test request body
        String requestBodyJson = objectMapper.writeValueAsString(requestBody);

        // perform DELETE request and validate the response
        mockMvc.perform(delete("/pharmacies/delete-account")
                .contentType(MediaType.APPLICATION_JSON)
                // pass in JSON string version of editAccountDto
                .content(requestBodyJson)
                .accept(MediaType.APPLICATION_JSON))
                // expect HTTP Status 404 Not Found
                .andExpect(status().isNotFound())
                // expect that the response is an empty string
                .andExpect(content().string(""));

    }

    // test for deletePharmacy() method where incorrect password is entered
    @Test
    public void testDeletePharmacyIncorrectPassword() throws Exception {
        // create test pharmacyDto and corresponding pharmacyEntity
        PharmacyDto pharmacyDto = TestData.createTestPharmacyDtoA();
        PharmacyEntity pharmacyEntity = TestData.createTestPharmacyA();
        // encode password of entity and persist the entity in the database
        pharmacyEntity.setPassword(passwordEncoder.encode(pharmacyEntity.getPassword()));
        pharmacyRepository.save(pharmacyEntity);

        // create test authentication
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
            pharmacyDto, 
            null, 
            List.of(new SimpleGrantedAuthority("ROLE_PHARMACY_USER"))
        );
        // store authentication in the SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // create test request body with incorrect password
        Map<String, String> requestBody = Map.of("password", "Incorrect Password");

        // declare JSON string version of test request body
        String requestBodyJson = objectMapper.writeValueAsString(requestBody);

        // perform DELETE request and validate the response
        mockMvc.perform(delete("/pharmacies/delete-account")
                .contentType(MediaType.APPLICATION_JSON)
                // pass in JSON string version of editAccountDto
                .content(requestBodyJson)
                .accept(MediaType.APPLICATION_JSON))
                // expect HTTP Status 401 Unauthorized
                .andExpect(status().isUnauthorized())
                // expect that the response is an empty string
                .andExpect(content().string(""));

    }

}