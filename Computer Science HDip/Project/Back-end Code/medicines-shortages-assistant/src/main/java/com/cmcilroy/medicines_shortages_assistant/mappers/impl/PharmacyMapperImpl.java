package com.cmcilroy.medicines_shortages_assistant.mappers.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.cmcilroy.medicines_shortages_assistant.domain.dto.PharmacyDto;
import com.cmcilroy.medicines_shortages_assistant.domain.entities.PharmacyEntity;
import com.cmcilroy.medicines_shortages_assistant.mappers.Mapper;

@Component
public class PharmacyMapperImpl implements Mapper<PharmacyEntity, PharmacyDto> {

    private ModelMapper modelMapper;

    public PharmacyMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    // map from Entity to Dto
    @Override
    public PharmacyDto mapTo(PharmacyEntity pharmacyEntity) {
        return modelMapper.map(pharmacyEntity, PharmacyDto.class);
    }

    // map from Dto to Entity
    @Override
    public PharmacyEntity mapFrom(PharmacyDto pharmacyDto) {
        return modelMapper.map(pharmacyDto, PharmacyEntity.class);
    }

}
