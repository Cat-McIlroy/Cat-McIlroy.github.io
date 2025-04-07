package com.cmcilroy.medicines_shortages_assistant.services.impl;

import java.util.Optional;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cmcilroy.medicines_shortages_assistant.domain.dto.AccountCredentialsDto;
import com.cmcilroy.medicines_shortages_assistant.domain.entities.PharmacyEntity;
import com.cmcilroy.medicines_shortages_assistant.repositories.PharmacyRepository;
import com.cmcilroy.medicines_shortages_assistant.scrapers.WebScraper;
import com.cmcilroy.medicines_shortages_assistant.services.PharmacyDrugAvailabilityService;
import com.cmcilroy.medicines_shortages_assistant.services.PharmacyService;

import jakarta.transaction.Transactional;

@Service
public class PharmacyServiceImpl implements PharmacyService{

    // inject Repositories
    private PharmacyRepository pharmacyRepository;

    // inject PharmacyDrugAvailabilityService
    private PharmacyDrugAvailabilityService pharmacyDrugAvailabilityService;

    // inject WebScraper
    private WebScraper webScraper;

    // inject PasswordEncoder
    private PasswordEncoder passwordEncoder;

    // constructor injection
    public PharmacyServiceImpl(
        PharmacyRepository pharmacyRepository, 
        PharmacyDrugAvailabilityService pharmacyDrugAvailabilityService,
        WebScraper webScraper,
        PasswordEncoder passwordEncoder) {
            this.pharmacyRepository = pharmacyRepository;
            this.pharmacyDrugAvailabilityService = pharmacyDrugAvailabilityService;
            this.webScraper = webScraper;
            this.passwordEncoder = passwordEncoder;
    }

    //////////////////////////////////////////////////////// ALL USERS //////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////// REGISTER AS A PHARMACY USER ///////////////////////////////////////////////////


    // pass-through method. The Service layer is taking the Entity and passing it to the Repository which persists it in the database
    @Override
    public PharmacyEntity registerNewPharmacy(PharmacyEntity pharmacy) {

        // verify that provided PSI registration number is valid
        if(!webScraper.validatePsiRegNumber(pharmacy.getPsiRegNo())) {
            // if it is not valid, throw error
            throw new IllegalArgumentException("Invalid PSI registration number.");
        }
        // check if a record already exists with this PSI registration number
        else if(pharmacyRepository.existsById(pharmacy.getPsiRegNo())) {
            // if an account already exists with this PSI number, throw error
            throw new IllegalArgumentException("This account already exists.");
        }
        else{
            // encode the password associated with the pharmacy before storing in the database and set it as the pharmacy account password
            pharmacy.setPassword(passwordEncoder.encode(pharmacy.getPassword()));
            // save new PharmacyEntity to the database
            return pharmacyRepository.save(pharmacy);
        }

    }

    ////////////////////////////////////////////////// SIGN IN AS A PHARMACY USER ////////////////////////////////////////////////////

    @Override
    public Optional<PharmacyEntity> signIn(AccountCredentialsDto credentials) {

        // find pharmacy record by email entered
        Optional<PharmacyEntity> pharmacyOpt = pharmacyRepository.findByEmail(credentials.getEmail());

        // get pharmacy entity from optional, if it no record exists throw exception
        PharmacyEntity pharmacy = pharmacyOpt.orElseThrow(() -> new UsernameNotFoundException("No account found with this email."));

        // check if the password entered (once encoded) does not match the encoded password in the database
        if(passwordEncoder.matches(credentials.getPassword(), pharmacy.getPassword())) {
            // return pharmacy optional
            return Optional.of(pharmacy);
        }
        else {
            // incorrect password, throw exception
            throw new BadCredentialsException("Incorrect password. Please try again.");
        }
    }

    //////////////////////////////////////////////// PHARMACY USERS ONLY ////////////////////////////////////////////////////////////

    //////////////////////////////////////////////////// EDIT ACCOUNT DETAILS ///////////////////////////////////////////////////////

    @Override
    public PharmacyEntity updatePharmacy(String password, PharmacyEntity pharmacy) {

        // retrieve record from the database
        Optional<PharmacyEntity> recordOpt = pharmacyRepository.findById(pharmacy.getPsiRegNo());

        // get entity from optional, if no record exists throw exception
        PharmacyEntity record = recordOpt.orElseThrow(() -> new UsernameNotFoundException("No account found."));

        // check if the password entered (once encoded) does not match the encoded password in the database
        if(!passwordEncoder.matches(password, record.getPassword())) {
            // incorrect password, throw exception
            throw new BadCredentialsException("Incorrect password. Please try again.");
        }

        // update pharmacy name if different from existing pharmacy name and not empty string (useful in the case of a sale/takeover/merge)
        if(!record.getPharmacyName().equalsIgnoreCase(pharmacy.getPharmacyName()) && !pharmacy.getPharmacyName().equalsIgnoreCase("")) {
            record.setPharmacyName(pharmacy.getPharmacyName());
        }
        // update contact number if different from existing contact number and not empty string
        if(!record.getPhoneNo().equalsIgnoreCase(pharmacy.getPhoneNo()) && !pharmacy.getPhoneNo().equalsIgnoreCase("")) {
            record.setPhoneNo(pharmacy.getPhoneNo());
        }
        // update email if different from existing email and not empty string
        if(!record.getEmail().equalsIgnoreCase(pharmacy.getEmail()) && !pharmacy.getEmail().equalsIgnoreCase("")) {
            record.setEmail(pharmacy.getEmail());
        }
        // update password if different from existing password and not empty string
        if(!passwordEncoder.matches(record.getPassword(), pharmacy.getPassword()) && !pharmacy.getPassword().equalsIgnoreCase("")) {
            record.setPassword(passwordEncoder.encode(pharmacy.getPassword()));
        }
        // do not support update of PSI Registration Number, Address, or Eircode, as these should stay constant
        // if either of these properties change, this would be a new different pharmacy and should be a new record
        return pharmacyRepository.save(record);
    }

    //////////////////////////////////////////////////////// DELETE ACCOUNT ///////////////////////////////////////////////////////////

    @Transactional
    @Override
    public void delete(String password, PharmacyEntity pharmacy) {

        // check record exists
        if(!isPresent(pharmacy.getPsiRegNo())){
            // if no record exists throw an exception
            throw new UsernameNotFoundException("No account found.");
        }

        // check if the password entered does not match the password in the database
        if(!passwordEncoder.matches(password, pharmacy.getPassword())) {
            // incorrect password, throw exception
            throw new BadCredentialsException("Incorrect password. Please try again.");
        }

        // delete any associated stock availability listings
        pharmacyDrugAvailabilityService.deleteAllByPharmacy(pharmacy);
        // delete pharmacy record from database
        pharmacyRepository.deleteById(pharmacy.getPsiRegNo());

    }

    ///////////////////////////////////////////////////// CHECK ACCOUNT EXISTS /////////////////////////////////////////////////////////

    @Override
    public boolean isPresent(Integer psiRegNo) {
        return pharmacyRepository.existsById(psiRegNo);
    }

}
