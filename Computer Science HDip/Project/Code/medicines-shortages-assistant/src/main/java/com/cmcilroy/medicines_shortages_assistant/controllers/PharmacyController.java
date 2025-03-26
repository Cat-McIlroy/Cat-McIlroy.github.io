package com.cmcilroy.medicines_shortages_assistant.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.cmcilroy.medicines_shortages_assistant.domain.dto.AccountCredentialsDto;
import com.cmcilroy.medicines_shortages_assistant.domain.dto.EditAccountDto;
import com.cmcilroy.medicines_shortages_assistant.domain.dto.PharmacyDto;
import com.cmcilroy.medicines_shortages_assistant.domain.entities.PharmacyEntity;
import com.cmcilroy.medicines_shortages_assistant.mappers.Mapper;
import com.cmcilroy.medicines_shortages_assistant.services.PharmacyDrugAvailabilityService;
import com.cmcilroy.medicines_shortages_assistant.services.PharmacyService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
public class PharmacyController {

    // inject PharmacyService, PharmacyDrugAvailabilityService and Mapper
    private PharmacyService pharmacyService;
    private PharmacyDrugAvailabilityService pharmacyDrugAvailabilityService;

    private Mapper<PharmacyEntity, PharmacyDto> pharmacyMapper;

    // constructor injection
    public PharmacyController(PharmacyService pharmacyService, 
    Mapper<PharmacyEntity, PharmacyDto> pharmacyMapper,
    PharmacyDrugAvailabilityService pharmacyDrugAvailabilityService) {
        this.pharmacyService = pharmacyService;
        this.pharmacyMapper = pharmacyMapper;
        this.pharmacyDrugAvailabilityService = pharmacyDrugAvailabilityService;
    }


    //////////////////////////////////////////////////////// ALL USERS //////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////// CHECK IF USER SIGNED IN //////////////////////////////////////////////////////

