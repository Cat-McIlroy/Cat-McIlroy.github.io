package com.cmcilroy.medicines_shortages_assistant.unit.services;

import com.cmcilroy.medicines_shortages_assistant.TestData;
import com.cmcilroy.medicines_shortages_assistant.domain.dto.AccountCredentialsDto;
import com.cmcilroy.medicines_shortages_assistant.domain.entities.PharmacyEntity;
import com.cmcilroy.medicines_shortages_assistant.repositories.PharmacyRepository;
import com.cmcilroy.medicines_shortages_assistant.scrapers.WebScraper;
import com.cmcilroy.medicines_shortages_assistant.services.PharmacyDrugAvailabilityService;
import com.cmcilroy.medicines_shortages_assistant.services.impl.PharmacyServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class PharmacyServiceTest {

    @Mock
    private PharmacyRepository pharmacyRepository;

    @Mock
    private PharmacyDrugAvailabilityService pharmacyDrugAvailabilityService;

    @Mock
    private WebScraper webScraper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PharmacyServiceImpl pharmacyService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

/////////////////////////////////////// registerNewPharmacy() METHOD TESTS /////////////////////////////////////////////////////////

    // test for registerNewPharmacy() method where registration successful
    @Test
    public void testRegisterNewPharmacySuccessful() {
        // create test pharmacy
        PharmacyEntity pharmacy = TestData.createTestPharmacyA();
        // store raw password
        String rawPassword = pharmacy.getPassword();

        // mock webscraper to return true as if PSI reg number is valid
        when(webScraper.validatePsiRegNumber(pharmacy.getPsiRegNo())).thenReturn(true);
        // mock pharmacy repository to return false
        when(pharmacyRepository.existsById(pharmacy.getPsiRegNo())).thenReturn(false);
        // mock password encoder
        when(passwordEncoder.encode(pharmacy.getPassword())).thenReturn("EncodedPassword");
        // mock pharmacy repository to save pharmacy entity and return identical pharmacy entity
        when(pharmacyRepository.save(pharmacy)).thenReturn(pharmacy);

        PharmacyEntity savedPharmacy = pharmacyService.registerNewPharmacy(pharmacy);

        // verify that the web scraper is called once with the following method and parameter
        verify(webScraper, times(1)).validatePsiRegNumber(pharmacy.getPsiRegNo());
        // verify that the pharmacy repository is called once with each method and parameter
        verify(pharmacyRepository, times(1)).existsById(pharmacy.getPsiRegNo());
        verify(pharmacyRepository, times(1)).save(pharmacy);
        // verify that the password encoder is called once with the following method and parameter
        verify(passwordEncoder, times(1)).encode(rawPassword);
        // verify that the pharmacy entity was saved to the database
        assertNotNull(savedPharmacy);
        // verify that the pharmacy entity saved was the test pharmacy
        assertTrue(savedPharmacy.equals(pharmacy));
    }

    // test for registerNewPharmacy() method where PSI number is invalid
    @Test
    public void testRegisterNewPharmacyInvalidPsiRegNumber() {
        // create test pharmacy
        PharmacyEntity pharmacy = TestData.createTestPharmacyA();

        // mock web scraper to return false as if PSI reg number is invalid
        when(webScraper.validatePsiRegNumber(pharmacy.getPsiRegNo())).thenReturn(false);
        
        // verify that correct exception is thrown
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class, () -> pharmacyService.registerNewPharmacy(pharmacy)
        );
        assertEquals("Invalid PSI registration number.", exception.getMessage());
        // verify that no pharmacy repository methods are called
        verify(pharmacyRepository, never()).save(any());
    }

    // test for registerNewPharmacy() method where account already exists
    @Test
    public void testRegisterNewPharmacyAccountAlreadyExists() {
        // create test pharmacy
        PharmacyEntity pharmacy = TestData.createTestPharmacyA();

        // mock web scraper to return true as if PSI reg number is valid
        when(webScraper.validatePsiRegNumber(pharmacy.getPsiRegNo())).thenReturn(true);
        // mock pharmacy repository to return true as if record already exists
        when(pharmacyRepository.existsById(pharmacy.getPsiRegNo())).thenReturn(true);
        
        // verify that correct exception is thrown
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class, () -> pharmacyService.registerNewPharmacy(pharmacy)
        );
        assertEquals("This account already exists.", exception.getMessage());
        // verify that the web scraper is called once with the following method and parameter
        verify(webScraper, times(1)).validatePsiRegNumber(pharmacy.getPsiRegNo());
        // verify that the pharmacy repository is called once with the following method and parameter
        verify(pharmacyRepository, times(1)).existsById(pharmacy.getPsiRegNo());
    }

