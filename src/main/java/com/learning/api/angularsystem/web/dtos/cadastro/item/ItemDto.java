package com.learning.api.angularsystem.web.dtos.cadastro.item;

import com.learning.api.angularsystem.enums.item.Tamanho;
import com.learning.api.angularsystem.enums.item.TipoItem;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ItemDto {

    private Long codigo;

    private int grupoItem;

    @NotBlank(message = "O campo descrição é obrigatório")
    private String descricao;

    private TipoItem tipoProduto;

    private String observacao;

    private Long unidadeVenda;

    private Long fabricante;

    private String modelo;

    private Tamanho tamanho;

    private Double precoCusto;

    private Double precoVenda;

    private int estoque;

    private int quantidade;

    private Double margemLucro;

}
