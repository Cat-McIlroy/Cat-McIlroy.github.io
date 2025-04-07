package com.cmcilroy.medicines_shortages_assistant.unit.controllers;

import com.cmcilroy.medicines_shortages_assistant.TestData;
import com.cmcilroy.medicines_shortages_assistant.controllers.DrugController;
import com.cmcilroy.medicines_shortages_assistant.domain.dto.DrugDto;
import com.cmcilroy.medicines_shortages_assistant.domain.entities.DrugEntity;
import com.cmcilroy.medicines_shortages_assistant.services.DrugService;
import com.cmcilroy.medicines_shortages_assistant.mappers.Mapper;
import com.cmcilroy.medicines_shortages_assistant.paginators.Paginator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class DrugControllerTest {

    @Mock
    private DrugService drugService;

    @Mock
    private Mapper<DrugEntity, DrugDto> drugMapper;

    @Mock
    private Paginator<DrugDto> paginator;

    @InjectMocks
    private DrugController drugController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(drugController)
        .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
        .build();
    }

/////////////////////////////////////// listAllShorts() METHOD TESTS /////////////////////////////////////////////////////////

    // test for listAllShorts() method when list contains both an Available drug and a Short drug, it has results
    @Test
    public void testListAllShortsHasResults() throws Exception {
        List<DrugEntity> drugs = new ArrayList<>();
        DrugEntity drugAvailable = TestData.createTestDrugA();
        DrugEntity drugShort = TestData.createTestDrugC();
        // populate the drugs list with one available drug and one short drug
        drugs.add(drugAvailable);
        drugs.add(drugShort);

        // mock service call, expect it returns a list containing the short drug only
        when(drugService.findAllByIsAvailable(false)).thenReturn(List.of(drugShort));

        // mock mapper to convert DrugEntity to DrugDto, check it returns the corresponding DrugDto
        when(drugMapper.mapTo(drugShort)).thenReturn(TestData.createTestDrugDtoC());

        // perform GET request and validate the response
        mockMvc.perform(get("/drugs/shortages")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // expect that there is a DrugDto returned as one of the test drugs is short
                .andExpect(jsonPath("$", hasSize(1)));

        // expect that the findAllByIsAvailable method was called once with the specified parameter
        verify(drugService, times(1)).findAllByIsAvailable(false);
        // expect that the drugMapper method was called once with the specified parameter
        verify(drugMapper, times(1)).mapTo(drugShort);
    }

    // test for listAllShorts() method when list contains only Available drugs and no Shorts, it has no results
    @Test
    public void testListAllShortsAvailableNoResults() throws Exception {
        List<DrugEntity> drugs = new ArrayList<>();
        DrugEntity drugA = TestData.createTestDrugA();
        DrugEntity drugB = TestData.createTestDrugB();
        // populate the drugs list with two available drugs
        drugs.add(drugA);
        drugs.add(drugB);

        // mock service call, expect it returns an empty list
        when(drugService.findAllByIsAvailable(false)).thenReturn(Collections.emptyList());

        // perform GET request and validate the response
        mockMvc.perform(get("/drugs/shortages")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // expect that there is an empty list returned
                .andExpect(jsonPath("$", hasSize(0)));

        // expect that the findAllByIsAvailable method was called once with the specified parameter
        verify(drugService, times(1)).findAllByIsAvailable(false);
        // expect that the drugMapper is never called
        verify(drugMapper, never()).mapTo(any());
    }

/////////////////////////////////////// listDrugNamesByActiveSubstance() METHOD TESTS /////////////////////////////////////////////////////////

    // test for listDrugNamesByActiveSubstance() method when no results are returned
    @Test
    public void testListDrugNamesByActiveSubstanceNoResults() throws Exception {
        // declare a non-existent active substance
        String activeSubstance = "non-existent";
        List<DrugEntity> drugs = new ArrayList<>();
        // create a test drug
        DrugEntity drugA = TestData.createTestDrugA();
        // add drug to the list
        drugs.add(drugA);

        // mock service call, expect it returns an empty list
        when(drugService.findAllByActiveSubstance(activeSubstance)).thenReturn(Collections.emptyList());

        // create a paginated response mock
        List<DrugDto> drugDtos = List.of();
        Pageable pageable = PageRequest.of(0, 10);
        Page<DrugDto> pageResponse = new PageImpl<>(drugDtos, pageable, drugDtos.size());
        when(paginator.paginate(any(), eq(pageable))).thenReturn(pageResponse);

        // perform GET request with active substance and validate the response
        mockMvc.perform(get("/drugs/search-by-active-substance")
                .accept(MediaType.APPLICATION_JSON)
                .param("activeSubstance", activeSubstance)
                .param("page", "page")
                .param("size", "size"))
                // expect to receive a HTTP Status 404 Not Found
                .andExpect(status().isNotFound())
                // expect response is an empty string
                .andExpect(content().string(""));

        // expect that the findAllByActiveSubstance method was called once with the specified parameter
        verify(drugService, times(1)).findAllByActiveSubstance(activeSubstance);
        // expect that the drugMapper is never called
        verify(drugMapper, never()).mapTo(any());
    }

    // test for listDrugNamesByActiveSubstance() method with a single active substance
    @Test
    public void testListDrugNamesByActiveSubstanceSingleSubstance() throws Exception {
        // declare an active substance
        String activeSubstance = "amlodipine";
        // create test drug containing single substance
        DrugEntity drugSingleAmlodipine = TestData.createTestDrugA();

        // mock service call, expect it returns only the single substance drug
        when(drugService.findAllByActiveSubstance(activeSubstance)).thenReturn(List.of(drugSingleAmlodipine));

        // mock mapper to convert DrugEntity object to its corresponding DrugDto object
        DrugDto drugSingleAmlodipineDto = TestData.createTestDrugDtoA();
        // expect the corresponding DrugDto object to be returned
        when(drugMapper.mapTo(drugSingleAmlodipine)).thenReturn(drugSingleAmlodipineDto);

        // create a paginated response mock
        List<DrugDto> drugDtos = List.of(drugSingleAmlodipineDto);
        Pageable pageable = PageRequest.of(0, 10);
        Page<DrugDto> pageResponse = new PageImpl<>(drugDtos, pageable, drugDtos.size());
        when(paginator.paginate(any(), eq(pageable))).thenReturn(pageResponse);

        // perform GET request with active substance and validate the response
        mockMvc.perform(get("/drugs/search-by-active-substance")
                .accept(MediaType.APPLICATION_JSON)
                .param("activeSubstance", activeSubstance)
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                // check that the expected DrugDto object is contained in the response
                .andExpect(jsonPath("$.content[0].licenceNo", is(drugSingleAmlodipineDto.getLicenceNo())));

        // expect that the findAllByActiveSubstance method was called once with the specified parameter
        verify(drugService, times(1)).findAllByActiveSubstance(activeSubstance);
        // expect that the drugMapper method was called once with the specified parameter
        verify(drugMapper, times(1)).mapTo(drugSingleAmlodipine);
    }


    // test for listDrugNamesByActiveSubstance() method when single active substance has synonyms
    @Test
    public void testListDrugNamesByActiveSubstanceSingleSubstanceSynonyms() throws Exception {
        // declare an active substance which has synonyms
        String activeSubstance = "aspirin";
        Pageable pageable = PageRequest.of(0, 10);
        List<DrugEntity> drugs = new ArrayList<>();
        // create two test drugs with different aspirin synonyms (aspirin, and acetylsalicylic acid)
        DrugEntity drugAspirin = TestData.createTestDrugD();
        DrugEntity drugAsa = TestData.createTestDrugE();
        // add both drugs to the list
        drugs.add(drugAspirin);
        drugs.add(drugAsa);

        // mock service call, expect it returns both drugs in the list
        when(drugService.findAllByActiveSubstance(activeSubstance)).thenReturn(drugs);

        // mock mapper to convert DrugEntity objects to their corresponding DrugDto objects
        DrugDto drugAspirinDto = TestData.createTestDrugDtoD();
        DrugDto drugAsaDto = TestData.createTestDrugDtoE();
        // expect the corresponding DrugDto objects to be returned
        when(drugMapper.mapTo(drugAspirin)).thenReturn(drugAspirinDto);
        when(drugMapper.mapTo(drugAsa)).thenReturn(drugAsaDto);

        // create a paginated response mock
        List<DrugDto> drugDtos = List.of(drugAspirinDto, drugAsaDto);
        Page<DrugDto> pageResponse = new PageImpl<>(drugDtos, pageable, drugDtos.size());
        when(paginator.paginate(any(), eq(pageable))).thenReturn(pageResponse);

        // perform GET request with active substance and validate the response
        mockMvc.perform(get("/drugs/search-by-active-substance")
                .accept(MediaType.APPLICATION_JSON)
                .param("activeSubstance", activeSubstance)
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                // check that the expected DrugDto objects are contained in the response
                .andExpect(jsonPath("$.content[*].licenceNo", containsInAnyOrder(
                        drugAspirinDto.getLicenceNo(),
                        drugAsaDto.getLicenceNo()
                    )));

        // expect that the findAllByActiveSubstance method was called once with the specified parameter
        verify(drugService, times(1)).findAllByActiveSubstance(activeSubstance);
        // expect that the drugMapper method was called once with each specified parameter
        verify(drugMapper, times(1)).mapTo(drugAspirin);
        verify(drugMapper, times(1)).mapTo(drugAsa);
    }

    // test for listDrugNamesByActiveSubstance() method with a combination active substance
    @Test
    public void testListDrugNamesByActiveSubstanceCombinationSubstance() throws Exception {
        // declare an active substance
        String activeSubstance = "amlodipine, olmesartan";

        // create test drug containing a combination substance which includes the test active substance
        DrugEntity drugComboKonverge = TestData.createTestDrugB();


        // mock service call, expect it returns only the combination substance drug
        when(drugService.findAllByComboActiveSubstances(activeSubstance)).thenReturn(List.of(drugComboKonverge));

        // mock mapper to convert DrugEntity object to its corresponding DrugDto object
        DrugDto drugComboKonvergeDto = TestData.createTestDrugDtoB();
        // expect the corresponding DrugDto object to be returned
        when(drugMapper.mapTo(drugComboKonverge)).thenReturn(drugComboKonvergeDto);

        // create a paginated response mock
        List<DrugDto> drugDtos = List.of(drugComboKonvergeDto);
        Pageable pageable = PageRequest.of(0, 10);
        Page<DrugDto> pageResponse = new PageImpl<>(drugDtos, pageable, drugDtos.size());
        when(paginator.paginate(any(), eq(pageable))).thenReturn(pageResponse);

        // perform GET request with active substance and validate the response
        mockMvc.perform(get("/drugs/search-by-active-substance")
                .accept(MediaType.APPLICATION_JSON)
                .param("activeSubstance", activeSubstance)
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                // check that the expected DrugDto object is contained in the response
                .andExpect(jsonPath("$.content[0].licenceNo", is(drugComboKonvergeDto.getLicenceNo())));

        // expect that the findAllByComboActiveSubstances method was called once with the specified parameter
        verify(drugService, times(1)).findAllByComboActiveSubstances(activeSubstance);
        // expect that the drugMapper method was called once with the specified parameter
        verify(drugMapper, times(1)).mapTo(drugComboKonverge);
    }

    // test for listDrugNamesByActiveSubstance() method when combination active substance has synonyms
    @Test
    public void testListDrugNamesByActiveSubstanceCombinationSubstanceSynonyms() throws Exception {
        // declare a combination active substance which has synonyms (e.g. contains aspirin, AKA acetylsalicylic acid).
        String activeSubstance = "aspirin, ramipril, atorvastatin";
        Pageable pageable = PageRequest.of(0, 10);
        List<DrugEntity> drugs = new ArrayList<>();
        // create two test combination drugs with different aspirin synonyms (aspirin, and acetylsalicylic acid)
        DrugEntity drugComboAsa = TestData.createTestDrugF();
        DrugEntity drugComboAspirin = TestData.createTestDrugG();
        // add both drugs to the list
        drugs.add(drugComboAsa);
        drugs.add(drugComboAspirin);

        // mock service call, expect it returns both combination drugs in the list
        when(drugService.findAllByComboActiveSubstances(activeSubstance)).thenReturn(List.of(drugComboAsa, drugComboAspirin));

        // mock mapper to convert DrugEntity objects to their corresponding DrugDto objects
        DrugDto drugComboAsaDto = TestData.createTestDrugDtoF();
        DrugDto drugComboAspirinDto = TestData.createTestDrugDtoG();
        // expect the corresponding DrugDto objects to be returned
        when(drugMapper.mapTo(drugComboAsa)).thenReturn(drugComboAsaDto);
        when(drugMapper.mapTo(drugComboAspirin)).thenReturn(drugComboAspirinDto);

        // create a paginated response mock
        List<DrugDto> drugDtos = List.of(drugComboAsaDto, drugComboAspirinDto);
        Page<DrugDto> pageResponse = new PageImpl<>(drugDtos, pageable, drugDtos.size());
        when(paginator.paginate(any(), eq(pageable))).thenReturn(pageResponse);

        // perform GET request with active substance and validate the response
        mockMvc.perform(get("/drugs/search-by-active-substance")
                .accept(MediaType.APPLICATION_JSON)
                .param("activeSubstance", activeSubstance)
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                // check that the expected DrugDto objects are contained in the response
                .andExpect(jsonPath("$.content[*].licenceNo", containsInAnyOrder(
                        drugComboAsaDto.getLicenceNo(),
                        drugComboAspirinDto.getLicenceNo()
                    )));

        // expect that the findAllByComboActiveSubstances method was called once with the specified parameter
        verify(drugService, times(1)).findAllByComboActiveSubstances(activeSubstance);
        // expect that the drugMapper method was called once with each specified parameter
        verify(drugMapper, times(1)).mapTo(drugComboAsa);
        verify(drugMapper, times(1)).mapTo(drugComboAspirin);
    }

/////////////////////////////////////// listAlternativeDrugNames() METHOD TESTS /////////////////////////////////////////////////////////

    // test for listAlternativeDrugNames() method where no results are returned
    @Test
    public void testListAlternativeDrugNamesNoResults() throws Exception {
        // declare a product name which is non-existent
        String productName = "non-existent";
        List<DrugEntity> drugs = new ArrayList<>();
        // create a test drug
        DrugEntity drugA = TestData.createTestDrugA();
        // add drug to the list
        drugs.add(drugA);

        // mock service call, expect it returns an empty list
        when(drugService.findByContainsProductName(productName)).thenReturn(Collections.emptyList());

        // create a paginated response mock
        List<DrugDto> drugDtos = List.of();
        Pageable pageable = PageRequest.of(0, 10);
        Page<DrugDto> pageResponse = new PageImpl<>(drugDtos, pageable, drugDtos.size());
        when(paginator.paginate(any(), eq(pageable))).thenReturn(pageResponse);

        // perform GET request for alternative drug names and validate the response
        mockMvc.perform(get("/drugs/search-by-product-name")
                .accept(MediaType.APPLICATION_JSON)
                .param("productName", productName)
                .param("page", "0")
                .param("size", "10"))
                // expect to receive a HTTP Status 404 Not Found
                .andExpect(status().isNotFound())
                // expect response is an empty string
                .andExpect(content().string(""));

        // expect that the findByContainsProductName method was called once with the specified parameter
        verify(drugService, times(1)).findByContainsProductName(productName);
        // expect that the drugMapper is never called
        verify(drugMapper, never()).mapFrom(any());
        verify(drugMapper, never()).mapTo(any());
    }

    // test for listAlternativeDrugNames() method where results are returned
    @Test
    public void testListAlternativeDrugNamesHasResults() throws Exception {
        // declare a product name which is a brand name and has existing generic and brand alternatives
        String productName = "istin";
        List<DrugEntity> drugs = new ArrayList<>();
        // create two test drugs which are different brands containing the same active substance,
        // one that contains a combination with the same active in it, and one that contains a different single active
        DrugEntity drugIstinBrand = TestData.createTestDrugH();
        DrugEntity drugGenericBrand = TestData.createTestDrugA();
        DrugEntity drugComboContainingActive = TestData.createTestDrugB();
        DrugEntity drugDifferentActive = TestData.createTestDrugD();
        // add all drugs to the list
        drugs.add(drugIstinBrand);
        drugs.add(drugGenericBrand);
        drugs.add(drugComboContainingActive);
        drugs.add(drugDifferentActive);

        // mock service calls, expect they return lists containing the two different brands but not the combination or the different active
        when(drugService.findByContainsProductName(productName)).thenReturn(List.of(drugIstinBrand, drugGenericBrand));
        when(drugService.findAllByActiveSubstance(drugIstinBrand.getActiveSubstance())).thenReturn(List.of(drugIstinBrand, drugGenericBrand));

        // mock mapper to convert DrugEntity objects to their corresponding DrugDto objects
        DrugDto drugIstinBrandDto = TestData.createTestDrugDtoH();
        DrugDto drugGenericBrandDto = TestData.createTestDrugDtoA();

        // expect the corresponding DrugDto objects to be returned
        when(drugMapper.mapTo(drugIstinBrand)).thenReturn(drugIstinBrandDto);
        when(drugMapper.mapTo(drugGenericBrand)).thenReturn(drugGenericBrandDto);

        // create a paginated response mock
        List<DrugDto> drugDtos = List.of(drugIstinBrandDto, drugGenericBrandDto);
        Pageable pageable = PageRequest.of(0, 10);
        Page<DrugDto> pageResponse = new PageImpl<>(drugDtos, pageable, drugDtos.size());
        when(paginator.paginate(any(), eq(pageable))).thenReturn(pageResponse);

        // perform GET request for alternative drug names and validate the response
        mockMvc.perform(get("/drugs/search-by-product-name")
                .accept(MediaType.APPLICATION_JSON)
                .param("productName", productName)
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                // check that the expected DrugDto objects are contained in the response
                .andExpect(jsonPath("$.content[*].licenceNo", containsInAnyOrder(
                        drugIstinBrandDto.getLicenceNo(),
                        drugGenericBrandDto.getLicenceNo()
                    )));

        // expect that the findByContainsProductName method was called once with the specified parameter
        verify(drugService, times(1)).findByContainsProductName(productName);
        // expect that the drugMapper method was called once with each specified parameter
        verify(drugMapper, times(1)).mapTo(drugIstinBrand);
        verify(drugMapper, times(1)).mapTo(drugGenericBrand);
    }
}