/////////////////////////////////////// signIn() METHOD TESTS /////////////////////////////////////////////////////////

    // test for signIn() method where sign in successful
    @Test
    public void testSignInSuccessful() {
        // create test pharmacy and account credentials
        PharmacyEntity pharmacy = TestData.createTestPharmacyA();
        AccountCredentialsDto credentials = new AccountCredentialsDto(pharmacy.getEmail(), pharmacy.getPassword());

        // mock pharmacy repository to return test pharmacy
        when(pharmacyRepository.findByEmail(credentials.getEmail())).thenReturn(Optional.of(pharmacy));
        // mock password encoder to match passwords and return true
        when(passwordEncoder.matches(credentials.getPassword(), pharmacy.getPassword())).thenReturn(true);

        Optional<PharmacyEntity> result = pharmacyService.signIn(credentials);

        // verify that the pharmacy repository is called once with the following method and parameter
        verify(pharmacyRepository, times(1)).findByEmail(pharmacy.getEmail());
        // verify that the password encoder is called once with the following method and parameters
        verify(passwordEncoder, times(1)).matches(credentials.getPassword(), pharmacy.getPassword());
        // verify that a pharmacy entity was retrieved from the database
        assertTrue(result.isPresent());
        // verify that the pharmacy entity retrieved was the test pharmacy
        assertEquals(pharmacy, result.get());
    }

    // test for signIn() method where user not found
    @Test
    public void testSignInAccountNotFound() {
        // create test account credentials
        AccountCredentialsDto credentials = new AccountCredentialsDto("test@email.com", "password");

        // mock pharmacy repository to return an empty optional, as if no matching record exists
        when(pharmacyRepository.findByEmail(credentials.getEmail())).thenReturn(Optional.empty());

        // verify that correct exception is thrown
        UsernameNotFoundException exception = assertThrows(
            UsernameNotFoundException.class, () -> pharmacyService.signIn(credentials)
        );
        assertEquals("No account found with this email.", exception.getMessage());
        // verify that pharmacy repository is called once with the following method and parameter
        verify(pharmacyRepository, times(1)).findByEmail(credentials.getEmail());
    }

    // test for signIn() method where incorrect password entered
    @Test
    public void testSignInIncorrectPassword() {
        // create test pharmacy and account credentials
        PharmacyEntity pharmacy = TestData.createTestPharmacyA();
        AccountCredentialsDto credentials = new AccountCredentialsDto(pharmacy.getEmail(), "IncorrectPassword");

        // mock pharmacy repository to return an optional containing the test pharmacy
        when(pharmacyRepository.findByEmail(credentials.getEmail())).thenReturn(Optional.of(pharmacy));
        // mock password encoder to return false, as if password is incorrect
        when(passwordEncoder.matches(credentials.getPassword(), pharmacy.getPassword())).thenReturn(false);

        // verify that correct exception is thrown
        BadCredentialsException exception = assertThrows(
            BadCredentialsException.class, () -> pharmacyService.signIn(credentials)
        );
        assertEquals("Incorrect password. Please try again.", exception.getMessage());
        // verify that pharmacy repository is called once with the following method and parameter
        verify(pharmacyRepository, times(1)).findByEmail(credentials.getEmail());
        // verify that the password encoder is called once with the following method and parameters
        verify(passwordEncoder, times(1)).matches(credentials.getPassword(), pharmacy.getPassword());
    }

