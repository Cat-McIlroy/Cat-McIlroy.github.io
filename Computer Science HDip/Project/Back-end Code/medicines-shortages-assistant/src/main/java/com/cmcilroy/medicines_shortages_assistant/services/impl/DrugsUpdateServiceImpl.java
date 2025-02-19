package com.cmcilroy.medicines_shortages_assistant.services.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.cmcilroy.medicines_shortages_assistant.domain.dto.DrugDto;
import com.cmcilroy.medicines_shortages_assistant.domain.entities.DrugEntity;
import com.cmcilroy.medicines_shortages_assistant.mappers.Mapper;
import com.cmcilroy.medicines_shortages_assistant.parsers.XmlParser;
import com.cmcilroy.medicines_shortages_assistant.repositories.DrugRepository;
import com.cmcilroy.medicines_shortages_assistant.scrapers.WebScraper;
import com.cmcilroy.medicines_shortages_assistant.services.DrugsUpdateService;
import jakarta.annotation.PostConstruct;

@Service
public class DrugsUpdateServiceImpl implements DrugsUpdateService {

    // inject dependencies
    private XmlParser xmlParser;
    // web scraper to obtain medicines shortages information
    private WebScraper webScraper;
    // mapper to convert DrugDto objects to DrugEntity objects
    private Mapper<DrugEntity, DrugDto> drugMapper;
    // DrugRepository to handle database interactions
    private DrugRepository drugRepository;

    public DrugsUpdateServiceImpl(XmlParser xmlParser, WebScraper webScraper, Mapper<DrugEntity, DrugDto> drugMapper, DrugRepository drugRepository) {
        this.xmlParser = xmlParser;
        this.webScraper = webScraper;
        this.drugMapper = drugMapper;
        this.drugRepository = drugRepository;
    }


    // run initial update of database when application starts up
    @PostConstruct
    public void initialUpdate() {
        // download Authorised Medicines list
        downloadAuthorisedList();
        // download Interchangeables list
        downloadInterchangeablesList();
        // parse Authorised Medicines List and Interchangeables List into List of DrugDto objects
        List<DrugDto> drugs = xmlParser.parseDrugData();
        // scrape HPRA website for medicines shortages
        List<String> shorts = webScraper.scrapeUnavailableDrugs();
        // for each DrugDto object in the drugs list
        for(DrugDto drugDto : drugs) {
            // check if its licence number is contained in the shorts list
            if(shorts.contains(drugDto.getLicenceNo())) {
                // if it is, set isAvailable to false
                drugDto.setIsAvailable(false);
            }
            else {
                // otherwise set isAvailable to true
                drugDto.setIsAvailable(true);
            }
            // map it to a DrugEntity object
            DrugEntity drugEntity = drugMapper.mapFrom(drugDto);
            // save the DrugEntity object to the database
            drugRepository.save(drugEntity);
        }
        
    }

    // run scheduled update every 24 hours
    @Scheduled(fixedRate = 86400000)
    public void scheduledUpdate() {
        // download Authorised Medicines list
        downloadAuthorisedList();
        // download Interchangeables list
        downloadInterchangeablesList();
        // download Withdrawn Medicines list
        downloadWithdrawnList();
        // parse Authorised Medicines List and Interchangeables List into List of DrugDto objects
        List<DrugDto> drugs = xmlParser.parseDrugData();
        // parse Withdrawn Medicines List into List of DrugDto objects
        List<DrugDto> withdrawnDrugs = xmlParser.parseWithdrawnList();
        // scrape HPRA website for medicines shortages
        List<String> shorts = webScraper.scrapeUnavailableDrugs();

        // for each DrugDto object in the drugs list
        for(DrugDto drugDto : drugs) {
            // check if its licence number is contained in the shorts list
            if(shorts.contains(drugDto.getLicenceNo())) {
                // if it is, set isAvailable to false
                drugDto.setIsAvailable(false);
            }
            else {
                // otherwise set isAvailable to true
                drugDto.setIsAvailable(true);
            }
            // map it to a DrugEntity object
            DrugEntity drugEntity = drugMapper.mapFrom(drugDto);
            // find the corresponding record in the database
            Optional<DrugEntity> existingRecord = drugRepository.findById(drugEntity.getLicenceNo());
            // compare drugEntity to existingRecord, if existingRecord is empty (i.e. this DrugEntity will be a new record), 
            // or any of the values are different than the existingRecord, save drugEntity to database
            if(existingRecord.isEmpty() || isDifferent(existingRecord.get(), drugEntity)
){
                // save the DrugEntity object to the database
                drugRepository.save(drugEntity);
            }
        }

        // for each DrugDto object in the withdrawnDrugs list
        for(DrugDto withdrawnDrugDto : withdrawnDrugs) {
            // store record in an Optional so that it can be empty if the record doesn't exist
            Optional<DrugEntity> record = drugRepository.findById(withdrawnDrugDto.getLicenceNo());
            // if the record is not empty, (i.e. the record exists in the database)
            if(!record.isEmpty()) {
                // delete the record as this drug has been withdrawn
                drugRepository.deleteById(withdrawnDrugDto.getLicenceNo());
            }
        }

    }

