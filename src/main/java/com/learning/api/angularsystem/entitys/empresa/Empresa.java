package com.learning.api.angularsystem.entitys.empresa;

import com.learning.api.angularsystem.enums.TipoEmpresa;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "empresa")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CODIGO")
    private Long codigo;

    @Column(name = "NOME_EMPRESA")
    private String nomeEmpresa;

    @Column(name = "LOGO")
    @Lob
    private byte[] logo;

    @Column(name="TIPO_SISTEMA")
    @Enumerated(EnumType.STRING)
    private TipoEmpresa tipoEmpresa;
}