package com.cmcilroy.medicines_shortages_assistant.parsers;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// class to represent XML root (IcFile) and allow Jackson to unwrap the list

@Data
@AllArgsConstructor
@NoArgsConstructor
@JacksonXmlRootElement(localName = "IcFile")
public class InterchangeablesWrapper {

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "IcList")
    private List<Interchangeable> interchangeables;

}
