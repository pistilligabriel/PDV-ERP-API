package com.learning.api.angularsystem.web.dtos.faturamento.pedido;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ItemVendaDto {

    private Long produtoId;

    private Integer quantidade;

    private String observacao;

}
