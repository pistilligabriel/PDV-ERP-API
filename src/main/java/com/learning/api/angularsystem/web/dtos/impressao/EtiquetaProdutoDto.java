package com.learning.api.angularsystem.web.dtos.impressao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EtiquetaProdutoDto {

    private Long produtoId;

    private String descricao;

    private String codigoBarras;

    private Double precoVenda;

    private Integer quantidade;
}