/////////////////////////////////////// updatePharmacy() METHOD TESTS /////////////////////////////////////////////////////////

    // test for updatePharmacy() method where update is successful
    @Test
    public void testUpdatePharmacySuccessful() {
        // create test existing pharmacy
        PharmacyEntity existingPharmacy = TestData.createTestPharmacyA();
        existingPharmacy.setPassword("EncodedPassword");
        // create test update
        PharmacyEntity updateRequest = TestData.createTestPharmacyA();
        updateRequest.setPharmacyName("Updated Name");
        updateRequest.setPhoneNo("Updated Phone Number");
        updateRequest.setEmail("UpdatedEmail@email.com");
        updateRequest.setPassword("UpdatedRawPassword");

        // mock pharmacy repository to return record matching test existing pharmacy
        when(pharmacyRepository.findById(updateRequest.getPsiRegNo())).thenReturn(Optional.of(existingPharmacy));
        // mock password encoder to return a match as if correct password entered
        when(passwordEncoder.matches("rawPassword", "EncodedPassword")).thenReturn(true);
        // mock password encoder to return false as if new password is different from existing one
        when(passwordEncoder.matches("EncodedPassword", "UpdatedRawPassword")).thenReturn(false);
        // mock password encoder to encode password
        when(passwordEncoder.encode("UpdatedRawPassword")).thenReturn("NewEncodedPassword");
        // mock pharmacy repository to return updated pharmacy
        when(pharmacyRepository.save(existingPharmacy)).thenReturn(updateRequest);

        PharmacyEntity updatedPharmacy = pharmacyService.updatePharmacy("rawPassword", updateRequest);

        // verify that the updated pharmacy entity was saved to the database
        assertNotNull(updatedPharmacy);
        // verify that the pharmacy repository was called once with each method and parameter
        verify(pharmacyRepository, times(1)).findById(updateRequest.getPsiRegNo());
        verify(pharmacyRepository, times(1)).save(existingPharmacy);
        // verify that the password encoder was called once with each method and parameter
        verify(passwordEncoder, times(1)).matches("rawPassword", "EncodedPassword");
        verify(passwordEncoder, times(1)).matches("EncodedPassword", "UpdatedRawPassword");
        verify(passwordEncoder, times(1)).encode("UpdatedRawPassword");
    }

    // test for updatePharmacy() method where incorrect password entered
    @Test
    public void testUpdatePharmacyIncorrectPassword() {
        // create test pharmacy
        PharmacyEntity pharmacy = TestData.createTestPharmacyA();

        // mock pharmacy repository to return Optional containing the test pharmacy as if matching record exists
        when(pharmacyRepository.findById(pharmacy.getPsiRegNo())).thenReturn(Optional.of(pharmacy));
        // mock password encoder to return false as if entered password does not match password in database
        when(passwordEncoder.matches("IncorrectPassword", pharmacy.getPassword())).thenReturn(false);

        // verify that correct exception is thrown
        BadCredentialsException exception = assertThrows(
            BadCredentialsException.class, () -> pharmacyService.updatePharmacy("IncorrectPassword", pharmacy)
        );
        assertEquals("Incorrect password. Please try again.", exception.getMessage());
        // verify that the pharmacy repository was called once with the specified method and parameter
        verify(pharmacyRepository, times(1)).findById(pharmacy.getPsiRegNo());
        // verify that the password encoder was called once with each method and parameter
        verify(passwordEncoder, times(1)).matches("IncorrectPassword", pharmacy.getPassword());
    }

    // test for updatePharmacy() method where account is not found
    @Test
    public void testUpdatePharmacyAccountNotFound() {
        // create test pharmacy
        PharmacyEntity pharmacy = TestData.createTestPharmacyA();

        // mock pharmacy repository to return an empty optional as if no matching record exists
        when(pharmacyRepository.findById(pharmacy.getPsiRegNo())).thenReturn(Optional.empty());

        // verify that correct exception is thrown
        UsernameNotFoundException exception = assertThrows(
            UsernameNotFoundException.class, () -> pharmacyService.updatePharmacy("password", pharmacy)
        );
        assertEquals("No account found.", exception.getMessage());
        // verify that the pharmacy repository was called once with each method and parameter
        verify(pharmacyRepository, times(1)).findById(pharmacy.getPsiRegNo());
    }

