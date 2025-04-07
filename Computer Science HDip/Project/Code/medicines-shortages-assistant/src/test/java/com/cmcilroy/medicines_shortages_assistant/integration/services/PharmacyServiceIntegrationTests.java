package com.cmcilroy.medicines_shortages_assistant.integration.services;

import com.cmcilroy.medicines_shortages_assistant.TestData;
import com.cmcilroy.medicines_shortages_assistant.cleaner.DatabaseCleaner;
import com.cmcilroy.medicines_shortages_assistant.domain.dto.AccountCredentialsDto;
import com.cmcilroy.medicines_shortages_assistant.domain.entities.PharmacyEntity;
import com.cmcilroy.medicines_shortages_assistant.repositories.PharmacyRepository;
import com.cmcilroy.medicines_shortages_assistant.scrapers.WebScraper;
import com.cmcilroy.medicines_shortages_assistant.services.impl.PharmacyServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
public class PharmacyServiceIntegrationTests {

    private PharmacyServiceImpl pharmacyService;

    private PharmacyRepository pharmacyRepository;

    private PasswordEncoder passwordEncoder;

    private DatabaseCleaner databaseCleaner;

    @MockitoBean
    private WebScraper webScraper;

    @Autowired
    public PharmacyServiceIntegrationTests(
        PharmacyServiceImpl pharmacyService, PharmacyRepository pharmacyRepository, 
        PasswordEncoder passwordEncorder, DatabaseCleaner databaseCleaner
    ) {
        this.pharmacyService = pharmacyService;
        this.pharmacyRepository = pharmacyRepository;
        this.passwordEncoder = passwordEncorder;
        this.databaseCleaner = databaseCleaner;
    }

///////////////////////////////////////////////// CLEAR DATABASE BEFORE EACH TEST ////////////////////////////////////////////////////

    @BeforeEach
    public void clearDatabase() {
        databaseCleaner.clearDatabase();
    }

/////////////////////////////////////// registerNewPharmacy() METHOD TESTS /////////////////////////////////////////////////////////

    // test for registerNewPharmacy() method where registration successful
    @Test
    public void testRegisterNewPharmacySuccessful() {
        // create test pharmacy 
        PharmacyEntity pharmacy = TestData.createTestPharmacyA();

        // mock web scraper PSI reg number validation to return true
        when(webScraper.validatePsiRegNumber(pharmacy.getPsiRegNo())).thenReturn(true);

        PharmacyEntity savedPharmacy = pharmacyService.registerNewPharmacy(pharmacy);

        // verify that the test pharmacy was saved to the database
        assertNotNull(savedPharmacy);
        assertEquals(pharmacy, savedPharmacy);
        assertTrue(pharmacyRepository.existsById(savedPharmacy.getPsiRegNo()));

    }

    // test for registerNewPharmacy() method where PSI registration number invalid
    @Test
    public void testRegisterNewPharmacyInvalidPsiRegNumber() {
        // create a pharmacy object with an invalid PSI number
        PharmacyEntity pharmacy = TestData.createTestPharmacyA();

        // PSI reg number of test pharmacy is not valid so no need to mock behaviour

        // verify that correct exception is thrown
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class, () -> pharmacyService.registerNewPharmacy(pharmacy)
        );
        assertEquals("Invalid PSI registration number.", exception.getMessage());

    }

/////////////////////////////////////// signIn() METHOD TESTS /////////////////////////////////////////////////////////

    // test for signIn() method where sign in is successful
    @Test
    public void testSignInSuccessful() {
        // create test pharmacy and encode password
        PharmacyEntity pharmacy = TestData.createTestPharmacyA();
        pharmacy.setPassword(passwordEncoder.encode(pharmacy.getPassword()));
        // persist it in the database
        pharmacyRepository.save(pharmacy);
        // create test account credentials
        AccountCredentialsDto credentials = new AccountCredentialsDto("test@email.com", "password");

        Optional<PharmacyEntity> result = pharmacyService.signIn(credentials);

        // verify that a record is returned
        assertTrue(result.isPresent());
        // verify that the record returned is the same as the test pharmacy
        assertEquals(pharmacy, result.get());

    }

    // test for signIn() method where no matching record is found
    @Test
    public void testSignInAccountNotFound() {
        // create test pharmacy and encode password
        PharmacyEntity pharmacy = TestData.createTestPharmacyA();
        pharmacy.setPassword(passwordEncoder.encode(pharmacy.getPassword()));
        // persist it in the database
        pharmacyRepository.save(pharmacy);
        // create test account credentials
        AccountCredentialsDto credentials = new AccountCredentialsDto("incorrect@email.com", "password");

        // verify that correct exception is thrown
        UsernameNotFoundException exception = assertThrows(
            UsernameNotFoundException.class,
            () -> pharmacyService.signIn(credentials) 
        );
        assertEquals("No account found with this email.", exception.getMessage());

    }

