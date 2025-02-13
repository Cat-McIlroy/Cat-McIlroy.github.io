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

@SpringBootTest
@ExtendWith(SpringExtension.class)
// use DirtiesContext to clean the context after each test and prevent test pollution
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class DrugRepositoryIntegrationTests {

    // class under test
    private DrugRepository underTest;

    // constructor dependency injection
    @Autowired
    public DrugRepositoryIntegrationTests(DrugRepository underTest) {
        this.underTest = underTest;
    }

    // tests that a drug entity can be correctly created in the database (in the drugs table) and can subsequently be retrieved 
    @Test
    public void testThatDrugCanBeCreatedAndRecalled() {
        // instantiate test DrugEntity
        DrugEntity drug = TestData.createTestDrugA();
        underTest.save(drug);
        // use Optional so that it can be empty and doesn't just return null if no result
        Optional<DrugEntity> result = underTest.findById(drug.getLicenceNo());
        // assert that a result has been returned
        assertThat(result).isPresent();
        // assert that the result is equal to the drug entity which was created
        assertThat(result.get()).isEqualTo(drug);
    }

    // tests that multiple drug entities can be correctly created in the database (in the drugs table) and can subsequently be retrieved 
    @Test
    public void testThatMultipleDrugsCanBeCreatedAndRecalled() {
        // instantiate test DrugEntity objects
        DrugEntity drugA = TestData.createTestDrugA();
        underTest.save(drugA);
        DrugEntity drugB = TestData.createTestDrugB();
        underTest.save(drugB);
        DrugEntity drugC = TestData.createTestDrugC();
        underTest.save(drugC);

        // the findAll method returns an Iterable
        Iterable<DrugEntity> result = underTest.findAll();
        // assert that a result is of size 3 and contains exactly the DrugEntity objects saved above
        assertThat(result).
            hasSize(3).
            containsExactly(drugA, drugB, drugC);
    }

    // tests that a drug entity can be correctly updated in the database (in the drugs table)
    @Test
    public void testThatDrugCanBeUpdated() {
        // instantiate test DrugEntity object
        DrugEntity drug = TestData.createTestDrugA();
        // save it to database
        underTest.save(drug);
        // update the drug name
        drug.setProductName("Updated Drug Name");
        // save the updated drug to database
        underTest.save(drug);
        // use Optional so that it can be empty and doesn't just return null if no result
        Optional<DrugEntity> result = underTest.findById(drug.getLicenceNo());
        // assert that the result is not empty, and that the result matches the updated drug
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(drug);
    }

    // tests that a drug entity can be deleted from the database
    @Test
    public void testThatDrugCanBeDeleted() {
        // instantiate test DrugEntity object
        DrugEntity drug = TestData.createTestDrugA();
        // save it to database
        underTest.save(drug);
        // delete by Id
        underTest.deleteById(drug.getLicenceNo());
        // use Optional so that it can be empty and doesn't just return null if no result
        Optional<DrugEntity> result = underTest.findById(drug.getLicenceNo());
        // assert that the result is empty (i.e. the record could not be found)
        assertThat(result).isEmpty();
    }

}