/////////////////////////////////////// delete() METHOD TESTS /////////////////////////////////////////////////////////

    // test for delete() method where deletion is successful
    @Test
    public void testDeleteSuccessful() {
        // create test pharmacy
        PharmacyEntity pharmacy = TestData.createTestPharmacyA();

        // mock pharmacy repository to return true as if record exists
        when(pharmacyRepository.existsById(pharmacy.getPsiRegNo())).thenReturn(true);
        // mock password encoder to return true as if entered password is correct
        when(passwordEncoder.matches("password", pharmacy.getPassword())).thenReturn(true);

        // call service method to delete record
        pharmacyService.delete("password", pharmacy);

        // verify that pharmacy drug availability service method was called once with the specified parameter
        verify(pharmacyDrugAvailabilityService, times(1)).deleteAllByPharmacy(pharmacy);
        // verify that pharmacy service methods were called once with the specified parameters
        verify(pharmacyRepository, times(1)).existsById(pharmacy.getPsiRegNo());
        verify(pharmacyRepository, times(1)).deleteById(pharmacy.getPsiRegNo());
        // verify that the password encoder was called once with each method and parameter
        verify(passwordEncoder, times(1)).matches("password", pharmacy.getPassword());
    }

    // test for delete() method where incorrect password entered
    @Test
    public void testDeleteIncorrectPassword() {
        // create test pharmacy
        PharmacyEntity pharmacy = TestData.createTestPharmacyA();

        // mock pharmacy repository to return true as if record exists
        when(pharmacyRepository.existsById(pharmacy.getPsiRegNo())).thenReturn(true);
        // mock password encoder to return false as if entered password is incorrect
        when(passwordEncoder.matches("IncorrectPassword", pharmacy.getPassword())).thenReturn(false);

        // verify that correct exception is thrown
        BadCredentialsException exception = assertThrows(
            BadCredentialsException.class, () -> pharmacyService.delete("IncorrectPassword", pharmacy)
        );
        assertEquals("Incorrect password. Please try again.", exception.getMessage());
        // verify that pharmacy service method was called once with the specified parameter
        verify(pharmacyRepository, times(1)).existsById(pharmacy.getPsiRegNo());
        // verify that the password encoder was called once with each method and parameter
        verify(passwordEncoder, times(1)).matches("IncorrectPassword", pharmacy.getPassword());

    }

    // test for delete() method where account is not found
    @Test
    public void testDeleteAccountNotFound() {
        // create test pharmacy
        PharmacyEntity pharmacy = TestData.createTestPharmacyA();

        // mock pharmacy repository to return false as if record does not exist
        when(pharmacyRepository.existsById(pharmacy.getPsiRegNo())).thenReturn(false);

        // verify that correct exception is thrown
        UsernameNotFoundException exception = assertThrows(
            UsernameNotFoundException.class, () -> pharmacyService.delete("password", pharmacy)
        );
        assertEquals("No account found.", exception.getMessage());
        // verify that pharmacy repository is called once with the following method and parameter
        verify(pharmacyRepository, times(1)).existsById(pharmacy.getPsiRegNo());
    }

/////////////////////////////////////// isPresent() METHOD TEST /////////////////////////////////////////////////////////

    @Test
    public void testIsPresent() {

        // mock pharmacy repository to return true as if record exists
        when(pharmacyRepository.existsById(1234)).thenReturn(true);
        // verify that pharmacy service method returns true
        assertTrue(pharmacyService.isPresent(1234));

        // mock pharmacy repository to return false as if record doesn't exist
        when(pharmacyRepository.existsById(9999)).thenReturn(false);
        // verify that pharmacy service method returns false
        assertFalse(pharmacyService.isPresent(9999));
    }

}