    public void downloadAuthorisedList() {

        File tempFile = new File(System.getProperty("user.dir") + "/latestHumanlist.xml.tmp");
        File finalFile = new File(System.getProperty("user.dir") + "/latestHumanlist.xml");

        try{
            URI uri = URI.create("https://hpraproddocsstg.blob.core.windows.net/products/xml/latestHumanlist.xml");
            URL url = uri.toURL();

            ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
            // write to temporary file
            FileOutputStream tempOutputStream = new FileOutputStream(tempFile);
            FileChannel tempChannel = tempOutputStream.getChannel();
            tempChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
            // close resources
            tempOutputStream.close();
            readableByteChannel.close();
            tempChannel.close();

            // rename to final file
            tempFile.renameTo(finalFile);
        }
        // this method could throw several types of exception, use Exception to cover all scenarios
        catch(Exception e){
            System.err.println("Download failed: " + e.getMessage());
        }
    }

    public void downloadInterchangeablesList() {

        File tempFile = new File(System.getProperty("user.dir") + "/latestInterchangeableslist.xml.tmp");
        File finalFile = new File(System.getProperty("user.dir") + "/latestInterchangeableslist.xml");

        try{
            URI uri = URI.create("https://hpraproddocsstg.blob.core.windows.net/products/xml/latestInterchangeableslist.xml");
            URL url = uri.toURL();

            ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
            // write to temporary file
            FileOutputStream tempOutputStream = new FileOutputStream(tempFile);
            FileChannel tempChannel = tempOutputStream.getChannel();
            tempChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
            // close resources
            tempOutputStream.close();
            readableByteChannel.close();
            tempChannel.close();

            // rename to final file
            tempFile.renameTo(finalFile);
        }
        // this method could throw several types of exception, use Exception to cover all scenarios
        catch(Exception e){
            System.err.println("Download failed: " + e.getMessage());
        }
    }

    public void downloadWithdrawnList() {

        File tempFile = new File(System.getProperty("user.dir") + "/withdrawnHumanlist.xml.tmp");
        File finalFile = new File(System.getProperty("user.dir") + "/withdrawnHumanlist.xml");

        try{
            URI uri = URI.create("https://hpraproddocsstg.blob.core.windows.net/products/xml/withdrawnHumanlist.xml");
            URL url = uri.toURL();

            ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
            // write to temporary file
            FileOutputStream tempOutputStream = new FileOutputStream(tempFile);
            FileChannel tempChannel = tempOutputStream.getChannel();
            tempChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
            // close resources
            tempOutputStream.close();
            readableByteChannel.close();
            tempChannel.close();

            // rename to final file
            tempFile.renameTo(finalFile);
        }
        // this method could throw several types of exception, use Exception to cover all scenarios
        catch(Exception e){
            System.err.println("Download failed: " + e.getMessage());
        }
    }

    public boolean isDifferent(DrugEntity existingRecord, DrugEntity drugEntity) {
        return 
            !Objects.equals(existingRecord.getProductName(), drugEntity.getProductName()) ||
            !Objects.equals(existingRecord.getManufacturer(), drugEntity.getManufacturer()) ||
            !Objects.equals(existingRecord.getStrength(), drugEntity.getStrength()) ||
            !Objects.equals(existingRecord.getDosageForm(), drugEntity.getDosageForm()) ||
            !Objects.equals(existingRecord.getActiveSubstance(), drugEntity.getActiveSubstance()) ||
            !Objects.equals(existingRecord.getIsAvailable(), drugEntity.getIsAvailable());
    }

}
