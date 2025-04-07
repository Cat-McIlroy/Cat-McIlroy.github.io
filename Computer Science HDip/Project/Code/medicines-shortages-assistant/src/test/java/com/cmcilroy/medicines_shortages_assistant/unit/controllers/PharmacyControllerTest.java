package com.cmcilroy.medicines_shortages_assistant.unit.controllers;

import com.cmcilroy.medicines_shortages_assistant.TestData;
import com.cmcilroy.medicines_shortages_assistant.controllers.PharmacyController;
import com.cmcilroy.medicines_shortages_assistant.domain.dto.AccountCredentialsDto;
import com.cmcilroy.medicines_shortages_assistant.domain.dto.EditAccountDto;
import com.cmcilroy.medicines_shortages_assistant.domain.dto.PharmacyDto;
import com.cmcilroy.medicines_shortages_assistant.domain.entities.PharmacyEntity;
import com.cmcilroy.medicines_shortages_assistant.services.PharmacyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.cmcilroy.medicines_shortages_assistant.mappers.Mapper;
import com.cmcilroy.medicines_shortages_assistant.paginators.Paginator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.hamcrest.Matchers.*;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;

import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class PharmacyControllerTest {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private PharmacyService pharmacyService;

    @Mock
    private Mapper<PharmacyEntity, PharmacyDto> pharmacyMapper;

    @Mock
    private Paginator<PharmacyDto> paginator;

    @InjectMocks
    private PharmacyController pharmacyController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(pharmacyController)
        .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
        .build();
    }

