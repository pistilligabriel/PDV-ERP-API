package com.learning.api.angularsystem.web.dtos.financeiro.mapper;

import com.learning.api.angularsystem.entitys.financeiro.Caixa;
import com.learning.api.angularsystem.web.dtos.financeiro.CaixaDto;
import com.learning.api.angularsystem.web.dtos.financeiro.CaixaResponseDto;
import org.modelmapper.ModelMapper;

import java.util.List;

public class CaixaMapper {

    public static Caixa toCaixa(CaixaDto dto){
        return new ModelMapper().map(dto, Caixa.class);
    }

    public static CaixaResponseDto toDto(Caixa caixa){
        return new ModelMapper().map(caixa, CaixaResponseDto.class);
    }

    public static List<CaixaResponseDto> toListDto(List<Caixa> caixas){
        return caixas.stream().map(caixa -> toDto(caixa)).toList();
    }
}
