package com.cmcilroy.medicines_shortages_assistant.mappers.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.cmcilroy.medicines_shortages_assistant.domain.dto.DrugDto;
import com.cmcilroy.medicines_shortages_assistant.domain.entities.DrugEntity;
import com.cmcilroy.medicines_shortages_assistant.mappers.Mapper;

@Component
public class DrugMapperImpl implements Mapper<DrugEntity, DrugDto> {

    private ModelMapper modelMapper;

    public DrugMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    // map from Entity to Dto
    @Override
    public DrugDto mapTo(DrugEntity drugEntity) {
        return modelMapper.map(drugEntity, DrugDto.class);
    }

    // map from Dto to Entity
    @Override
    public DrugEntity mapFrom(DrugDto drugDto) {
        return modelMapper.map(drugDto, DrugEntity.class);
    }

}
