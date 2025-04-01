package com.cmcilroy.medicines_shortages_assistant.parsers;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// this class represents the required data associated with each Product in the HPRA Authorised Human Medicines XML list

@JacksonXmlRootElement(localName = "Product")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @JacksonXmlProperty(localName = "LicenceNumber")
    private String licenceNo;

    @JacksonXmlProperty(localName = "ProductName")
    private String productName;

    @JacksonXmlProperty(localName = "PAHolder")
    private String manufacturer;

    @JacksonXmlProperty(localName = "DosageForm")
    private String dosageForm;

    @JacksonXmlProperty(localName = "ActiveSubstances")
    private List<String> activeSubstances;

    // method to convert the activeSubstances List to a comma separated String (CSV)
    public String getActiveSubstance() {
        if (activeSubstances == null || activeSubstances.isEmpty()) {
            return "";
        }
        StringBuilder activeSubstance = new StringBuilder(activeSubstances.get(0));
        for(int i = 1; i <= activeSubstances.size()-1; i++){
            // if active substance is different from the element before it, concatenate it
            if(!activeSubstances.get(i).equals(activeSubstances.get(i-1))) {
                activeSubstance.append(", ").append(activeSubstances.get(i));
            }
        }
        return activeSubstance.toString();
    }

}
