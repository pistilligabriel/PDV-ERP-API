package com.learning.api.angularsystem.web.dtos.financeiro;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CaixaDto {
    private Double saldoInicial;
}