/////////////////////////////////////// checkAuthentication METHOD TESTS /////////////////////////////////////////////////////////

    // test for checkAuthentication() method when user is authenticated
    @Test
    public void testCheckAuthenticationHasAuthentication() throws Exception {
        // create test pharmacyDto
        PharmacyDto pharmacyDto = TestData.createTestPharmacyDtoA();
        // mock authentication
        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(pharmacyDto);
        // set test security context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // perform GET request and validate the response
        mockMvc.perform(MockMvcRequestBuilders.get("/pharmacies/check-auth")
                .accept(MediaType.APPLICATION_JSON))
                // expect HTTP Status 200 OK
                .andExpect(status().isOk())
                // expect authentication to be true
                .andExpect(jsonPath("$.authenticated", is(true))) 
                // expect pharmacy details to match those of the test pharmacy
                .andExpect(jsonPath("$.pharmacyName", is("Pharmacy A")))
                .andExpect(jsonPath("$.psiRegNo", is(1234)))
                .andExpect(jsonPath("$.address", is("Test Address")))
                .andExpect(jsonPath("$.eircode", is("AAAAAAA")));
    }

    // test for checkAuthentication() method when user is not authenticated
    @Test
    public void testCheckAuthenticationNoAuthentication() throws Exception {
        // set test security context to null authentication
        SecurityContextHolder.getContext().setAuthentication(null);

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
        // create test pharmacyEntity and test pharmacyDto
        PharmacyEntity pharmacyA = TestData.createTestPharmacyA();
        PharmacyDto pharmacyADto= TestData.createTestPharmacyDtoA();

        // declare JSON string version of pharmacyDto
        String pharmacyJson = objectMapper.writeValueAsString(pharmacyADto);

        // mock mapper to convert PharmacyDto to PharmacyEntity, check it returns the corresponding PharmacyEntity
        when(pharmacyMapper.mapFrom(pharmacyADto)).thenReturn(pharmacyA);

        // mock service call, expect it returns an identical pharmacyEntity
        when(pharmacyService.registerNewPharmacy(pharmacyA)).thenReturn(pharmacyA);

        // mock mapper to convert PharmacyEntity back to PharmacyDto, check it returns the corresponding PharmacyDto
        when(pharmacyMapper.mapTo(pharmacyA)).thenReturn(pharmacyADto);

        // perform POST request and validate the response
        mockMvc.perform(post("/pharmacies/register")
                .contentType(MediaType.APPLICATION_JSON)
                // pass in JSON string version of pharmacyDto
                .content(pharmacyJson)
                .accept(MediaType.APPLICATION_JSON))
                // expect HTTP Status 201 created
                .andExpect(status().isCreated())
                // expect that the pharmacyDto is returned in the response
                .andExpect(jsonPath("$.psiRegNo", is(1234)))
                .andExpect(jsonPath("$.pharmacyName", is("Pharmacy A")))
                .andExpect(jsonPath("$.address", is("Test Address")))
                .andExpect(jsonPath("$.eircode", is("AAAAAAA")))
                .andExpect(jsonPath("$.phoneNo", is("0123456789")))
                .andExpect(jsonPath("$.email", is("test@email.com")))
                .andExpect(jsonPath("$.password", is("password")));

        // expect that the registerNewPharmacy method was called once with the specified parameter
        verify(pharmacyService, times(1)).registerNewPharmacy(pharmacyA);
        // expect that the pharmacyMapper method was called once with each specified parameter
        verify(pharmacyMapper, times(1)).mapFrom(pharmacyADto);
        verify(pharmacyMapper, times(1)).mapTo(pharmacyA);
    }

    // test for registerNewPharmacy() method where user enters an invalid PSI registration number
    @Test
    public void testRegisterNewPharmacyInvalidPsiRegNo() throws Exception {
        // create test pharmacyEntity and test pharmacyDto
        PharmacyEntity pharmacyA = TestData.createTestPharmacyA();
        PharmacyDto pharmacyADto= TestData.createTestPharmacyDtoA();

        // declare JSON string version of pharmacyDto
        String pharmacyJson = objectMapper.writeValueAsString(pharmacyADto);

        // mock mapper to convert PharmacyDto to PharmacyEntity, check it returns the corresponding PharmacyEntity
        when(pharmacyMapper.mapFrom(pharmacyADto)).thenReturn(pharmacyA);

        // mock service call, expect it throws an IllegalArgumentException with corresponding error message
        when(pharmacyService.registerNewPharmacy(pharmacyA)).thenThrow(new IllegalArgumentException("Invalid PSI registration number."));

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



        // expect that the registerNewPharmacy method was called once with the specified parameter
        verify(pharmacyService, times(1)).registerNewPharmacy(pharmacyA);
        // expect that the pharmacyMapper method was called once with the specified parameter
        verify(pharmacyMapper, times(1)).mapFrom(pharmacyADto);
        // expect that the pharmacyMapper method was never called with the following parameter
        verify(pharmacyMapper, never()).mapTo(pharmacyA);
    }

    // test for registerNewPharmacy() method where account already exists
    @Test
    public void testRegisterNewPharmacyAccountAlreadyExists() throws Exception {
        // create test pharmacyEntity and test pharmacyDto
        PharmacyEntity pharmacyA = TestData.createTestPharmacyA();
        PharmacyDto pharmacyADto= TestData.createTestPharmacyDtoA();

        // declare JSON string version of pharmacyDto
        String pharmacyJson = objectMapper.writeValueAsString(pharmacyADto);

        // mock mapper to convert PharmacyDto to PharmacyEntity, check it returns the corresponding PharmacyEntity
        when(pharmacyMapper.mapFrom(pharmacyADto)).thenReturn(pharmacyA);

        // mock service call, expect it throws an IllegalArgumentException with corresponding error message
        when(pharmacyService.registerNewPharmacy(pharmacyA)).thenThrow(new IllegalArgumentException("This account already exists."));

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



        // expect that the registerNewPharmacy method was called once with the specified parameter
        verify(pharmacyService, times(1)).registerNewPharmacy(pharmacyA);
        // expect that the pharmacyMapper method was called once with the specified parameter
        verify(pharmacyMapper, times(1)).mapFrom(pharmacyADto);
        // expect that the pharmacyMapper method was never called with the following parameter
        verify(pharmacyMapper, never()).mapTo(pharmacyA);
    }

