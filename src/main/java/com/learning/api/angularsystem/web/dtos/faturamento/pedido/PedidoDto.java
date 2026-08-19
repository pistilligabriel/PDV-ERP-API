package com.learning.api.angularsystem.web.dtos.faturamento.pedido;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.learning.api.angularsystem.enums.Status;
import com.learning.api.angularsystem.enums.movimentacao.TipoFormaPagamento;
import com.learning.api.angularsystem.enums.pedido.TipoPedido;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PedidoDto {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime dataEmissao;

    private Long clienteId;

    private String nomeCliente;

    private TipoPedido tipoVenda;

    private Status status;

    private Double total;

    private Double totalSemDesconto;

    private TipoFormaPagamento formaPagamento;

    private Integer parcelas;

    private List<ItemVendaDto> itens;

}
