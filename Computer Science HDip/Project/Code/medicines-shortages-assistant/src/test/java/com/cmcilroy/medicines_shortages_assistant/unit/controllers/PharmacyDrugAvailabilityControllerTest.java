package com.cmcilroy.medicines_shortages_assistant.unit.controllers;

import com.cmcilroy.medicines_shortages_assistant.TestData;
import com.cmcilroy.medicines_shortages_assistant.controllers.PharmacyDrugAvailabilityController;
import com.cmcilroy.medicines_shortages_assistant.domain.dto.PharmacyDrugAvailabilityDto;
import com.cmcilroy.medicines_shortages_assistant.domain.dto.PharmacyDto;
import com.cmcilroy.medicines_shortages_assistant.domain.entities.DrugEntity;
import com.cmcilroy.medicines_shortages_assistant.domain.entities.PharmacyDrugAvailabilityEntity;
import com.cmcilroy.medicines_shortages_assistant.domain.entities.PharmacyEntity;
import com.cmcilroy.medicines_shortages_assistant.services.DrugService;
import com.cmcilroy.medicines_shortages_assistant.services.PharmacyDrugAvailabilityService;
import com.cmcilroy.medicines_shortages_assistant.mappers.Mapper;
import com.cmcilroy.medicines_shortages_assistant.paginators.Paginator;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.security.core.Authentication;
import static org.hamcrest.Matchers.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


public class PharmacyDrugAvailabilityControllerTest {

    @Mock
    private PharmacyDrugAvailabilityService availabilityService;

    @Mock
    private DrugService drugService;

    @Mock
    private Mapper<PharmacyDrugAvailabilityEntity, PharmacyDrugAvailabilityDto> availabilityMapper;

    @Mock
    private Mapper<PharmacyEntity, PharmacyDto> pharmacyMapper;

    @Mock
    private Paginator<PharmacyDrugAvailabilityDto> paginator;

    @InjectMocks
    private PharmacyDrugAvailabilityController availabilityController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(availabilityController)
        .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
        .build();
    }

    @AfterEach
    public void tearDown() {
        Mockito.reset(availabilityService);
        Mockito.reset(drugService);
    }

/////////////////////////////////////// listPharmacyDrugAvailabilitiesByDrug METHOD TESTS /////////////////////////////////////////////////////////

    // test for listPharmacyDrugAvailabilitiesByDrug() method when results are returned
    @Test
    public void testListPharmacyDrugAvailabilitiesByDrugHasResults() throws Exception {
        // create test licence number
        String licenceNo = "PA0749/067/001";
        // create test availabilityDto and corresponding availabilityEntity
        PharmacyDrugAvailabilityDto availabilityDto = TestData.createTestPharmacyDrugAvailabilityDtoA(
            TestData.createTestPharmacyDtoA(), TestData.createTestDrugDtoA()
        );
        PharmacyDrugAvailabilityEntity availabilityEntity = TestData.createTestPharmacyDrugAvailabilityA(
            TestData.createTestPharmacyA(), TestData.createTestDrugA()
        );

        // create a paginated response mock
        Pageable pageable = PageRequest.of(0, 10);
        Page<PharmacyDrugAvailabilityEntity> pageResponse = new PageImpl<>(List.of(availabilityEntity), pageable, 1);

        // mock service call, expect it returns a page identical to pageResponse, containing only availabilityEntity
        when(availabilityService.findAllByLicenceNo(licenceNo, pageable)).thenReturn(pageResponse);

        // mock mapper to convert PharmacyDrugAvailabilityEntity to PharmacyDrugAvailabilityDto, check it returns the corresponding Dto
        when(availabilityMapper.mapTo(availabilityEntity)).thenReturn(availabilityDto);

        // perform GET request and validate the response
        mockMvc.perform(MockMvcRequestBuilders.get("/pharmacy-drug-availabilities/search-for-stock")
                .accept(MediaType.APPLICATION_JSON)
                .param("licenceNo", licenceNo)
                .param("page", "0")
                .param("size", "10"))
                // expect HTTP Status 200 OK
                .andExpect(status().isOk())
                // expect availability details to match those of the test availability
                .andExpect(jsonPath("$.content[0].pharmacy.psiRegNo", is(availabilityDto.getPharmacy().getPsiRegNo())))
                .andExpect(jsonPath("$.content[0].drug.licenceNo", is(availabilityDto.getDrug().getLicenceNo())))
                .andExpect(jsonPath("$.content[0].isAvailable", is(true)));

        // expect that the findAllByLicenceNo method was called once with the specified parameter
        verify(availabilityService, times(1)).findAllByLicenceNo(licenceNo, pageable);
        // expect that the pharmacyMapper method was called once with the specified parameter
        verify(availabilityMapper, times(1)).mapTo(availabilityEntity);

    }

    // test for listPharmacyDrugAvailabilitiesByDrug() method when no results are returned
    @Test
    public void testListPharmacyDrugAvailabilitiesByDrugNoResults() throws Exception {
        // create test licence number
        String licenceNo = "PA0749/067/001";

        // create a paginated response mock
        Pageable pageable = PageRequest.of(0, 10);
        Page<PharmacyDrugAvailabilityEntity> pageResponse = new PageImpl<>(Collections.emptyList(), pageable, 0);

        // mock service call, expect it returns a page identical to pageResponse, an empty page
        when(availabilityService.findAllByLicenceNo(licenceNo, pageable)).thenReturn(pageResponse);

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

        // expect that the findAllByLicenceNo method was called once with the specified parameter
        verify(availabilityService, times(1)).findAllByLicenceNo(licenceNo, pageable);
        // expect that the pharmacyMapper method was never called
        verify(availabilityMapper, never()).mapTo(any());

    }

