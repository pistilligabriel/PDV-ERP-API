package com.learning.api.angularsystem.services.impressao;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class EscPosBuilder {

    private final ByteArrayOutputStream output =
            new ByteArrayOutputStream();

    private final Charset charset;

    private static final int LARGURA_IMPRESSORA = 384;

    public EscPosBuilder() {
        this(StandardCharsets.UTF_8);
    }

    public EscPosBuilder(Charset charset) {
        this.charset = charset;
        inicializar();
    }

    // =========================================================
    // Inicialização
    // =========================================================

    public EscPosBuilder inicializar() {

        write(
                0x1B,
                0x40
        );

        return this;
    }

    // =========================================================
    // Texto
    // =========================================================

    public EscPosBuilder texto(String texto) {

        if (texto == null) {
            return this;
        }

        byte[] bytes =
                texto.getBytes(charset);

        output.write(
                bytes,
                0,
                bytes.length
        );

        return this;
    }

    public EscPosBuilder textoAscii(String texto) {

        if (texto == null) {
            return this;
        }

        byte[] bytes =
                texto.getBytes(
                        StandardCharsets.US_ASCII
                );

        output.write(
                bytes,
                0,
                bytes.length
        );

        return this;
    }

    // =========================================================
    // Quebra de linha
    // =========================================================

    public EscPosBuilder quebraLinha() {

        write(0x0A);

        return this;
    }

    public EscPosBuilder linhas(int quantidade) {

        for (int i = 0; i < quantidade; i++) {
            write(0x0A);
        }

        return this;
    }

    // =========================================================
    // Alinhamento
    // =========================================================

    public EscPosBuilder esquerda() {

        write(
                0x1B,
                0x61,
                0x00
        );

        return this;
    }

    public EscPosBuilder centralizar() {

        write(
                0x1B,
                0x61,
                0x01
        );

        return this;
    }

    public EscPosBuilder direita() {

        write(
                0x1B,
                0x61,
                0x02
        );

        return this;
    }

    // =========================================================
    // Negrito
    // =========================================================

    public EscPosBuilder negrito(boolean ativo) {

        write(
                0x1B,
                0x45,
                ativo ? 0x01 : 0x00
        );

        return this;
    }

    // =========================================================
    // Tamanho do texto
    // =========================================================

    public EscPosBuilder tamanhoNormal() {

        write(
                0x1D,
                0x21,
                0x00
        );

        return this;
    }

    public EscPosBuilder tamanho2x() {

        write(
                0x1D,
                0x21,
                0x11
        );

        return this;
    }

    public EscPosBuilder tamanho2xHorizontal() {

        write(
                0x1D,
                0x21,
                0x10
        );

        return this;
    }

    public EscPosBuilder tamanho2xVertical() {

        write(
                0x1D,
                0x21,
                0x01
        );

        return this;
    }

    // =========================================================
    // Sublinhado
    // =========================================================

    public EscPosBuilder sublinhado(boolean ativo) {

        write(
                0x1B,
                0x2D,
                ativo ? 0x01 : 0x00
        );

        return this;
    }

    // =========================================================
    // Código de barras EAN-13
    // =========================================================

    public EscPosBuilder ean13(
            String codigo12
    ) {

        if (codigo12 == null
                || !codigo12.matches("\\d{12}")) {

            throw new IllegalArgumentException(
                    "EAN-13 deve possuir exatamente "
                            + "12 dígitos."
            );
        }

        int digito =
                calcularDigitoEan13(
                        codigo12
                );

        String codigoCompleto =
                codigo12 + digito;

        byte[] bitmap =
                gerarBitmapEan13(
                        codigoCompleto
                );

        /*
         * Centraliza o barcode.
         */
        centralizar();

        /*
         * Envia imagem.
         */
        output.write(
                bitmap,
                0,
                bitmap.length
        );

        /*
         * Volta para texto normal.
         */
        tamanhoNormal();

        centralizar();

        textoAscii(
                codigoCompleto
        );

        quebraLinha();

        return this;
    }

    // =========================================================
    // Alimentação
    // =========================================================

    public EscPosBuilder alimentar(
            int linhas
    ) {

        if (linhas < 0) {
            throw new IllegalArgumentException(
                    "Quantidade de linhas inválida."
            );
        }

        for (int i = 0; i < linhas; i++) {
            write(0x0A);
        }

        return this;
    }

    // =========================================================
    // Reset
    // =========================================================

    public EscPosBuilder resetar() {

        write(
                0x1B,
                0x40
        );

        return this;
    }

    // =========================================================
    // Build
    // =========================================================

    public byte[] build() {

        return output.toByteArray();
    }

    // =========================================================
    // Helpers
    // =========================================================

    private void write(int... bytes) {

        for (int b : bytes) {

            output.write(
                    b & 0xFF
            );
        }
    }

    private int calcularDigitoEan13(
            String codigo12
    ) {

        int soma = 0;

        for (int i = 0; i < 12; i++) {

            int digito =
                    codigo12.charAt(i) - '0';

            if (i % 2 == 0) {

                soma += digito;

            } else {

                soma += digito * 3;
            }
        }

        return (10 - (soma % 10)) % 10;
    }

    // =========================================================
    // Geração do EAN-13 como bitmap
    // =========================================================

    private byte[] gerarBitmapEan13(
            String codigo
    ) {

        final int ALTURA = 120;

        String[] L = {
                "0001101",
                "0011001",
                "0010011",
                "0111101",
                "0100011",
                "0110001",
                "0101111",
                "0111011",
                "0110111",
                "0001011"
        };

        String[] G = {
                "0100111",
                "0110011",
                "0011011",
                "0100001",
                "0011101",
                "0111001",
                "0000101",
                "0010001",
                "0001001",
                "0010111"
        };

        String[] R = {
                "1110010",
                "1100110",
                "1101100",
                "1000010",
                "1011100",
                "1001110",
                "1010000",
                "1000100",
                "1001000",
                "1110100"
        };

        String[] PARIDADE = {
                "LLLLLL",
                "LLGLGG",
                "LLGGLG",
                "LLGGGL",
                "LGLLGG",
                "LGGLLG",
                "LGGGLL",
                "LGLGLG",
                "LGLGGL",
                "LGGLGL"
        };

        int primeiroDigito =
                codigo.charAt(0) - '0';

        String padrao =
                PARIDADE[primeiroDigito];

        StringBuilder barras =
                new StringBuilder();

        /*
         * Guarda inicial
         */
        barras.append("101");

        /*
         * Seis dígitos da esquerda
         */
        for (int i = 1; i <= 6; i++) {

            int digito =
                    codigo.charAt(i) - '0';

            if (padrao.charAt(i - 1) == 'L') {

                barras.append(
                        L[digito]
                );

            } else {

                barras.append(
                        G[digito]
                );
            }
        }

        /*
         * Guarda central
         */
        barras.append("01010");

        /*
         * Seis dígitos da direita
         */
        for (int i = 7; i <= 12; i++) {

            int digito =
                    codigo.charAt(i) - '0';

            barras.append(
                    R[digito]
            );
        }

        /*
         * Guarda final
         */
        barras.append("101");

        if (barras.length() != 95) {

            throw new IllegalStateException(
                    "EAN-13 inválido. "
                            + "Módulos: "
                            + barras.length()
            );
        }

        /*
         * 3 pixels por módulo.
         *
         * 95 × 3 = 285 pixels.
         */
        final int escala = 3;

        int larguraCodigo =
                95 * escala;

        int margem =
                (
                        LARGURA_IMPRESSORA
                                - larguraCodigo
                ) / 2;

        int bytesPorLinha =
                LARGURA_IMPRESSORA / 8;

        ByteArrayOutputStream bitmap =
                new ByteArrayOutputStream();

        /*
         * GS v 0
         */
        bitmap.write(0x1D);
        bitmap.write(0x76);
        bitmap.write(0x30);
        bitmap.write(0x00);

        /*
         * Largura em bytes
         */
        bitmap.write(
                bytesPorLinha & 0xFF
        );

        bitmap.write(
                (bytesPorLinha >> 8) & 0xFF
        );

        /*
         * Altura
         */
        bitmap.write(
                ALTURA & 0xFF
        );

        bitmap.write(
                (ALTURA >> 8) & 0xFF
        );

        /*
         * Pixels
         */
        for (int y = 0; y < ALTURA; y++) {

            for (int byteIndex = 0;
                 byteIndex < bytesPorLinha;
                 byteIndex++) {

                int valor = 0;

                for (int bit = 0; bit < 8; bit++) {

                    int x =
                            byteIndex * 8 + bit;

                    int moduloX =
                            x - margem;

                    boolean preto = false;

                    if (moduloX >= 0
                            && moduloX < larguraCodigo) {

                        int modulo =
                                moduloX / escala;

                        if (modulo < 95) {

                            preto =
                                    barras
                                            .charAt(modulo)
                                            == '1';
                        }
                    }

                    if (preto) {

                        valor |=
                                1 << (7 - bit);
                    }
                }

                bitmap.write(valor);
            }
        }

        return bitmap.toByteArray();
    }
}