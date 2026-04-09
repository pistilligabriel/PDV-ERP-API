package com.learning.api.angularsystem.web.dtos.cadastro.item;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.learning.api.angularsystem.entitys.cadastro.item.Fabricante;
import com.learning.api.angularsystem.entitys.cadastro.item.UnidadeMedida;
import com.learning.api.angularsystem.enums.item.Tamanho;
import com.learning.api.angularsystem.enums.item.TipoItem;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ItemResponseDto {
    private Long codigo;
    private String status;
    private int empresa;
    private String grupoItem;
    private String descricao;
    private TipoItem tipoProduto;
    private String observacao;
    private String modelo;
    private Tamanho tamanho;
    private UnidadeMedida unidadeVenda;
    private Fabricante fabricante;
    private Double precoVenda;
    private Double precoCusto;
    private Double estoque;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime dataCadastro;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime versao;
}
