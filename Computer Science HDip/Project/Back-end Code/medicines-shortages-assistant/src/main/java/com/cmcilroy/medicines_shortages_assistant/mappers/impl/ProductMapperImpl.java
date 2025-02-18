package com.cmcilroy.medicines_shortages_assistant.mappers.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import com.cmcilroy.medicines_shortages_assistant.domain.dto.DrugDto;
import com.cmcilroy.medicines_shortages_assistant.mappers.Mapper;
import com.cmcilroy.medicines_shortages_assistant.parsers.Product;

@Component
public class ProductMapperImpl implements Mapper<Product, DrugDto> {

    private ModelMapper modelMapper;

    public ProductMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    // map from Entity to Dto
    @Override
    public DrugDto mapTo(Product product) {
        return modelMapper.map(product, DrugDto.class);
    }

    // map from Dto to Entity
    @Override
    public Product mapFrom(DrugDto drugDto) {
        return modelMapper.map(drugDto, Product.class);
    }

}