/////////////////////////////////////// updatePharmacy() METHOD TESTS /////////////////////////////////////////////////////////

    // test for updatePharmacy() method where update is successful
    @Test
    public void testUpdatePharmacySuccessful() {
        // create test pharmacy and encode password
        PharmacyEntity pharmacy = TestData.createTestPharmacyA();
        String rawPassword = pharmacy.getPassword();
        pharmacy.setPassword(passwordEncoder.encode(pharmacy.getPassword()));
        // persist it in the database
        pharmacyRepository.save(pharmacy);

        // create test updateRequest
        PharmacyEntity updateRequest = TestData.createTestPharmacyA();
        updateRequest.setPharmacyName("Updated Name");
        updateRequest.setPhoneNo("Updated Phone Number");
        updateRequest.setEmail("UpdatedEmail@email.com");
        updateRequest.setPassword("UpdatedRawPassword");

        PharmacyEntity updatedPharmacy = pharmacyService.updatePharmacy(rawPassword, updateRequest);

        // verify that a pharmacy entity is returned by the method
        assertNotNull(updatedPharmacy);
        // verify that the entity returned matches the update request
        assertEquals(updatedPharmacy.getPharmacyName(), updateRequest.getPharmacyName());
        assertEquals(updatedPharmacy.getPhoneNo(), updateRequest.getPhoneNo());
        assertEquals(updatedPharmacy.getEmail(), updateRequest.getEmail());
        assertTrue(passwordEncoder.matches(updateRequest.getPassword(), updatedPharmacy.getPassword()));

    }

    // test for updatePharmacy() method where incorrect password entered
    @Test
    public void testUpdatePharmacyIncorrectPassword() {
        // create test pharmacy and encode password
        PharmacyEntity pharmacy = TestData.createTestPharmacyA();
        pharmacy.setPassword(passwordEncoder.encode(pharmacy.getPassword()));
        // persist it in the database
        pharmacyRepository.save(pharmacy);

        // create test updateRequest
        PharmacyEntity updateRequest = TestData.createTestPharmacyA();
        updateRequest.setPharmacyName("Updated Name");

        // verify that correct exception is thrown
        BadCredentialsException exception = assertThrows(
            BadCredentialsException.class, () -> pharmacyService.updatePharmacy("IncorrectPassword", updateRequest)
        );
        assertEquals("Incorrect password. Please try again.", exception.getMessage());

    }

/////////////////////////////////////// delete() METHOD TESTS /////////////////////////////////////////////////////////

    // test for delete() method where deletion is successful
    @Test
    public void testDeleteSuccessful() {
        // create test pharmacy
        PharmacyEntity pharmacy = TestData.createTestPharmacyA();
        // store raw password
        String rawPassword = pharmacy.getPassword();
        // encode password
        pharmacy.setPassword(passwordEncoder.encode(pharmacy.getPassword()));
        // persist it in the database
        pharmacyRepository.save(pharmacy);

        // verify that pharmacy exists in the database
        assertTrue(pharmacyRepository.existsById(pharmacy.getPsiRegNo()));

        // delete the pharmacy from the database
        pharmacyService.delete(rawPassword, pharmacy);

        // verify that the pharmacy no longer exists in the database
        assertFalse(pharmacyRepository.existsById(pharmacy.getPsiRegNo()));

    }

    // test for delete() method where incorrect password is entered
    @Test
    public void testDeleteIncorrectPassword() {
        // create test pharmacy
        PharmacyEntity pharmacy = TestData.createTestPharmacyA();
        // encode password
        pharmacy.setPassword(passwordEncoder.encode(pharmacy.getPassword()));
        // persist it in the database
        pharmacyRepository.save(pharmacy);

        // verify that pharmacy exists in the database
        assertTrue(pharmacyRepository.existsById(pharmacy.getPsiRegNo()));

        // verify that correct exception is thrown
        BadCredentialsException exception = assertThrows(
            BadCredentialsException.class, () -> pharmacyService.delete("IncorrectPassword", pharmacy)
        );
        assertEquals("Incorrect password. Please try again.", exception.getMessage());

    }

    /////////////////////////////////////// isPresent() METHOD TEST /////////////////////////////////////////////////////////

    @Test
    public void testIsPresent() {
        // create test pharmacy and persist it in the database
        PharmacyEntity pharmacy = TestData.createTestPharmacyA();
        pharmacyRepository.save(pharmacy);

        // verify that the test pharmacy is present in the database
        assertTrue(pharmacyService.isPresent(pharmacy.getPsiRegNo()));

        // verify that a non-existent pharmacy does not exist in the database
        assertFalse(pharmacyService.isPresent(9999));
    }
}