/////////////////////////////////////// signIn METHOD TESTS /////////////////////////////////////////////////////////

    // test for signIn() method where user enters valid account credentials
    @Test
    public void testSignInValidCredentials() throws Exception {
        // create test account credentials
        AccountCredentialsDto accountCredentials = new AccountCredentialsDto("test@email.com", "password");
        // create test pharmacyEntity and test pharmacyDto
        PharmacyEntity pharmacyA = TestData.createTestPharmacyA();
        PharmacyDto pharmacyADto= TestData.createTestPharmacyDtoA();

        // mock service call, expect it to return an Optional containing the test pharmacy
        when(pharmacyService.signIn(accountCredentials)).thenReturn(Optional.of(pharmacyA));

        // mock mapper to convert PharmacyEntity to PharmacyDto, check it returns the corresponding PharmacyDto
        when(pharmacyMapper.mapTo(pharmacyA)).thenReturn(pharmacyADto);

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
                // expect that the pharmacyDto is returned in the response
                .andExpect(jsonPath("$.psiRegNo", is(1234)))
                .andExpect(jsonPath("$.pharmacyName", is("Pharmacy A")))
                .andExpect(jsonPath("$.address", is("Test Address")))
                .andExpect(jsonPath("$.eircode", is("AAAAAAA")))
                .andExpect(jsonPath("$.phoneNo", is("0123456789")))
                .andExpect(jsonPath("$.email", is("test@email.com")))
                .andExpect(jsonPath("$.password", is("password")));

        // expect that the signIn method was called once with the specified parameter
        verify(pharmacyService, times(1)).signIn(accountCredentials);
        // expect that the pharmacyMapper method was called once with the specified parameter
        verify(pharmacyMapper, times(1)).mapTo(pharmacyA);
    }

    // test for signIn() method where user enters invalid email address
    @Test
    public void testSignInInvalidEmail() throws Exception {
        // create test account credentials
        AccountCredentialsDto accountCredentials = new AccountCredentialsDto("invalidEmail@email.com", "password");

        // mock service call, expect it to throw a UsernameNotFoundException
        when(pharmacyService.signIn(accountCredentials)).thenThrow(new UsernameNotFoundException("No account found with this email."));

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

        // expect that the signIn method was called once with the specified parameter
        verify(pharmacyService, times(1)).signIn(accountCredentials);
        // expect that the pharmacyMapper method was never called
        verify(pharmacyMapper, never()).mapTo(any());

    }

    // test for signIn() method where user enters invalid password
    @Test
    public void testSignInInvalidPassword() throws Exception {
        // create test account credentials
        AccountCredentialsDto accountCredentials = new AccountCredentialsDto("test@email.com", "InvalidPassword");

        // mock service call, expect it to throw a BadCredentialsException
        when(pharmacyService.signIn(accountCredentials)).thenThrow(new BadCredentialsException("Incorrect password. Please try again."));

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

        // expect that the signIn method was called once with the specified parameter
        verify(pharmacyService, times(1)).signIn(accountCredentials);
        // expect that the pharmacyMapper method was never called
        verify(pharmacyMapper, never()).mapTo(any());
        
    }

