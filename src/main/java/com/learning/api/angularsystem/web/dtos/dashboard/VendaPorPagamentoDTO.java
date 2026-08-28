package com.learning.api.angularsystem.web.dtos.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VendaPorPagamentoDTO {
    private String formaPagamento;
    private BigDecimal valor;
}
