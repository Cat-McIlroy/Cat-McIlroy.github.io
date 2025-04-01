package com.cmcilroy.medicines_shortages_assistant.scrapers.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import com.cmcilroy.medicines_shortages_assistant.scrapers.WebScraper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class WebScraperImpl implements WebScraper {

    private ObjectMapper objectMapper;

    public WebScraperImpl() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public List<String> scrapeUnavailableDrugs() {

        // set skip to 0 initially to start from the first record
        int skip = 0;
        // mimic the real API calls made when using the HPRA website, by retrieving 10 records at a time
        int take = 10; 
        // use boolean to track when the scraper has reached the end of the available records
        // this is necessary as the number of records will vary depending on the number of current shortages
        boolean isFinished = false;
        // instantiate ArrayList to hold shorts
        List<String> shorts = new ArrayList<>();

        try{
            // while there are next pages remaining
            while(!isFinished){
                // format Request Payload
                String jsonPayload = String.format(
                    "{\"id\":null, \"skip\":%d, \"take\":%d, \"query\":null, \"order\":\"lastupdated DESC\", \"filter\":\"All\"}",
                    skip, take
                );

                // send POST request using JSoup
                Connection.Response response = Jsoup.connect(
                    
                )
                        .header("Content-Type", "application/json")
                        .header("Accept", "*/*")
                        .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)")
                        .header("Referer", "https://www.hpra.ie/find-a-medicine/for-human-use/medicine-shortages")
                        .header("Origin", "https://www.hpra.ie")
                        .requestBody(jsonPayload)
                        .method(Connection.Method.POST)
                        // ignore content type as default type for JSoup is HTML and this is not HTML
                        .ignoreContentType(true)
                        .execute();

                // capture Response Body
                String responseBody = response.body();

                // parse the Response Body
                JsonNode rootNode = objectMapper.readTree(responseBody);
                JsonNode items = rootNode.get("items");

                // set a counter for number of records returned, initialise it to zero
                int counter = 0;

                // for each product in the items array
                for(JsonNode item: items) {
                    JsonNode productLicense = item.get("productLicense");
                    String licenceNo = productLicense.get("licenseNumber").asText();
                    // add the licenceNo to the shorts list
                    shorts.add(licenceNo);
                    // increment the counter
                    counter++;
                }

                // if the number of records returned is less than 10, the end of the results has been reached
                if(counter < 10) {
                    // set isFinished to true, breaking the while loop
                    isFinished = true;
                }
                else {
                    // increment skip to go to the next page
                    skip += take;
                }
            }   
        }
        catch(IOException e) {
            System.err.println("Error occurred while scraping data: " + e.getMessage());
        }

        return shorts;
    }

    @Override
    public boolean validatePsiRegNumber(Integer psiRegNo) {

        try{
            // search by the PSI registration number provided
            String url = "https://registrations.thepsi.ie/search-register/?type=0&search=*" +  psiRegNo + "*";

            // retrieve the webpage
            Document document = Jsoup.connect(url)
                                // mimic real user
                                .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)")
                                .timeout(10000)
                                .get();

            // from inspection of the webpage using Google Chrome DevTools, PSI registration numbers are inside span tags class="srchitms"
            Elements psiRegNoResults = document.select("span.srchitms");

            /* when searching by PSI registration number, the PSI website returns search results if the number matches any of the pharmacy details
             e.g. the number is contained within the phone number, the companies office registration number, or the PSI registration number.
             Therefore it is necessary to loop through the PSI registration numbers of the returned search results and look for a match to the 
             entered PSI registration number */
            for(Element result : psiRegNoResults) {
                // the sibling node immediately previous to the span tags containing the PSI reg numbers contains text reading "PSI Registration Number:"
                if (result.previousSibling() != null && result.previousSibling().outerHtml().contains("PSI Registration Number:")){
                    // if any of the returned PSI registration numbers match the PSI registration number entered
                    if(Integer.valueOf(result.text().trim()).equals(psiRegNo)) {
                        // the PSI number entered is a valid PSI registration number, return true
                        return true;
                    }
                }
            }
        }
        catch(IOException e){
            System.err.println("Error occurred while scraping data: " + e.getMessage());
        }

        // if no match is found, the PSI registration number entered is invalid, return false
        return false;
        
    }

}
