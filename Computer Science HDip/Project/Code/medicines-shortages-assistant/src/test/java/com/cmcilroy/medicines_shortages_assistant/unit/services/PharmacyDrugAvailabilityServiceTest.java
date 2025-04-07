package com.cmcilroy.medicines_shortages_assistant.unit.services;

import com.cmcilroy.medicines_shortages_assistant.TestData;
import com.cmcilroy.medicines_shortages_assistant.domain.entities.DrugEntity;
import com.cmcilroy.medicines_shortages_assistant.domain.entities.PharmacyDrugAvailabilityEntity;
import com.cmcilroy.medicines_shortages_assistant.domain.entities.PharmacyEntity;
import com.cmcilroy.medicines_shortages_assistant.repositories.PharmacyDrugAvailabilityRepository;
import com.cmcilroy.medicines_shortages_assistant.services.impl.PharmacyDrugAvailabilityServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PharmacyDrugAvailabilityServiceTest {

    @Mock
    private PharmacyDrugAvailabilityRepository pharmacyDrugAvailabilityRepository;

    @InjectMocks
    private PharmacyDrugAvailabilityServiceImpl pharmacyDrugAvailabilityService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

/////////////////////////////////////// savePharmacyDrugAvailability() METHOD TEST /////////////////////////////////////////////////////////

    @Test
    public void testSavePharmacyDrugAvailability() {
        // create test availability
        PharmacyDrugAvailabilityEntity availability = TestData.createTestPharmacyDrugAvailabilityA(
            TestData.createTestPharmacyA(), TestData.createTestDrugA()
        );

        // mock availability repository to return identical entity when saving an entity to the database
        when(pharmacyDrugAvailabilityRepository.save(availability)).thenReturn(availability);

        PharmacyDrugAvailabilityEntity savedAvailability = pharmacyDrugAvailabilityService.savePharmacyDrugAvailability(availability);

        // verify that a record was saved to the database
        assertNotNull(savedAvailability);
        // verify that the record returned was the same as the test availability
        assertEquals(availability, savedAvailability);
        // verify that the availability repository method was called once with the specified parameter
        verify(pharmacyDrugAvailabilityRepository, times(1)).save(availability);
    }

/////////////////////////////////////// findAllByLicenceNo() METHOD TEST /////////////////////////////////////////////////////////

    @Test
    public void testFindAllByLicenceNo() {
        // create test licence number
        String licenceNo = "PA0749/067/001";
        // create test availability
        PharmacyDrugAvailabilityEntity availability = TestData.createTestPharmacyDrugAvailabilityA(
            TestData.createTestPharmacyA(), TestData.createTestDrugA()
        );

        // mock pagination
        Pageable pageable = mock(Pageable.class);
        Page<PharmacyDrugAvailabilityEntity> page = new PageImpl<>(List.of(availability));
        
        // mock repository method to return a page containing the test availability
        when(pharmacyDrugAvailabilityRepository.findAllByDrug_LicenceNo(licenceNo, pageable)).thenReturn(page);

        Page<PharmacyDrugAvailabilityEntity> result = pharmacyDrugAvailabilityService.findAllByLicenceNo(licenceNo, pageable);

        // verify that a result was returned and is not null
        assertNotNull(result);
        // verify that the page contents contains one element
        assertEquals(1, result.getContent().size());
        // verify that the page contains the test availability
        assertEquals(result.getContent().getFirst(), availability);
        // verify that the repository method was called once with the specified parameters
        verify(pharmacyDrugAvailabilityRepository, times(1)).findAllByDrug_LicenceNo(licenceNo, pageable);
    }

/////////////////////////////////////// findAllByPharmacy() METHOD TEST /////////////////////////////////////////////////////////

    @Test
    public void testFindAllByPharmacy() {
        // create test pharmacy
        PharmacyEntity pharmacy = TestData.createTestPharmacyA();
        // create test availability
        PharmacyDrugAvailabilityEntity availability = TestData.createTestPharmacyDrugAvailabilityA(
            TestData.createTestPharmacyA(), TestData.createTestDrugA()
        );

        // mock repository to return a list containing the test availability
        when(pharmacyDrugAvailabilityRepository.findAllByPharmacy(pharmacy)).thenReturn(List.of(availability));

        List<PharmacyDrugAvailabilityEntity> result = pharmacyDrugAvailabilityService.findAllByPharmacy(pharmacy);

        // verify that a result was returned and is not null
        assertNotNull(result);
        // verify that the list contains one element
        assertEquals(1, result.size());
        // verify that the list contains the test availability
        assertEquals(result.get(0), availability);
        // verify that the repository method was called once with the specified parameters
        verify(pharmacyDrugAvailabilityRepository, times(1)).findAllByPharmacy(pharmacy);
    }

/////////////////////////////////////// existsByPharmacyAndDrug() METHOD TEST /////////////////////////////////////////////////////////

    @Test
    public void testExistsByPharmacyAndDrug() {
        // create test pharmacy and test drug
        PharmacyEntity pharmacy = TestData.createTestPharmacyA();
        DrugEntity drug = TestData.createTestDrugA();

        // mock repository to return true as if record found
        when(pharmacyDrugAvailabilityRepository.existsByPharmacyAndDrug(pharmacy, drug)).thenReturn(true);

        boolean recordExists = pharmacyDrugAvailabilityService.existsByPharmacyAndDrug(pharmacy, drug);

        // verify that a record was found
        assertTrue(recordExists);
        // verify that the repository method was called once with the specified parameters
        verify(pharmacyDrugAvailabilityRepository, times(1)).existsByPharmacyAndDrug(pharmacy, drug);
    }

/////////////////////////////////////// delete() METHOD TEST /////////////////////////////////////////////////////////

    @Test
    public void testDelete() {
        // declare test record ID
        Long id = 1L;

        // mock behaviour of void repository delete method
        doNothing().when(pharmacyDrugAvailabilityRepository).deleteById(id);

        // call service method
        pharmacyDrugAvailabilityService.delete(id);

        // verify that the repository method was called once with the specified parameter
        verify(pharmacyDrugAvailabilityRepository, times(1)).deleteById(id);
    }

/////////////////////////////////////// deleteAllByPharmacy() METHOD TEST /////////////////////////////////////////////////////////

    @Test
    public void testDeleteAllByPharmacy() {
        // create test pharmacy
        PharmacyEntity pharmacy = TestData.createTestPharmacyA();

        // mock behaviour of void repository deleteAllByPharmacy method
        doNothing().when(pharmacyDrugAvailabilityRepository).deleteAllByPharmacy(pharmacy);

        // call service method
        pharmacyDrugAvailabilityService.deleteAllByPharmacy(pharmacy);

        // verify that the repository method was called once with the specified parameter
        verify(pharmacyDrugAvailabilityRepository, times(1)).deleteAllByPharmacy(pharmacy);
    }
}
