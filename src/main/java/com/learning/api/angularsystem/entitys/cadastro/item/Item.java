package com.learning.api.angularsystem.entitys.cadastro.item;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.learning.api.angularsystem.enums.Status;
import com.learning.api.angularsystem.enums.item.Tamanho;
import com.learning.api.angularsystem.enums.item.TipoItem;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "item")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codigo;

    @Column(name = "DATA_CADASTRO")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime dataCadastro = LocalDateTime.now();

    @Column(name="MODELO")
    private String modelo;

    @ManyToOne
    @JoinColumn(name = "GRUPO_ITEM")
    private ItemGrupo grupoItem;

    @Column(name = "DESCRICAO")
    private String descricao;

    @Column(name="TIPO_PRODUTO")
    @Enumerated(EnumType.STRING)
    private TipoItem tipoProduto;

    @Column(name = "TAMANHO")
    @Enumerated(EnumType.STRING)
    private Tamanho tamanho;

    @Column(name = "OBSERVACAO")
    private String observacao;

    @ManyToOne
    @JoinColumn(name = "UNIDADE_VENDA")
    @JsonIgnore
    private UnidadeMedida unidadeVenda;

    @ManyToOne
    @JoinColumn(name = "FABRICANTE")
    @JsonIgnore
    private Fabricante fabricante;

    @Column(name = "PRECO_CUSTO")
    private Double precoCusto;

    @Column(name = "PRECO_VENDA")
    private Double precoVenda;

    @Column(name = "MARGEM_LUCRO")
    private Double margemLucro;

    @Column(name = "ESTOQUE")
    private Integer estoque;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private Status status = Status.ATIVO;

    @Column(name = "EMPRESA")
    private Long empresa = 1L;

    @Column(name = "VERSAO")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime versao = LocalDateTime.now();


    public void setStatusAtivo() {
        this.status = Status.ATIVO;
        this.versao = LocalDateTime.now();
    }

    public void setStatusInativo() {
        this.status = Status.DESATIVADO;
        this.versao = LocalDateTime.now();
    }

    public Double calcularMargemLucro(){
        precoCusto = getPrecoCusto();
        precoVenda = getPrecoVenda();
        return ((precoVenda - precoCusto) / precoCusto) * 100;
    }

}
