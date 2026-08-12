package com.learning.api.angularsystem.services.impressao;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class ImpressaoService {

    private final WindowsPrintService windowsPrintService;

    public ImpressaoService(
            WindowsPrintService windowsPrintService
    ) {
        this.windowsPrintService = windowsPrintService;
    }

    public void imprimirTeste() {

        byte[] inicializacao = {
                0x1B, 0x40
        };

        String texto =
                "\n" +
                        "================================\n" +
                        "       TESTE DO SISTEMA\n" +
                        "================================\n" +
                        "\n" +
                        "Impressora: POS-58\n" +
                        "Porta: USB001\n" +
                        "\n" +
                        "Teste ESC/POS\n" +
                        "\n\n\n";

        byte[] conteudo =
                texto.getBytes(
                        StandardCharsets.UTF_8
                );

        byte[] corte = {
                0x1D,
                0x56,
                0x00
        };

        byte[] dados =
                new byte[
                        inicializacao.length
                                + conteudo.length
                                + corte.length
                        ];

        int posicao = 0;

        System.arraycopy(
                inicializacao,
                0,
                dados,
                posicao,
                inicializacao.length
        );

        posicao += inicializacao.length;

        System.arraycopy(
                conteudo,
                0,
                dados,
                posicao,
                conteudo.length
        );

        posicao += conteudo.length;

        System.arraycopy(
                corte,
                0,
                dados,
                posicao,
                corte.length
        );

        windowsPrintService.imprimir(dados);
    }
}