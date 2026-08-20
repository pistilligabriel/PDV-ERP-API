package com.learning.api.angularsystem.entitys.faturamento.pedido;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.learning.api.angularsystem.entitys.cadastro.integrante.Cliente;
import com.learning.api.angularsystem.entitys.cadastro.item.Item;
import com.learning.api.angularsystem.enums.Status;
import com.learning.api.angularsystem.enums.movimentacao.TipoFormaPagamento;
import com.learning.api.angularsystem.enums.pedido.TipoPedido;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Formula;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "pedido")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pedido implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codigo;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private Status status = Status.FINALIZADO;

    @Column(name="TIPO_VENDA")
    @Enumerated(EnumType.STRING)
    private TipoPedido tipoVenda;

    @Column(name = "DATA_EMISSAO")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime dataEmissao;

    @JoinColumn(name = "INTEGRANTE")
    @ManyToOne
    private Cliente integrante;

    @Column(name = "NOME_CLIENTE")
    private String nomeCliente;

    @Enumerated(EnumType.STRING)
    private TipoFormaPagamento formaPagamento;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PedidoDetalhe> detalhes = new ArrayList<>();

    @Column(name = "DESCONTO")
    private Double desconto;

    @Column(name = "PORCENTAGEM_DESCONTO")
    private Double porcentagemDesconto;

    @Column(name = "TOTAL", precision = 15, scale = 2)
    private BigDecimal total;

    @Column(name = "TOTAL_BRUTO", precision = 15, scale = 2)
    private BigDecimal totalSemDesconto;

    @Column(name = "PARCELAS")
    private int parcelas;

    @Column(name = "LUCRO",precision=15,scale=2)
    private BigDecimal lucro;

    @Column(name = "CUSTO",precision = 15,scale = 2)
    private BigDecimal custo;

    @Column(name = "EMPRESA")
    private Long empresa = 1L;

    @Column(name = "VERSAO")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime versao = LocalDateTime.now();


    public void setStatusNormal() {
        this.status = Status.NORMAL;
        this.versao = LocalDateTime.now();
    }

    public void setStatusCancelado() {
        this.status = Status.CANCELADO;
        this.versao = LocalDateTime.now();
    }

}
