package com.learning.api.angularsystem.web.dtos.cadastro.usuario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AlterarSenhaDto {

    @NotBlank(message = "O campo nome é obrigatório")
    private String login;
    @NotNull(message = "É necessário inserir a senha atual")
    private String password;

    @NotNull(message = "É necessário inserir a nova senha")
    private String newPassword;

    @NotNull(message = "É necessário confirmar a senha")
    private String confirmPassword;
}
