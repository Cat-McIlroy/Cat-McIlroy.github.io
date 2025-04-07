package com.cmcilroy.medicines_shortages_assistant.services.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;
import com.cmcilroy.medicines_shortages_assistant.domain.entities.DrugEntity;
import com.cmcilroy.medicines_shortages_assistant.repositories.DrugRepository;
import com.cmcilroy.medicines_shortages_assistant.services.DrugService;

@Service
public class DrugServiceImpl implements DrugService{

    // inject Repository
    private DrugRepository drugRepository;

    // active substance synonym map
    private Map<String, List<String>> synonymMap = Map.of(
        "aspirin", List.of(
            "aspirin",
            "acetylsalicylic acid",
            "acetyl salicylic acid",
            "acetylsalicilic acid"
        ),
        "paracetamol", List.of(
            "paracetamol",
            "acetaminophen"
        ),
        "hydrochlorothiazide", List.of(
            "hydrochlorothiazide",
            "HCTZ"
        ),
        "folic acid", List.of(
            "folic acid",
            "folate"
        )   
    );

    public DrugServiceImpl(DrugRepository drugRepository) {
        this.drugRepository = drugRepository;
    }

    @Override
    public Iterable<DrugEntity> findAll() {
        return drugRepository.findAll();
    }

    @Override
    public List<DrugEntity> findAllByActiveSubstance(String activeSubstance) {
        // allowing for drug synonyms, search synonym map keys and values
        // call entrySet method to retrieve a set of key-value pairs from the map, convert the set to a stream
        List<String> synonyms = synonymMap.entrySet().stream()
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

        Set<DrugEntity> results = new HashSet<>();
        // for each synonym in the list of synonyms
        for (String synonym : synonyms) {
            // search the database using that active ingredient and add results to the list
            results.addAll(drugRepository.findAllByContainsActiveSubstance(synonym));
        }

        List<DrugEntity> filteredResults = new ArrayList<>();
        // now filter for only entries which do not contain commas (those are combination drugs)
        for(DrugEntity result : results) {
            // allow for quirk in Utrogestan 200mg record, where the active is 'PROGESTERONE, MICRONISED'
            if(result.getActiveSubstance().equalsIgnoreCase("Progesterone, Micronised")) {
                filteredResults.add(result);
            }
            else if(!result.getActiveSubstance().contains(",")) {
                filteredResults.add(result);
            }
        }

        return filteredResults;

    }

    @Override
    public List<DrugEntity> findAllByComboActiveSubstances(String activeSubstance) {
        // list to hold the search results
        Set<DrugEntity> results = new HashSet<>();
        // retrieve all records from the database drugs table
        Iterable<DrugEntity> allDrugs = findAll();
        // split the activeSubstance string into an array of Strings on the commas, to get an array of the active substances in the combination
        String[] actives = activeSubstance.split(",");
        List<String> activesList = Arrays.asList(actives);
        // for each drug in the database
        for(DrugEntity drug : allDrugs) {
            // get the active substance(s)
            String comparisonActive = drug.getActiveSubstance();
            // split the active substance into an array of Strings on the commas
            String[] comparisonArr = comparisonActive.split(",");
            List<String> comparisonArrList = Arrays.asList(comparisonArr);
            // for each active in the list of active substances from the search term,
            // if any element in the list of active substances for comparison contains that active (allows for variations of drug salts used)
            // return true if all actives in the list of active substances from the search term have a match
            boolean containsAll = activesList.stream()
                                .allMatch(active -> comparisonArrList.stream()
                                    .anyMatch(comparison -> comparison.toLowerCase().contains(active.toLowerCase().trim()))
                                );
            if(containsAll) {
                // add the drug to the results list
                results.add(drug);
            }
            // check if the activesList contains any actives which are in the synonym map (either keys or values)
            // if so that active needs to be swapped out with each of its synonyms and each combination searched for
            if(
                activesList.stream().anyMatch(element -> 
                synonymMap.containsKey(element) || 
                synonymMap.values().stream().anyMatch(list -> list.contains(element)))
            ) {
                // make a copy of the activesList
                List<String> activesSynonyms = new ArrayList<>(activesList);

                // for each element in the list (each active substance)
                for(int i = 0; i < activesSynonyms.size(); i++) {

                    // hold the current element
                    String current = activesSynonyms.get(i);

                    // find if it has synonyms
                    List<String> synonyms = synonymMap.entrySet().stream()
                    // check each entry to see if the active substance matches the key or is present in the list of values for that entry
                    .filter(entry -> entry.getValue().stream()
                        // any match returns true if any value matches the active substance
                        .anyMatch(syn -> syn.equalsIgnoreCase(current)))
                    // extract the list of values from the map entry
                    .map(Map.Entry::getValue)
                    // take the first matching list of values and wrap it in an optional
                    .findFirst()
                    // if no synonyms found just use the active substance originally entered
                    .orElse(List.of(current)); 

                    // check if the synonyms list is longer than 1 element (i.e. the active has synonyms)
                    if(synonyms.size() > 1) {
                        // if so, for each synonym in the list
                        for(String synonym : synonyms) {
                            // replace the current element with that synonym
                            activesSynonyms.set(i, synonym);
                            // for each active in the synonym-swapped list of active substances from the search term,
                            // if any element in the list of active substances for comparison contains that active (allows for variations of drug salts used)
                            // return true if all actives in the list of active substances from the search term have a match
                            boolean containsAllSynonyms = activesSynonyms.stream()
                                .allMatch(activeSynonym -> comparisonArrList.stream()
                                    .anyMatch(comparison -> comparison.toLowerCase().contains(activeSynonym.toLowerCase().trim()))
                                );
                            // search for that combination
                            if(containsAllSynonyms) {
                                // add the drug to the results list
                                results.add(drug);
                            }
                        }
                    }
                }
            }
        }

        // instantiate list to hold filtered results
        List<DrugEntity> filteredResults = new ArrayList<>();

        // for each result in the results set
        for(DrugEntity result : results) {
            String drugActives = result.getActiveSubstance();
            String[] drugActivesArr = drugActives.split(",");
            // filter results to drugs containing only the same number of active ingredients as the search term
            // this will limit results to only drugs containing exactly the same combination as the search term
            if(drugActivesArr.length == actives.length) {
                filteredResults.add(result);
            }
        }

        return filteredResults;

    }

    @Override
    public Optional<DrugEntity> findOne(String licenceNo) {
        return drugRepository.findById(licenceNo);
    }

    @Override
    public List<DrugEntity> findByContainsProductName(String productName) {
        return drugRepository.findByContainsProductName(productName);
    }

    @Override
    public Iterable<DrugEntity> findAllByIsAvailable(boolean isAvailable) {
        return drugRepository.findAllByIsAvailable(isAvailable);
    }
    
}