/////////////////////////////////////// listAllPharmacyDrugAvailabilities METHOD TESTS /////////////////////////////////////////////////////////

    // test for listAllPharmacyDrugAvailabilities() method when results are returned
    @Test
    public void testListAllPharmacyDrugAvailabilitiesHasResults() throws Exception {
        // create test pharmacyDto and corresponding pharmacyEntity
        PharmacyDto pharmacyDto = TestData.createTestPharmacyDtoA();
        PharmacyEntity pharmacyEntity = TestData.createTestPharmacyA();

        // create test availabilityDto and corresponding availabilityEntity
        PharmacyDrugAvailabilityDto availabilityDto = TestData.createTestPharmacyDrugAvailabilityDtoA(pharmacyDto, TestData.createTestDrugDtoA());
        PharmacyDrugAvailabilityEntity availabilityEntity = TestData.createTestPharmacyDrugAvailabilityA(pharmacyEntity, TestData.createTestDrugA());

        // mock authentication
        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(pharmacyDto);
        // set test security context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // mock mapper to convert PharmacyDto to PharmacyEntity, check it returns the corresponding Entity
        when(pharmacyMapper.mapFrom(pharmacyDto)).thenReturn(pharmacyEntity);

        // mock service call, expect it returns a page identical to pageResponse, containing only availabilityEntity
        when(availabilityService.findAllByPharmacy(pharmacyEntity)).thenReturn(List.of(availabilityEntity));

        // mock mapper to convert PharmacyDrugAvailabilityEntity to PharmacyDrugAvailabilityDto, check it returns the corresponding Dto
        when(availabilityMapper.mapTo(availabilityEntity)).thenReturn(availabilityDto);

        // create a paginated response mock
        List<PharmacyDrugAvailabilityDto> availabilityDtos = List.of(availabilityDto);
        Pageable pageable = PageRequest.of(0, 10);
        Page<PharmacyDrugAvailabilityDto> pageResponse = new PageImpl<>(availabilityDtos, pageable, availabilityDtos.size());
        when(paginator.paginate(any(), eq(pageable))).thenReturn(pageResponse);

        // perform GET request and validate the response
        mockMvc.perform(MockMvcRequestBuilders.get("/pharmacy-drug-availabilities/view-all")
                .accept(MediaType.APPLICATION_JSON)
                .param("page", "0")
                .param("size", "10"))
                // expect HTTP Status 200 OK
                .andExpect(status().isOk())
                // expect availability details to match those of the test availability
                .andExpect(jsonPath("$.content[0].pharmacy.psiRegNo", is(pharmacyDto.getPsiRegNo())))
                .andExpect(jsonPath("$.content[0].drug.licenceNo", is(availabilityDto.getDrug().getLicenceNo())))
                .andExpect(jsonPath("$.content[0].isAvailable", is(true)));

        // expect that the findAllByPharmacy method was called once with the specified parameter
        verify(availabilityService, times(1)).findAllByPharmacy(pharmacyEntity);
        // expect that the pharmacyMapper method was called once with the specified parameter
        verify(pharmacyMapper, times(1)).mapFrom(pharmacyDto);
        // expect that the availabilityMapper method was called once with the specified parameter
        verify(availabilityMapper, times(1)).mapTo(availabilityEntity);

    }

    // test for listAllPharmacyDrugAvailabilities() method when no results are returned
    @Test
    public void testListAllPharmacyDrugAvailabilitiesNoResults() throws Exception {
        // create test pharmacyDto and corresponding pharmacyEntity
        PharmacyDto pharmacyDto = TestData.createTestPharmacyDtoA();
        PharmacyEntity pharmacyEntity = TestData.createTestPharmacyA();

        // create test availabilityEntity
        PharmacyDrugAvailabilityEntity availabilityEntity = TestData.createTestPharmacyDrugAvailabilityA(pharmacyEntity, TestData.createTestDrugA());

        // mock authentication
        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(pharmacyDto);
        // set test security context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // mock mapper to convert PharmacyDto to PharmacyEntity, check it returns the corresponding Entity
        when(pharmacyMapper.mapFrom(pharmacyDto)).thenReturn(pharmacyEntity);

        // mock service call, expect it to return an empty list
        when(availabilityService.findAllByPharmacy(pharmacyEntity)).thenReturn(Collections.emptyList());

        // create a paginated response mock
        Pageable pageable = PageRequest.of(0, 10);
        Page<PharmacyDrugAvailabilityDto> pageResponse = new PageImpl<>(Collections.emptyList(), pageable, 0);
        when(paginator.paginate(any(), eq(pageable))).thenReturn(pageResponse);

        // perform GET request and validate the response
        mockMvc.perform(MockMvcRequestBuilders.get("/pharmacy-drug-availabilities/view-all")
                .accept(MediaType.APPLICATION_JSON)
                .param("page", "0")
                .param("size", "10"))
                // expect HTTP Status 204 No Content
                .andExpect(status().isNoContent())
                // expect that the response is an empty string
                .andExpect(content().string(""));

        // expect that the findAllByPharmacy method was called once with the specified parameter
        verify(availabilityService, times(1)).findAllByPharmacy(pharmacyEntity);
        // expect that the pharmacyMapper method was called once with the specified parameter
        verify(pharmacyMapper, times(1)).mapFrom(pharmacyDto);
        // expect that the availabilityMapper method was never called
        verify(availabilityMapper, never()).mapTo(availabilityEntity);

    }

