package com.cmcilroy.medicines_shortages_assistant.parsers;

import java.util.List;
import com.cmcilroy.medicines_shortages_assistant.domain.dto.DrugDto;

public interface XmlParser {

    List<DrugDto> parseDrugData();

    List<DrugDto> parseWithdrawnList();

}