/////////////////////////////////////// updatePharmacy METHOD TESTS /////////////////////////////////////////////////////////

    // test for updatePharmacy() method where update is successful
    @Test
    public void testUpdatePharmacyWhereUpdateSuccessful() throws Exception {
        // create test existing pharmacyDto
        PharmacyDto existingPharmacyDto = TestData.createTestPharmacyDtoA();
        // create test updatePharmacyDto
        PharmacyDto updatePharmacyDto = TestData.createTestPharmacyDtoA();
        updatePharmacyDto.setPharmacyName("Updated Name");
        updatePharmacyDto.setPhoneNo("Updated Phone Number");
        updatePharmacyDto.setEmail("UpdatedEmail@email.com");
        updatePharmacyDto.setPassword("UpdatedPassword");
        // create test updatePharmacyEntity
        PharmacyEntity updatePharmacyEntity = TestData.createTestPharmacyA();
        updatePharmacyEntity.setPharmacyName("Updated Name");
        updatePharmacyEntity.setPhoneNo("Updated Phone Number");
        updatePharmacyEntity.setEmail("UpdatedEmail@email.com");
        updatePharmacyEntity.setPassword("UpdatedPassword");
        // create test editAccountDto
        EditAccountDto editAccountDto = new EditAccountDto(updatePharmacyDto, existingPharmacyDto.getPassword());

        // mock authentication
        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(existingPharmacyDto);
        // set test security context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // mock mapper to convert updatedPharmacyDto to updatedPharmacyEntity, check it returns the corresponding PharmacyEntity
        when(pharmacyMapper.mapFrom(updatePharmacyDto)).thenReturn(updatePharmacyEntity);

        // mock service call, expect it returns an identical pharmacyEntity
        when(pharmacyService.updatePharmacy(editAccountDto.getCurrentPassword(), updatePharmacyEntity)).thenReturn(updatePharmacyEntity);

        // mock mapper to convert PharmacyEntity back to PharmacyDto, check it returns the corresponding PharmacyDto
        when(pharmacyMapper.mapTo(updatePharmacyEntity)).thenReturn(updatePharmacyDto);

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
                .andExpect(jsonPath("$.psiRegNo", is(1234)))
                .andExpect(jsonPath("$.pharmacyName", is("Updated Name")))
                .andExpect(jsonPath("$.address", is("Test Address")))
                .andExpect(jsonPath("$.eircode", is("AAAAAAA")))
                .andExpect(jsonPath("$.phoneNo", is("Updated Phone Number")))
                .andExpect(jsonPath("$.email", is("UpdatedEmail@email.com")))
                .andExpect(jsonPath("$.password", is("UpdatedPassword")));

        // expect that the updatePharmacy method was called once with the specified parameters
        verify(pharmacyService, times(1)).updatePharmacy(editAccountDto.getCurrentPassword(), updatePharmacyEntity);
        // expect that the pharmacyMapper method was called once with each specified parameter
        verify(pharmacyMapper, times(1)).mapFrom(updatePharmacyDto);
        verify(pharmacyMapper, times(1)).mapTo(updatePharmacyEntity);
    }

    // test for updatePharmacy() method where the PSI number of the current signed in user does not match that of the record to be updated
    @Test
    public void testUpdatePharmacyInvalidPsiRegNo() throws Exception {
        // create test existing pharmacyDto
        PharmacyDto existingPharmacyDto = TestData.createTestPharmacyDtoA();
        // create different test updatePharmacyDto
        PharmacyDto updatePharmacyDto = TestData.createTestPharmacyDtoB();
        // create test editAccountDto
        EditAccountDto editAccountDto = new EditAccountDto(updatePharmacyDto, existingPharmacyDto.getPassword());

        // mock authentication
        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(existingPharmacyDto);
        // set test security context
        SecurityContextHolder.getContext().setAuthentication(authentication);

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

        // expect that the updatePharmacy method was never called
        verify(pharmacyService, never()).updatePharmacy(anyString(), any());
        // expect that the pharmacyMapper method was never called
        verify(pharmacyMapper, never()).mapFrom(updatePharmacyDto);
        verify(pharmacyMapper, never()).mapTo(any());
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
        // create corresponding test updatePharmacyEntity
        PharmacyEntity updatePharmacyEntity = TestData.createTestPharmacyA();
        updatePharmacyEntity.setPharmacyName("Updated Name");
        updatePharmacyEntity.setPhoneNo("Updated Phone Number");
        updatePharmacyEntity.setEmail("UpdatedEmail@email.com");
        updatePharmacyEntity.setPassword("UpdatedPassword");
        // create test editAccountDto
        EditAccountDto editAccountDto = new EditAccountDto(updatePharmacyDto, existingPharmacyDto.getPassword());

        // mock authentication
        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(existingPharmacyDto);
        // set test security context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // mock mapper to convert updatePharmacyDto to updatePharmacyEntity, check it returns the corresponding PharmacyEntity
        when(pharmacyMapper.mapFrom(updatePharmacyDto)).thenReturn(updatePharmacyEntity);

        // mock service call, expect it returns an identical pharmacyEntity
        when(pharmacyService.updatePharmacy(editAccountDto.getCurrentPassword(), updatePharmacyEntity)).
            thenThrow(new UsernameNotFoundException("No account found."));

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

        // expect that the updatePharmacy method was called once with the specified parameters
        verify(pharmacyService, times(1)).updatePharmacy(editAccountDto.getCurrentPassword(), updatePharmacyEntity);
        // expect that the pharmacyMapper method was called once with the specified parameter
        verify(pharmacyMapper, times(1)).mapFrom(updatePharmacyDto);
        // expect that the pharmacyMapper method was never called with the following parameter
        verify(pharmacyMapper, never()).mapTo(updatePharmacyEntity);
    }

    // test for updatePharmacy() method where the current password entered by the authenticated user is incorrect
    @Test
    public void testUpdatePharmacyIncorrectPassword() throws Exception {
        // create test existing pharmacyDto
        PharmacyDto existingPharmacyDto = TestData.createTestPharmacyDtoA();
        // create test updatePharmacyDto
        PharmacyDto updatePharmacyDto = TestData.createTestPharmacyDtoA();
        updatePharmacyDto.setPharmacyName("Updated Name");
        updatePharmacyDto.setPhoneNo("Updated Phone Number");
        updatePharmacyDto.setEmail("UpdatedEmail@email.com");
        updatePharmacyDto.setPassword("UpdatedPassword");
        // create corresponding test updatePharmacyEntity
        PharmacyEntity updatePharmacyEntity = TestData.createTestPharmacyA();
        updatePharmacyEntity.setPharmacyName("Updated Name");
        updatePharmacyEntity.setPhoneNo("Updated Phone Number");
        updatePharmacyEntity.setEmail("UpdatedEmail@email.com");
        updatePharmacyEntity.setPassword("UpdatedPassword");
        // create test editAccountDto
        EditAccountDto editAccountDto = new EditAccountDto(updatePharmacyDto, existingPharmacyDto.getPassword());

        // mock authentication
        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(existingPharmacyDto);
        // set test security context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // mock mapper to convert updatePharmacyDto to updatePharmacyEntity, check it returns the corresponding PharmacyEntity
        when(pharmacyMapper.mapFrom(updatePharmacyDto)).thenReturn(updatePharmacyEntity);

        // mock service call, expect it returns an identical pharmacyEntity
        when(pharmacyService.updatePharmacy(editAccountDto.getCurrentPassword(), updatePharmacyEntity)).
            thenThrow(new BadCredentialsException("Incorrect password. Please try again."));

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

        // expect that the updatePharmacy method was called once with the specified parameters
        verify(pharmacyService, times(1)).updatePharmacy(editAccountDto.getCurrentPassword(), updatePharmacyEntity);
        // expect that the pharmacyMapper method was called once with the specified parameter
        verify(pharmacyMapper, times(1)).mapFrom(updatePharmacyDto);
        // expect that the pharmacyMapper method was never called with the following parameter
        verify(pharmacyMapper, never()).mapTo(updatePharmacyEntity);
    }

