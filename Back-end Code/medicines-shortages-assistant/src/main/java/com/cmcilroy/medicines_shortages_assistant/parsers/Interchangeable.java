package com.cmcilroy.medicines_shortages_assistant.parsers;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// this class represents the required data associated with each IcList in the HPRA Interchangeable Medicines XML list

@JacksonXmlRootElement(localName = "IcList")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Interchangeable {

    @JacksonXmlProperty(localName = "LicenseNumber")
    private String licenceNo;

    @JacksonXmlProperty(localName = "ProductStrength")
    private String strength;

}
