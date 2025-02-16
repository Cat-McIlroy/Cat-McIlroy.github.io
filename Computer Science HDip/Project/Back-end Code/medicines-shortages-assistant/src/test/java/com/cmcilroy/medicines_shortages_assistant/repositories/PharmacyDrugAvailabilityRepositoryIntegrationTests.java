package com.cmcilroy.medicines_shortages_assistant.repositories;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.assertj.core.api.Assertions.assertThat;
import com.cmcilroy.medicines_shortages_assistant.TestData;
import com.cmcilroy.medicines_shortages_assistant.domain.entities.DrugEntity;
import com.cmcilroy.medicines_shortages_assistant.domain.entities.PharmacyDrugAvailabilityEntity;
import com.cmcilroy.medicines_shortages_assistant.domain.entities.PharmacyEntity;

@SpringBootTest
@ExtendWith(SpringExtension.class)
// use DirtiesContext to clean the context after each test and prevent test pollution
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class PharmacyDrugAvailabilityRepositoryIntegrationTests {

    // class under test
    private PharmacyDrugAvailabilityRepository underTest;

    // repository classes
    private PharmacyRepository pharmacyRepository;
    private DrugRepository drugRepository;

    // constructor dependency injection
    @Autowired
    public PharmacyDrugAvailabilityRepositoryIntegrationTests(PharmacyDrugAvailabilityRepository underTest, PharmacyRepository pharmacyRepository, DrugRepository drugRepository) {
        this.underTest = underTest;
        this.pharmacyRepository = pharmacyRepository;
        this.drugRepository = drugRepository;
    }

    // tests that a pharmacy drug availability entity can be correctly created in the database (in the pharmacy_drug_availabilities table) and can subsequently be retrieved 
    @Test
    public void testThatPharmacyDrugAvailabilityCanBeCreatedAndRecalled() {
        // instantiate PharmacyEntity for use with test method
        PharmacyEntity pharmacy = TestData.createTestPharmacyA();
        pharmacyRepository.save(pharmacy);
        // instantiate DrugEntity for use with test method
        DrugEntity drug = TestData.createTestDrugA();
        drugRepository.save(drug);
        // instantiate test PharmacyDrugAvailabilityEntity, passing in test pharmacy and test drug
        PharmacyDrugAvailabilityEntity pharmacyDrugAvailability = TestData.createTestPharmacyDrugAvailabilityA(pharmacy, drug);
        underTest.save(pharmacyDrugAvailability);
        // use Optional so that it can be empty and doesn't just return null if no result
        Optional<PharmacyDrugAvailabilityEntity> result = underTest.findById(pharmacyDrugAvailability.getId());
        // assert that a result has been returned
        assertThat(result).isPresent();
        // assert that the result is equal to the pharmacy entity which was created
        assertThat(result.get()).isEqualTo(pharmacyDrugAvailability);
    }

    // tests that multiple pharmacy drug availability entities can be correctly created in the database (in the pharmacy_drug_availabilities table) and can subsequently be retrieved
    @Test
    public void testThatMultiplePharmacyDrugAvailabilitiesCanBeCreatedAndRecalled() {
        // instantiate PharmacyEntity objects for use with test method
        PharmacyEntity pharmacyA = TestData.createTestPharmacyA();
        PharmacyEntity pharmacyB = TestData.createTestPharmacyB();
        PharmacyEntity pharmacyC = TestData.createTestPharmacyC();
        pharmacyRepository.save(pharmacyA);
        pharmacyRepository.save(pharmacyB);
        pharmacyRepository.save(pharmacyC);
        

        // instantiate DrugEntity objects for use with test method
        DrugEntity drugA = TestData.createTestDrugA();
        DrugEntity drugB = TestData.createTestDrugB();
        DrugEntity drugC = TestData.createTestDrugC();
        drugRepository.save(drugA);
        drugRepository.save(drugB);
        drugRepository.save(drugC);

        // instantiate test PharmacyDrugAvailabilityEntity objects, passing in test pharmacy and test drug objects
        PharmacyDrugAvailabilityEntity pharmacyDrugAvailabilityA = TestData.createTestPharmacyDrugAvailabilityA(pharmacyA, drugA);
        underTest.save(pharmacyDrugAvailabilityA);
        PharmacyDrugAvailabilityEntity pharmacyDrugAvailabilityB = TestData.createTestPharmacyDrugAvailabilityB(pharmacyB, drugB);
        underTest.save(pharmacyDrugAvailabilityB);
        PharmacyDrugAvailabilityEntity pharmacyDrugAvailabilityC = TestData.createTestPharmacyDrugAvailabilityC(pharmacyC, drugC);
        underTest.save(pharmacyDrugAvailabilityC);

        // the findAll method returns an Iterable
        Iterable<PharmacyDrugAvailabilityEntity> result = underTest.findAll();
        // assert that a result is of size 3 and contains exactly the DrugEntity objects saved above
        assertThat(result).
            hasSize(3).
            containsExactly(pharmacyDrugAvailabilityA, pharmacyDrugAvailabilityB, pharmacyDrugAvailabilityC);
    }

    // tests that a pharmacy drug availability entity can be correctly updated in the database (in the pharmacy_drug_availabilities table)
    @Test
    public void testThatPharmacyDrugAvailabilityEntityCanBeUpdated() {
        // instantiate PharmacyEntity for use with test method
        PharmacyEntity pharmacy = TestData.createTestPharmacyA();
        pharmacyRepository.save(pharmacy);
        // instantiate DrugEntity for use with test method
        DrugEntity drug = TestData.createTestDrugA();
        drugRepository.save(drug);
        // instantiate test PharmacyDrugAvailabilityEntity, passing in test pharmacy and test drug
        PharmacyDrugAvailabilityEntity pharmacyDrugAvailability = TestData.createTestPharmacyDrugAvailabilityA(pharmacy, drug);
        underTest.save(pharmacyDrugAvailability);
        // update the availability status from true to false
        pharmacyDrugAvailability.setIsAvailable(false);
        // save the updated PharmacyDrugAvailabilityEntity to the database
        underTest.save(pharmacyDrugAvailability);
        // use Optional so that it can be empty and doesn't just return null if no result
        Optional<PharmacyDrugAvailabilityEntity> result = underTest.findById(pharmacyDrugAvailability.getId());
        // assert that a result has been returned
        assertThat(result).isPresent();
        // assert that the result is equal to the pharmacy entity which was created
        assertThat(result.get()).isEqualTo(pharmacyDrugAvailability);
    }

    // tests that a pharmacy drug availability entity can be deleted from the database
    @Test
    public void testThatPharmacyDrugAvailabilityCanBeDeleted() {
        // instantiate PharmacyEntity for use with test method
        PharmacyEntity pharmacy = TestData.createTestPharmacyA();
        pharmacyRepository.save(pharmacy);
        // instantiate DrugEntity for use with test method
        DrugEntity drug = TestData.createTestDrugA();
        drugRepository.save(drug);
        // instantiate test PharmacyDrugAvailabilityEntity, passing in test pharmacy and test drug
        PharmacyDrugAvailabilityEntity pharmacyDrugAvailability = TestData.createTestPharmacyDrugAvailabilityA(pharmacy, drug);
        underTest.save(pharmacyDrugAvailability);
        // delete by Id
        underTest.deleteById(pharmacyDrugAvailability.getId());
        // use Optional so that it can be empty and doesn't just return null if no result
        Optional<PharmacyDrugAvailabilityEntity> result = underTest.findById(pharmacyDrugAvailability.getId());
        // assert that the result is empty (i.e. the record could not be found)
        assertThat(result).isEmpty();
    }

}
