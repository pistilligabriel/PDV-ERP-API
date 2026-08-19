package com.learning.api.angularsystem.web.dtos.impressao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EtiquetaProdutoDto {

    private Map<Long,Integer> etiquetas;
}