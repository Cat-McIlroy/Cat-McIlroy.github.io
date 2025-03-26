package com.cmcilroy.medicines_shortages_assistant.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.cmcilroy.medicines_shortages_assistant.domain.entities.DrugEntity;
import com.cmcilroy.medicines_shortages_assistant.repositories.DrugRepository;
import com.cmcilroy.medicines_shortages_assistant.services.DrugService;

@Service
public class DrugServiceImpl implements DrugService{

    // inject Repository
    private DrugRepository drugRepository;

    // active substance synonym map
    private Map<String, List<String>> SYNONYM_MAP = Map.of(
        "aspirin", List.of(
            "aspirin",
            "acetylsalicylic acid",
            "acetylsalicilic acid",
            "asa",
            "2-(acetyloxy)benzoic acid",
            "salicylic acid acetate"
        ),
        "paracetamol", List.of(
            "paracetamol",
            "acetaminophen"
        )
        
    );

    private String[] salts = {
        " acetate",
        " arginine",
        " besilate", 
        " maleate", 
        " mesilate",
        " medoxomil",
        " chloride", 
        " carbonate",
        " hydrochloride", 
        " dihydrochloride", 
        " potassium", 
        " phosphate",
        " sodium", 
        " disodium",
        " sodium phosphate",
        " citrate",
        " gluconate",
        " sulfate",
        " sulphate",
        " tartrate",
        " fumarate",
        " hemifumarate",
        " dinitrate",
        " benzoate",
        " monohydrate",
        " dimesylate",
        " hemihydrate"
        };


    public DrugServiceImpl(DrugRepository drugRepository) {
        this.drugRepository = drugRepository;
    }

    // pass-through method. The Service layer is taking the Entity and passing it to the Repository which persists it in the database
    @Override
    public DrugEntity saveDrug(String licenceNo, DrugEntity drug) {
        // ensure the licenceNo associated with the drug object to be saved is the same as the licenceNo in the URL
        drug.setLicenceNo(licenceNo);
        // save returns an Entity by default
        return drugRepository.save(drug);
    }

    @Override
    public Page<DrugEntity> findAll(Pageable pageable) {
        return drugRepository.findAll(pageable);
    }

    @Override
    public Iterable<DrugEntity> findAll() {
        return drugRepository.findAll();
    }

