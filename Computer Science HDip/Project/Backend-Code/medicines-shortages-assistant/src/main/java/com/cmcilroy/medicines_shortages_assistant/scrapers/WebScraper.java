package com.cmcilroy.medicines_shortages_assistant.scrapers;

import java.util.List;

public interface WebScraper {

    List<String> scrapeUnavailableDrugs();

    boolean validatePsiRegNumber(Integer psiRegNo);

}
