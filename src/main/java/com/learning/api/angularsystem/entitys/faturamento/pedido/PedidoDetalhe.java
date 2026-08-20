package com.learning.api.angularsystem.entitys.faturamento.pedido;

import com.learning.api.angularsystem.entitys.cadastro.item.Item;
import com.learning.api.angularsystem.entitys.cadastro.item.UnidadeMedida;
import com.learning.api.angularsystem.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Formula;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "pedido_detalhe")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PedidoDetalhe implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codigo;

    @ManyToOne
    @JoinColumn(name = "PEDIDO")
    private Pedido pedido;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "ITEM")
    private Item item;

    @Column(name = "ORDEM")
    private int ordem;

    @Column(name = "DESCRICAO")
    private String descricao;


    @Column(name = "QUANTIDADE")
    private int quantidade;

    @Column(name = "VALOR_UNITARIO", precision = 15, scale = 2)
    private BigDecimal valorUnitario;

    @Column(name = "VALOR_TOTAL", precision = 15, scale = 2)
    private BigDecimal valorTotal;


}