/////////////////////////////////////// deletePharmacy METHOD TESTS /////////////////////////////////////////////////////////

    // test for deletePharmacy() method where deletion is successful
    @Test
    public void testDeletePharmacyWhereDeletionSuccessful() throws Exception {
        // create test pharmacyDto and corresponding pharmacyEntity
        PharmacyDto pharmacyDto = TestData.createTestPharmacyDtoA();
        PharmacyEntity pharmacyEntity = TestData.createTestPharmacyA();

        // mock authentication
        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(pharmacyDto);
        // set test security context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // create test request body
        Map<String, String> requestBody = Map.of("password", pharmacyDto.getPassword());

        // declare JSON string version of test request body
        String requestBodyJson = objectMapper.writeValueAsString(requestBody);

        // mock mapper to convert pharmacyDto to pharmacyEntity, check it returns the corresponding PharmacyEntity
        when(pharmacyMapper.mapFrom(pharmacyDto)).thenReturn(pharmacyEntity);

        // mock service call, expect it returns void
        doNothing().when(pharmacyService).delete(requestBody.get("password"), pharmacyEntity);

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

        // expect that the delete method was called once with the specified parameters
        verify(pharmacyService, times(1)).delete(requestBody.get("password"), pharmacyEntity);
        // expect that the pharmacyMapper method was called once with the specified parameter
        verify(pharmacyMapper, times(1)).mapFrom(pharmacyDto);
        // expect that the pharmacyMapper method was never called with the following parameter
        verify(pharmacyMapper, never()).mapTo(pharmacyEntity);
    }

    // test for deletePharmacy() method where account is not found
    @Test
    public void testDeletePharmacyAccountNotFound() throws Exception {
        // create test pharmacyDto and corresponding pharmacyEntity
        PharmacyDto pharmacyDto = TestData.createTestPharmacyDtoA();
        PharmacyEntity pharmacyEntity = TestData.createTestPharmacyA();

        // mock authentication
        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(pharmacyDto);
        // set test security context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // create test request body
        Map<String, String> requestBody = Map.of("password", pharmacyDto.getPassword());

        // declare JSON string version of test request body
        String requestBodyJson = objectMapper.writeValueAsString(requestBody);

        // mock mapper to convert pharmacyDto to pharmacyEntity, check it returns the corresponding PharmacyEntity
        when(pharmacyMapper.mapFrom(pharmacyDto)).thenReturn(pharmacyEntity);

        // mock service call, expect it throws a UsernameNotFoundException
        doThrow(new UsernameNotFoundException("No account found.")).when(pharmacyService).delete(requestBody.get("password"), pharmacyEntity);

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

        // expect that the delete method was called once with the specified parameters
        verify(pharmacyService, times(1)).delete(requestBody.get("password"), pharmacyEntity);
        // expect that the pharmacyMapper method was called once with the specified parameter
        verify(pharmacyMapper, times(1)).mapFrom(pharmacyDto);
        // expect that the pharmacyMapper method was never called with the following parameter
        verify(pharmacyMapper, never()).mapTo(pharmacyEntity);
    }

    // test for deletePharmacy() method where incorrect password is entered
    @Test
    public void testDeletePharmacyIncorrectPassword() throws Exception {
        // create test pharmacyDto and corresponding pharmacyEntity
        PharmacyDto pharmacyDto = TestData.createTestPharmacyDtoA();
        PharmacyEntity pharmacyEntity = TestData.createTestPharmacyA();

        // mock authentication
        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(pharmacyDto);
        // set test security context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // create test request body
        Map<String, String> requestBody = Map.of("password", "Incorrect Password");

        // declare JSON string version of test request body
        String requestBodyJson = objectMapper.writeValueAsString(requestBody);

        // mock mapper to convert pharmacyDto to pharmacyEntity, check it returns the corresponding PharmacyEntity
        when(pharmacyMapper.mapFrom(pharmacyDto)).thenReturn(pharmacyEntity);

        // mock service call, expect it throws a BadCredentialsException
        doThrow(new BadCredentialsException("Incorrect password. Please try again.")).when(pharmacyService).delete(requestBody.get("password"), pharmacyEntity);

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

        // expect that the delete method was called once with the specified parameters
        verify(pharmacyService, times(1)).delete(requestBody.get("password"), pharmacyEntity);
        // expect that the pharmacyMapper method was called once with the specified parameter
        verify(pharmacyMapper, times(1)).mapFrom(pharmacyDto);
        // expect that the pharmacyMapper method was never called with the following parameter
        verify(pharmacyMapper, never()).mapTo(pharmacyEntity);
    }

}
