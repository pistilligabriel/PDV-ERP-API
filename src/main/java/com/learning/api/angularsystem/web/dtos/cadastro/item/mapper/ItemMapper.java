package com.learning.api.angularsystem.web.dtos.cadastro.item.mapper;

import com.learning.api.angularsystem.entitys.cadastro.item.Item;
import com.learning.api.angularsystem.web.dtos.cadastro.item.ItemDto;
import com.learning.api.angularsystem.web.dtos.cadastro.item.ItemResponseDto;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {

    private static final ModelMapper modelMapper = new ModelMapper();

    public static Item toItem(ItemDto itemDto) {
        return new ModelMapper().map(itemDto, Item.class);
    }

    public static ItemResponseDto toDto(Item item) {
        return new ModelMapper().map(item, ItemResponseDto.class);
    }

    public static List<ItemResponseDto> toListDto(List<Item> itens){
        return itens.stream().map(item -> toDto(item)).collect(Collectors.toList());
    }
}
