package com.cmcilroy.medicines_shortages_assistant.repositories;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.assertj.core.api.Assertions.assertThat;
import com.cmcilroy.medicines_shortages_assistant.TestData;
import com.cmcilroy.medicines_shortages_assistant.cleaner.DatabaseCleaner;
import com.cmcilroy.medicines_shortages_assistant.domain.entities.PharmacyEntity;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class PharmacyRepositoryIntegrationTests {

    // class under test
    private PharmacyRepository underTest;

    // inject DatabaseCleaner
    private DatabaseCleaner databaseCleaner;

    // constructor dependency injection
    @Autowired
    public PharmacyRepositoryIntegrationTests(PharmacyRepository underTest, DatabaseCleaner databaseCleaner) {
        this.underTest = underTest;
        this.databaseCleaner = databaseCleaner;
    }


///////////////////////////////////////////////// CLEAR DATABASE BEFORE EACH TEST ////////////////////////////////////////////////////

    @BeforeEach
    public void clearDatabase() {
        databaseCleaner.clearDatabase();
    }

    // tests that a pharmacy entity can be correctly created in the database (in the pharmacies table) and can subsequently be retrieved 
    @Test
    public void testThatPharmacyCanBeCreatedAndRecalled() {
        // instantiate test PharmacyEntity
        PharmacyEntity pharmacy = TestData.createTestPharmacyA();
        underTest.save(pharmacy);
        // use Optional so that it can be empty and doesn't just return null if no result
        Optional<PharmacyEntity> result = underTest.findById(pharmacy.getPsiRegNo());
        // assert that a result has been returned
        assertThat(result).isPresent();
        // assert that the result is equal to the pharmacy entity which was created
        assertThat(result.get()).isEqualTo(pharmacy);
    }

    // tests that multiple pharmacy entities can be correctly created in the database (in the pharmacies table) and can subsequently be retrieved 
    @Test
    public void testThatMultiplePharmaciesCanBeCreatedAndRecalled() {
        // instantiate test PharmacyEntity objects
        PharmacyEntity pharmacyA = TestData.createTestPharmacyA();
        underTest.save(pharmacyA);
        PharmacyEntity pharmacyB = TestData.createTestPharmacyB();
        underTest.save(pharmacyB);
        PharmacyEntity pharmacyC = TestData.createTestPharmacyC();
        underTest.save(pharmacyC);

        // the findAll method returns an Iterable
        Iterable<PharmacyEntity> result = underTest.findAll();
        // assert that a result is of size 3 and contains exactly the PharmacyEntity objects saved above
        assertThat(result).
            hasSize(3).
            containsExactlyInAnyOrder(pharmacyA, pharmacyB, pharmacyC);
    }

    // tests that a pharmacy entity can be correctly updated in the database (in the pharmacies table)
    @Test
    public void testThatPharmacyCanBeUpdated() {
        // instantiate test PharmacyEntity object
        PharmacyEntity pharmacy = TestData.createTestPharmacyA();
        // save it to database
        underTest.save(pharmacy);
        // update the pharmacy name
        pharmacy.setPharmacyName("Updated Pharmacy Name");
        // save the updated pharmacy to database
        underTest.save(pharmacy);
        Optional<PharmacyEntity> result = underTest.findById(pharmacy.getPsiRegNo());
        // assert that the result is not empty, and that the result matches the updated pharmacy
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(pharmacy);
    }

    // tests that a pharmacy entity can be deleted from the database
    @Test
    public void testThatPharmacyCanBeDeleted() {
        // instantiate test PharmacyEntity object
        PharmacyEntity pharmacy = TestData.createTestPharmacyA();
        // save it to database
        underTest.save(pharmacy);
        // delete by Id
        underTest.deleteById(pharmacy.getPsiRegNo());
        // use Optional so that it can be empty and doesn't just return null if no result
        Optional<PharmacyEntity> result = underTest.findById(pharmacy.getPsiRegNo());
        // assert that the result is empty (i.e. the record could not be found)
        assertThat(result).isEmpty();
    }

}