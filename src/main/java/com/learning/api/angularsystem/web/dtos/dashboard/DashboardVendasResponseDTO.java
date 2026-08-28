package com.learning.api.angularsystem.web.dtos.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardVendasResponseDTO {
    private Long quantidadeVendas;
    private BigDecimal valorVendido;
    private Long quantidadeProdutos;
    private BigDecimal ticketMedio;
    private List<VendaPorPagamentoDTO> vendasPorPagamento;
}
