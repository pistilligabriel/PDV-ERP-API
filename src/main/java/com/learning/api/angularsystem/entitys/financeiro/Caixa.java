package com.learning.api.angularsystem.entitys.financeiro;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.learning.api.angularsystem.entitys.cadastro.usuario.Usuario;
import com.learning.api.angularsystem.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "CAIXA")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Caixa implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codigo;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private Status status = Status.ABERTO;

    @Column(name = "DATA_ABERTURA")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime dataAbertura = LocalDateTime.now();

    @Column(name = "DATA_FECHAMENTO")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime dataFechamento;

    @Column(name = "SALDO_INICIAL")
    private BigDecimal saldoInicial;

    @Column(name = "SALDO_FINAL")
    private BigDecimal saldoFinal;

    @Column(name = "DIFERENCA")
    private BigDecimal diferenca;

    @Column(name = "TOTAL_ENTRADAS")
    private BigDecimal totalEntradas;

    @Column(name = "TOTAL_SAIDAS")
    private BigDecimal totalSaidas;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USUARIO_ABERTURA")
    private Usuario usuarioAbertura;

    @Column(name = "OBSERVACAO")
    private String observacao;

}