    @Override
    public Page<DrugEntity> findAllByActiveSubstance(String activeSubstance, Pageable pageable) {
        // allowing for drug synonyms, search synonym map keys and values
        // call entrySet method to retrieve a set of key-value pairs from the map, convert the set to a stream
        List<String> synonyms = SYNONYM_MAP.entrySet().stream()
            // check each entry to see if the active substance matches the key or is present in the list of values for that entry
            .filter(entry -> entry.getValue().stream()
                // any match returns true if any value matches the active substance
                .anyMatch(syn -> syn.equalsIgnoreCase(activeSubstance)))
            // extract the list of values from the map entry
            .map(Map.Entry::getValue)
            // take the first matching list of values and wrap it in an optional
            .findFirst()
            // if no synonyms found just use the active substance originally entered
            .orElse(List.of(activeSubstance)); 

        List<DrugEntity> results = new ArrayList<>();
        // for each synonym in the list of synonyms
        for (String synonym : synonyms) {
            // search the database using that active ingredient and add results to the list
            results.addAll(drugRepository.findAllByActiveSubstanceIgnoreCase(synonym));
            // for each salt in the salts array
            for(String salt : salts) {
                // append it to the synonym and search the database for it, and add results to the list
                results.addAll(drugRepository.findAllByActiveSubstanceIgnoreCase(synonym + salt));
            }
        }

        // convert the final results list into a Page and return it
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), results.size());

        if (start >= results.size()) {
            // if start is beyond the size of the results, return an empty page
            return new PageImpl<>(new ArrayList<>(), pageable, results.size());
        }
        List<DrugEntity> pagedResults = results.subList(start, end);

        return new PageImpl<DrugEntity>(pagedResults, pageable, results.size());

    }

    @Override
    public Page<DrugEntity> findAllByComboActiveSubstances(String activeSubstance, Pageable pageable) {
        // list to hold the search results
        List<DrugEntity> results = new ArrayList<>();
        // search the database using the combination of active substances passed in and add results to the list
        results.addAll(drugRepository.findAllByComboActiveSubstances(activeSubstance));
        // split the activeSubstance string into an array of Strings on the commas, to get an array of the active substances in the combination
        String[] actives = activeSubstance.split(",");
        // for each individual active substance in the combination of active substances
        for (int i = 0; i < actives.length; i++) {
            // store current active in a new variable for use in the lambda expression
            String currentActive = actives[i];
            // search the synonym map for matches
            // call entrySet method to retrieve a set of key-value pairs from the map, convert the set to a stream
            List<String> synonyms = SYNONYM_MAP.entrySet().stream()
            // check each entry to see if the active substance matches the key or is present in the list of values for that entry
            .filter(entry -> entry.getValue().stream()
                // any match returns true if any value matches the active substance
                .anyMatch(syn -> syn.equalsIgnoreCase(currentActive)))
            // extract the list of values from the map entry
            .map(Map.Entry::getValue)
            // take the first matching list of values and wrap it in an optional
            .findFirst()
            // if no synonyms found just use the active substance originally entered
            .orElse(List.of(currentActive));
            // for each synonym in the list of synonyms
            for (String synonym : synonyms) {
                // replace the element at index i of actives with the synonym
                actives[i] = synonym;
                // concatenate the actives array back into a String
                String activeCombo = String.join(", ", actives);
                // search the database for this combination and add any results to the list
                results.addAll(drugRepository.findAllByComboActiveSubstances(activeCombo));
            }
            // change element at i back to original active
            actives[i] = currentActive;
        }

        // convert the final results list into a Page and return it
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), results.size());

        if (start >= results.size()) {
            // if start is beyond the size of the results, return an empty page
            return new PageImpl<>(new ArrayList<>(), pageable, results.size());
        }
        List<DrugEntity> pagedResults = results.subList(start, end);

        return new PageImpl<DrugEntity>(pagedResults, pageable, results.size());

    }

    @Override
    public Optional<DrugEntity> findOne(String licenceNo) {
        return drugRepository.findById(licenceNo);
    }

    @Override
    public Optional<DrugEntity> findByContainsProductName(String productName) {
        List<DrugEntity> drugsList = drugRepository.findByContainsProductName(productName);
        if (drugsList.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(drugsList.getFirst());
    }

    @Override
    public Optional<DrugEntity> findByExactProductName(String productName) {
            return drugRepository.findByProductName(productName);
    }

    @Override
    public Iterable<DrugEntity> findAllByIsAvailable(boolean isAvailable) {
        return drugRepository.findAllByIsAvailable(isAvailable);
    }

    @Override
    public boolean isPresent(String licenceNo) {
        return drugRepository.existsById(licenceNo);
    }

    @Override
    public DrugEntity partialUpdate(String licenceNo, DrugEntity drug) {
        // make sure licenceNo of the drug entity passed in is the same as the licenceNo in the URL
        drug.setLicenceNo(licenceNo);
        // retrieve record from the database
        return drugRepository.findById(licenceNo).map(existingRecord -> {
            // update product name (useful in the case of a re-brand)
            Optional.ofNullable(drug.getProductName()).ifPresent(existingRecord::setProductName);
            // update product availability 
            Optional.ofNullable(drug.getIsAvailable()).ifPresent(existingRecord::setIsAvailable);
            // do not support partial update of licence number, active substance or strength, as these should stay constant
            // if the active substance or strength changed, this would be a different product with a different licence number
            return drugRepository.save(existingRecord);
        }).orElseThrow(() -> new RuntimeException("Record does not exist."));
    }

    @Override
    public void delete(String licenceNo) {
        drugRepository.deleteById(licenceNo);
    }
    
}