    @GetMapping(path = "/pharmacies/check-auth")
    public ResponseEntity<Map<String,Object>> checkAuthentication() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // if user is authenticated and therefore signed in
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof PharmacyDto) {

            // get the account user name from the authentication
            PharmacyDto pharmacyDto = (PharmacyDto) authentication.getPrincipal();
            String pharmacyName = pharmacyDto.getPharmacyName();
            Integer psiRegNo = pharmacyDto.getPsiRegNo();
            String address = pharmacyDto.getAddress();
            String eircode = pharmacyDto.getEircode();

            // create Map including necessary data
            Map<String, Object> data = new HashMap<>();
            data.put("pharmacyName", pharmacyName);
            data.put("authenticated", true);
            data.put("psiRegNo", psiRegNo);
            data.put("address", address);
            data.put("eircode", eircode);

            return new ResponseEntity<>(data, HttpStatus.OK);
        }
        
        // if user is not authenticated
        return new ResponseEntity<>(Map.of("authenticated", false), HttpStatus.OK);

    }

    ///////////////////////////////////////////////// REGISTER AS A PHARMACY USER ///////////////////////////////////////////////////

    @PostMapping(path = "/pharmacies/register")
    // RequestBodyAnnotation tells Spring to look at the HTTP request body for the PharmacyDto object represented as JSON and convert to Java
    // use type Object to allow PharmacyDto or Map<String, String> to be returned as required
    public ResponseEntity<Object> registerNewPharmacy(@RequestBody PharmacyDto pharmacyDto) {
        try{
            // map Dto to Entity
            PharmacyEntity pharmacyEntity = pharmacyMapper.mapFrom(pharmacyDto);
            // save the entity in the database
            PharmacyEntity savedPharmacyEntity = pharmacyService.registerNewPharmacy(pharmacyEntity);
            // map the Entity back to Dto
            PharmacyDto savedPharmacyDto = pharmacyMapper.mapTo(savedPharmacyEntity);
            // return the HTTP 201 Created status code
            return new ResponseEntity<>(savedPharmacyDto, HttpStatus.CREATED);
        }
       catch(IllegalArgumentException e){
            // print error message
            System.err.println(e);
            // return HTTP status 400 Bad Request
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST);
       }

    }

    ////////////////////////////////////////////////// SIGN IN AS A PHARMACY USER ////////////////////////////////////////////////////

    @PostMapping(path = "/pharmacies/sign-in")
    public ResponseEntity<PharmacyDto> signIn(
        @RequestBody AccountCredentialsDto credentialsDto,
        HttpServletRequest httpServletRequest
    ) {
        try{
            // pass account credentials to PharmacyService signIn method
            Optional<PharmacyEntity> pharmacy = pharmacyService.signIn(credentialsDto);
            // map the returned pharmacy entity to a dto
            PharmacyDto pharmacyDto = pharmacyMapper.mapTo(pharmacy.get());
            // authenticate user with Spring Security
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                pharmacyDto, 
                null, 
                List.of(new SimpleGrantedAuthority("PHARMACY_USER"))
            );
            // store authentication in the SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authToken);
            // persist authentication
            HttpSession session = httpServletRequest.getSession(true);
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
            // return HTTP 200 Ok
            return new ResponseEntity<>(pharmacyDto, HttpStatus.OK);
        }
        catch(UsernameNotFoundException e){
            // print error message
            System.err.println(e);
            // return HTTP status Not Found
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        catch(BadCredentialsException e) {
            // print error message
            System.err.println(e);
            // return HTTP status Unauthorised
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    //////////////////////////////////////////////// PHARMACY USERS ONLY ////////////////////////////////////////////////////////////

    //////////////////////////////////////////////////// EDIT ACCOUNT DETAILS ///////////////////////////////////////////////////////

    // PATCH method to partially update a pharmacy contained in the database
    @PatchMapping(path = "/pharmacies/edit-account-details")
    // RequestBodyAnnotation tells Spring to look at the HTTP request body for the PharmacyDto object represented as JSON and convert to Java
    public ResponseEntity<PharmacyDto> updatePharmacy(@RequestBody EditAccountDto requestBody, HttpSession session) {
        // extract the pharmacyDto from the requestBody
        PharmacyDto pharmacyDto = requestBody.getPharmacy();
        // extract the password from the requestBody
        String password = requestBody.getCurrentPassword();
        try{
            // get the pharmacy dto object from the pharmacy user currently signed in
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            PharmacyDto currentUser = (PharmacyDto) authentication.getPrincipal();
            // ensure the PSI number of the current signed in user matches that of the record to be updated
            // to make sure a user can only update their own account details
            if(!currentUser.getPsiRegNo().equals(pharmacyDto.getPsiRegNo())) {
                // if the PSI numbers don't match, return a HTTP Status Forbidden
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            // map Dto to Entity
            PharmacyEntity pharmacyEntity = pharmacyMapper.mapFrom(pharmacyDto);
            // save the Entity in the database
            PharmacyEntity updatedPharmacyEntity = pharmacyService.updatePharmacy(password, pharmacyEntity);
            // map the entity back to a Dto
            PharmacyDto updatedPharmacyDto = pharmacyMapper.mapTo(updatedPharmacyEntity);
            // update the authentication
            authentication = new UsernamePasswordAuthenticationToken(
                updatedPharmacyDto,
                null, 
                List.of(new SimpleGrantedAuthority("ROLE_PHARMACY_USER")) 
            );

            // set the updated authentication in the security context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            return new ResponseEntity<>(updatedPharmacyDto, HttpStatus.OK);
        }
        catch(UsernameNotFoundException e){
            // print error message
            System.err.println(e);
            // return HTTP status Not Found
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        catch(BadCredentialsException e) {
            // print error message
            System.err.println(e);
            // return HTTP status Unauthorised
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    //////////////////////////////////////////////////////// DELETE ACCOUNT ///////////////////////////////////////////////////////////

    // DELETE method to delete a specific pharmacy from the database
    @DeleteMapping(path = "/pharmacies/delete-account")
    public ResponseEntity<Void> deletePharmacy(@RequestBody Map<String, String> requestBody) {
        // extract the password from the request body
        String password = requestBody.get("password");
        // get the pharmacy dto object from the pharmacy user currently signed in
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PharmacyDto pharmacyDto = (PharmacyDto) authentication.getPrincipal();
        // delete any associated stock availability listings
        pharmacyDrugAvailabilityService.deleteAllByPharmacy(pharmacyMapper.mapFrom(pharmacyDto));
        
        try{
            pharmacyService.delete(password, pharmacyMapper.mapFrom(pharmacyDto));
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        catch(UsernameNotFoundException e){
            // print error message
            System.err.println(e);
            // return HTTP status Not Found
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        catch(BadCredentialsException e) {
            // print error message
            System.err.println(e);
            // return HTTP status Unauthorised
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    /////////////////////////////////////////////////// CURRENTLY UNUSED ENDPOINTS ///////////////////////////////////////////////////////

    // // GET method to return a paginated list of all pharmacies contained in the database
    // @GetMapping(path = "/pharmacies")
    // public Page<PharmacyDto> listAllPharmacies(Pageable pageable) {
    //     Page<PharmacyEntity> pharmacies = pharmacyService.findAll(pageable);
    //     return pharmacies.map(pharmacyMapper::mapTo);
    // }

    // // GET method to return pharmacy by psiRegNo
    // @GetMapping(path = "/pharmacies/{psiRegNo}")
    // public ResponseEntity<PharmacyDto> getPharmacy(@PathVariable("psiRegNo") Integer psiRegNo) {
    //     Optional<PharmacyEntity> foundPharmacy = pharmacyService.findOne(psiRegNo);
    //     if(foundPharmacy.isPresent()){
    //         PharmacyEntity pharmacyEntity = foundPharmacy.get();
    //         PharmacyDto pharmacyDto = pharmacyMapper.mapTo(pharmacyEntity);
    //         return new ResponseEntity<>(pharmacyDto, HttpStatus.OK);
    //     }
    //     else{
    //         return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    //     }
    // }

}