/////////////////////////////////////// createPharmacyDrugAvailability METHOD TESTS /////////////////////////////////////////////////////////

    // test for createPharmacyDrugAvailability() method when record created successfully
    @Test
    public void testCreatePharmacyDrugAvailabilityCreatedSuccessfully() throws Exception {
        // create test pharmacyDto and pharmacyEntity
        PharmacyDto pharmacyDto = TestData.createTestPharmacyDtoA();
        PharmacyEntity pharmacyEntity = TestData.createTestPharmacyA();
        // create test drugEntity 
        DrugEntity drug = TestData.createTestDrugC();
        // create test availability and corresponding Dto
        PharmacyDrugAvailabilityEntity availabilityEntity = TestData.createTestPharmacyDrugAvailabilityA(pharmacyEntity, drug);
        PharmacyDrugAvailabilityDto availabilityDto = TestData.createTestPharmacyDrugAvailabilityDtoA(pharmacyDto, TestData.createTestDrugDtoC());
        // declare test drug licence number and pharmacy stock availability status
        String licenceNo = "EU/1/17/1251/002";
        boolean isAvailable = true;

        // mock authentication
        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(pharmacyDto);
        // set test security context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // mock mapper to convert PharmacyDto to PharmacyEntity, check it returns the corresponding Entity
        when(pharmacyMapper.mapFrom(pharmacyDto)).thenReturn(pharmacyEntity);
        
        // mock service call to drug service, expect it to return an Optional containing the test drug
        when(drugService.findOne(licenceNo)).thenReturn(Optional.of(drug));

        // mock service call to availability service, expect it to return false
        when(availabilityService.existsByPharmacyAndDrug(pharmacyEntity, drug)).thenReturn(false);

        // mock service call to availability service, expect it to return identical availability entity
        when(availabilityService.savePharmacyDrugAvailability(availabilityEntity)).thenReturn(availabilityEntity);

        // mock mapper to convert availabilityEntity to availabilityDto, check it returns the corresponding Dto
        when(availabilityMapper.mapTo(availabilityEntity)).thenReturn(availabilityDto);
        System.out.println(availabilityMapper.mapTo(availabilityEntity));

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
                .andExpect(jsonPath("$.drug.licenceNo", is(drug.getLicenceNo())))
                .andExpect(jsonPath("$.drug.productName", is(drug.getProductName())))
                .andExpect(jsonPath("$.drug.manufacturer", is(drug.getManufacturer())))
                .andExpect(jsonPath("$.drug.strength", is(drug.getStrength())))
                .andExpect(jsonPath("$.drug.dosageForm", is(drug.getDosageForm())))
                .andExpect(jsonPath("$.drug.activeSubstance", is(drug.getActiveSubstance())))
                .andExpect(jsonPath("$.drug.isAvailable", is(drug.getIsAvailable())))
                .andExpect(jsonPath("$.isAvailable", is(true)));

        // expect that the findOne method from the drugService was called once with the specified parameter
        verify(drugService, times(1)).findOne(licenceNo);
        // expect that the pharmacyMapper method was called once with the specified parameter
        verify(pharmacyMapper, times(1)).mapFrom(pharmacyDto);
        // expect that the existsByPharmacyAndDrug method from the availabilityService was called once with the specified parameter
        verify(availabilityService, times(1)).existsByPharmacyAndDrug(pharmacyEntity, drug);
        // expect that the savePharmacyDrugAvailability method from the availabilityService was called once with the specified parameter
        verify(availabilityService, times(1)).savePharmacyDrugAvailability(availabilityEntity);
        // expect that the availabilityMapper method was called once with the specified parameter
        verify(availabilityMapper, times(1)).mapTo(availabilityEntity);

    }

    // test for createPharmacyDrugAvailability() method when record already exists
    @Test
    public void testCreatePharmacyDrugAvailabilityRecordAlreadyExists() throws Exception {
        // create test pharmacyDto and pharmacyEntity
        PharmacyDto pharmacyDto = TestData.createTestPharmacyDtoA();
        PharmacyEntity pharmacyEntity = TestData.createTestPharmacyA();
        // create test drugEntity 
        DrugEntity drug = TestData.createTestDrugC();
        // declare test drug licence number and pharmacy stock availability status
        String licenceNo = "EU/1/17/1251/002";
        boolean isAvailable = true;

        // mock authentication
        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(pharmacyDto);
        // set test security context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // mock mapper to convert PharmacyDto to PharmacyEntity, check it returns the corresponding Entity
        when(pharmacyMapper.mapFrom(pharmacyDto)).thenReturn(pharmacyEntity);

        // mock service call to drug service, expect it to return an Optional containing the test drug
        when(drugService.findOne(licenceNo)).thenReturn(Optional.of(drug));

        // mock service call to availability service, expect it to return true
        when(availabilityService.existsByPharmacyAndDrug(pharmacyEntity, drug)).thenReturn(true);

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

        // expect that the findOne method from the drugService was called once with the specified parameter
        verify(drugService, times(1)).findOne(licenceNo);
        // expect that the pharmacyMapper method was called once with the specified parameter
        verify(pharmacyMapper, times(1)).mapFrom(pharmacyDto);
        // expect that the existsByPharmacyAndDrug method from the availabilityService was called once with the specified parameter
        verify(availabilityService, times(1)).existsByPharmacyAndDrug(pharmacyEntity, drug);
        // expect that the savePharmacyDrugAvailability method from the availabilityService was never called
        verify(availabilityService, never()).savePharmacyDrugAvailability(any());
        // expect that the availabilityMapper method was never called
        verify(availabilityMapper, never()).mapTo(any());

    }

/////////////////////////////////////// deletePharmacyDrugAvailability METHOD TEST /////////////////////////////////////////////////////////

    // test for deletePharmacyDrugAvailability() method
    @Test
    public void testDeletePharmacyDrugAvailability() throws Exception {
        // create test availability ID
        Long id = 1L;

        // mock service call, expect it to return void
        doNothing().when(availabilityService).delete(id);

        // perform DELETE request and validate the response
        mockMvc.perform(delete("/pharmacy-drug-availabilities/delete/{id}", id))
                // expect HTTP Status 204 No Content
                .andExpect(status().isNoContent())
                // expect that the response is an empty string
                .andExpect(content().string(""));

        // expect that the delete method from the availabilityService was called once with the specified parameter
        verify(availabilityService, times(1)).delete(id);

    }

}
