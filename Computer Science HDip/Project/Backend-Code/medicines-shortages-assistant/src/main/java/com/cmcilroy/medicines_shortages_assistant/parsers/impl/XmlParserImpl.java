package com.cmcilroy.medicines_shortages_assistant.parsers.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import com.cmcilroy.medicines_shortages_assistant.domain.dto.DrugDto;
import com.cmcilroy.medicines_shortages_assistant.mappers.Mapper;
import com.cmcilroy.medicines_shortages_assistant.parsers.Interchangeable;
import com.cmcilroy.medicines_shortages_assistant.parsers.InterchangeablesWrapper;
import com.cmcilroy.medicines_shortages_assistant.parsers.Product;
import com.cmcilroy.medicines_shortages_assistant.parsers.ProductsWrapper;
import com.cmcilroy.medicines_shortages_assistant.parsers.XmlParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

@Component
public class XmlParserImpl implements XmlParser {

    // inject product mapper
    private Mapper<Product, DrugDto> productMapper;
    private XmlMapper xmlMapper;

    public XmlParserImpl(Mapper<Product, DrugDto> productMapper){
        this.productMapper = productMapper;
        this.xmlMapper = new XmlMapper();
        // configure XmlMapper to ignore unrecognised properties
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public List<DrugDto> parseDrugData() {

        // specify files
        File authorisedMedicines = new File(System.getProperty("user.dir") + "/latestHumanlist.xml");
        File interchangeablesList = new File(System.getProperty("user.dir") + "/latestInterchangeableslist.xml");

        // use ArrayList to hold list of DrugDto's
        List<DrugDto> drugs = new ArrayList<>();

        // use HashMap to hold the licenceNo and strength variables associated with the Interchangeables
        Map<String, String> icMap = new HashMap<>();

        try{
            // try reading Authorised Medicines XML file and mapping to DrugDto
            ProductsWrapper productsWrapper = xmlMapper.readValue(authorisedMedicines, ProductsWrapper.class);
            List<Product> products = productsWrapper.getProducts();
            for (Product product : products) {
                // map Product to DrugDto and add this to ArrayList
                drugs.add(productMapper.mapTo(product));
            }
        }
        // handle exception
        catch(IOException e){
            System.err.println("Error parsing input file: " + authorisedMedicines + "\n" + e.getMessage());
        }

        try{
            // try reading Interchangeables XML file
            InterchangeablesWrapper icWrapper = xmlMapper.readValue(interchangeablesList, InterchangeablesWrapper.class);
            List<Interchangeable> interchangeables = icWrapper.getInterchangeables();
            // for each interchangeable
            for(Interchangeable interchangeable : interchangeables) {
                // insert relevant data in the map using put method
                // licenceNo is the key, strength is the value
                icMap.put(interchangeable.getLicenceNo(), interchangeable.getStrength());
            }
        }
        // handle exception
        catch(IOException e){
            System.err.println("Error parsing input file: " + interchangeablesList + "\n" + e.getMessage());
        }

        // for each drug in the list
        for(DrugDto drug : drugs) {
            // set the strength variable of the DrugDto to be the value in the icMap associated with the DrugDto licenceNo
            // returns null if not found
            drug.setStrength(icMap.get(drug.getLicenceNo()));
        }

        return drugs;
        
    }

    @Override
    public List<DrugDto> parseWithdrawnList() {
    
        // specify file
        File withdrawnList = new File(System.getProperty("user.dir") + "/withdrawnHumanlist.xml");

        // use ArrayList to hold list of DrugDto's
        List<DrugDto> withdrawnDrugs = new ArrayList<>();

        try{
            // try reading Withdrawn Medicines XML file and mapping to DrugDto
            ProductsWrapper productsWrapper = xmlMapper.readValue(withdrawnList, ProductsWrapper.class);
            List<Product> products = productsWrapper.getProducts();
            for (Product product : products) {
                // map Product to DrugDto and add this to ArrayList
                withdrawnDrugs.add(productMapper.mapTo(product));
            }
        }
        // handle exception
        catch(IOException e){
            System.err.println("Error parsing input file: " + withdrawnList + "\n" + e.getMessage());
        }

        return withdrawnDrugs;
        
    }

}
