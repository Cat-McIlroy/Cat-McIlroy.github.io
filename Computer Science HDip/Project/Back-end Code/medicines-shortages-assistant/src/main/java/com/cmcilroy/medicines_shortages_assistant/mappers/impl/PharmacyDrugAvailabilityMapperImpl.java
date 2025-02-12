package com.cmcilroy.medicines_shortages_assistant.mappers.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.cmcilroy.medicines_shortages_assistant.domain.dto.PharmacyDrugAvailabilityDto;
import com.cmcilroy.medicines_shortages_assistant.domain.entities.PharmacyDrugAvailabilityEntity;
import com.cmcilroy.medicines_shortages_assistant.mappers.Mapper;

@Component // to allow this class to be injected elsewhere
public class PharmacyDrugAvailabilityMapperImpl implements Mapper<PharmacyDrugAvailabilityEntity, PharmacyDrugAvailabilityDto> {

    // inject ModelMapper
    private ModelMapper modelMapper;

    public PharmacyDrugAvailabilityMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    // map from Entity to Dto
    @Override
    public PharmacyDrugAvailabilityDto mapTo(PharmacyDrugAvailabilityEntity pharmacyDrugAvailabilityEntity) {
        return modelMapper.map(pharmacyDrugAvailabilityEntity, PharmacyDrugAvailabilityDto.class);
    }

    // map from Dto to Entity
    @Override
    public PharmacyDrugAvailabilityEntity mapFrom(PharmacyDrugAvailabilityDto pharmacyDrugAvailabilityDto) {
        return modelMapper.map(pharmacyDrugAvailabilityDto, PharmacyDrugAvailabilityEntity.class);
    }

}
