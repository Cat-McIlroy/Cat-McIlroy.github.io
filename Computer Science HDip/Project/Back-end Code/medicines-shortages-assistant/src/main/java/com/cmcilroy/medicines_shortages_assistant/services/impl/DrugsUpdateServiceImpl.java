package com.cmcilroy.medicines_shortages_assistant.services.impl;

import java.io.FileOutputStream;
import java.net.URI;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.List;
import java.util.Optional;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.cmcilroy.medicines_shortages_assistant.domain.dto.DrugDto;
import com.cmcilroy.medicines_shortages_assistant.domain.entities.DrugEntity;
import com.cmcilroy.medicines_shortages_assistant.mappers.Mapper;
import com.cmcilroy.medicines_shortages_assistant.parsers.XmlParser;
import com.cmcilroy.medicines_shortages_assistant.repositories.DrugRepository;
import com.cmcilroy.medicines_shortages_assistant.services.DrugsUpdateService;
import jakarta.annotation.PostConstruct;

@Service
public class DrugsUpdateServiceImpl implements DrugsUpdateService {

    // inject dependencies
    private XmlParser xmlParser;
    // mapper to convert DrugDto objects to DrugEntity objects
    private Mapper<DrugEntity, DrugDto> drugMapper;
    private DrugRepository drugRepository;

    public DrugsUpdateServiceImpl(XmlParser xmlParser, Mapper<DrugEntity, DrugDto> drugMapper, DrugRepository drugRepository) {
        this.xmlParser = xmlParser;
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

        // for each DrugDto object in the drugs list
        for(DrugDto drugDto : drugs) {
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

        // for each DrugDto object in the drugs list
        for(DrugDto drugDto : drugs) {
            // map it to a DrugEntity object
            DrugEntity drugEntity = drugMapper.mapFrom(drugDto);
            // find the corresponding record in the database
            Optional<DrugEntity> existingRecord = drugRepository.findById(drugEntity.getLicenceNo());
            // compare drugEntity to existingRecord, if existingRecord is empty (i.e. this DrugEntity will be a new record), 
            // or any of the values are different than the existingRecord, save drugEntity to database
            if(
                existingRecord.isEmpty() ||
                !drugEntity.getProductName().equals(existingRecord.get().getProductName()) ||
                !drugEntity.getStrength().equals(existingRecord.get().getStrength()) ||
                !drugEntity.getDosageForm().equals(existingRecord.get().getDosageForm()) ||
                !drugEntity.getActiveSubstance().equals(existingRecord.get().getActiveSubstance()) ||
                !drugEntity.getIsAvailable().equals(existingRecord.get().getIsAvailable())
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
        try{
            URI uri = URI.create("https://hpraproddocsstg.blob.core.windows.net/products/xml/latestHumanlist.xml");
            URL url = uri.toURL();
            ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
            // write file to same location that XmlParserImpl.parseDrugData() reads from
            // if file already exists it will be overwritten
            FileOutputStream fileOutputStream = new FileOutputStream(System.getProperty("user.dir") + "/latestHumanlist.xml");
            FileChannel fileChannel = fileOutputStream.getChannel();
            fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
            fileOutputStream.close();
            readableByteChannel.close();
        }
        // this method could throw several types of exception, use Exception to cover all scenarios
        catch(Exception e){
            System.err.println("Download failed: " + e.getMessage());
        }
    }

    public void downloadInterchangeablesList() {
        try{
            URI uri = URI.create("https://hpraproddocsstg.blob.core.windows.net/products/xml/latestInterchangeableslist.xml");
            URL url = uri.toURL();
            ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
            // write file to same location that XmlParserImpl.parseDrugData() reads from
            // if file already exists it will be overwritten
            FileOutputStream fileOutputStream = new FileOutputStream(System.getProperty("user.dir") + "/latestInterchangeableslist.xml");
            FileChannel fileChannel = fileOutputStream.getChannel();
            fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
            fileOutputStream.close();
            readableByteChannel.close();
        }
        // this method could throw several types of exception, use Exception to cover all scenarios
        catch(Exception e){
            System.err.println("Download failed: " + e.getMessage());
        }
    }

    public void downloadWithdrawnList() {
        try{
            URI uri = URI.create("https://hpraproddocsstg.blob.core.windows.net/products/xml/withdrawnHumanlist.xml");
            URL url = uri.toURL();
            ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
            // write file to same location that XmlParserImpl.parseDrugData() reads from
            // if file already exists it will be overwritten
            FileOutputStream fileOutputStream = new FileOutputStream(System.getProperty("user.dir") + "/withdrawnHumanlist.xml");
            FileChannel fileChannel = fileOutputStream.getChannel();
            fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
            fileOutputStream.close();
            readableByteChannel.close();
        }
        // this method could throw several types of exception, use Exception to cover all scenarios
        catch(Exception e){
            System.err.println("Download failed: " + e.getMessage());
        }
    }

}
