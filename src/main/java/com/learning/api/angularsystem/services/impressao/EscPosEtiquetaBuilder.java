package com.learning.api.angularsystem.services.impressao;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class EscPosEtiquetaBuilder {

    /*
     * POS-58 normalmente trabalha com 384 dots.
     */
    private static final int LARGURA =
            384;

    /*
     * Altura padrão inicial.
     *
     * Vamos ajustar depois conforme o tamanho
     * real da etiqueta que você comprar.
     */
    private static final int ALTURA =
            320;

    private final ByteArrayOutputStream output =
            new ByteArrayOutputStream();

    private final Charset charset;

    public EscPosEtiquetaBuilder() {

        this(StandardCharsets.UTF_8);
    }

    public EscPosEtiquetaBuilder(
            Charset charset
    ) {

        this.charset = charset;

        inicializar();
    }

    // =========================================================
    // Inicialização
    // =========================================================

    public EscPosEtiquetaBuilder inicializar() {

        write(
                0x1B,
                0x40
        );

        return this;
    }

    // =========================================================
    // Texto
    // =========================================================

    public EscPosEtiquetaBuilder texto(
            String texto
    ) {

        if (texto == null) {
            return this;
        }

        byte[] dados =
                texto.getBytes(charset);

        output.write(
                dados,
                0,
                dados.length
        );

        return this;
    }

    public EscPosEtiquetaBuilder textoAscii(
            String texto
    ) {

        if (texto == null) {
            return this;
        }

        byte[] dados =
                texto.getBytes(
                        StandardCharsets.US_ASCII
                );

        output.write(
                dados,
                0,
                dados.length
        );

        return this;
    }

    // =========================================================
    // Quebra de linha
    // =========================================================

    public EscPosEtiquetaBuilder quebraLinha() {

        write(0x0A);

        return this;
    }

    // =========================================================
    // Alinhamento
    // =========================================================

    public EscPosEtiquetaBuilder esquerda() {

        write(
                0x1B,
                0x61,
                0x00
        );

        return this;
    }

    public EscPosEtiquetaBuilder centralizar() {

        write(
                0x1B,
                0x61,
                0x01
        );

        return this;
    }

    public EscPosEtiquetaBuilder direita() {

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

    public EscPosEtiquetaBuilder negrito(
            boolean ativo
    ) {

        write(
                0x1B,
                0x45,
                ativo ? 0x01 : 0x00
        );

        return this;
    }

    // =========================================================
    // Tamanho
    // =========================================================

    public EscPosEtiquetaBuilder tamanhoNormal() {

        write(
                0x1D,
                0x21,
                0x00
        );

        return this;
    }

    public EscPosEtiquetaBuilder tamanho2x() {

        write(
                0x1D,
                0x21,
                0x11
        );

        return this;
    }

    // =========================================================
    // EAN-13
    // =========================================================

    public EscPosEtiquetaBuilder ean13(
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

        String codigo =
                codigo12 + digito;

        byte[] bitmap =
                gerarBitmapEan13(
                        codigo
                );

        centralizar();

        output.write(
                bitmap,
                0,
                bitmap.length
        );

        /*
         * Número abaixo do barcode.
         */
        tamanhoNormal();

        centralizar();

        textoAscii(codigo);

        quebraLinha();

        return this;
    }

    // =========================================================
    // Finalizar etiqueta
    // =========================================================

    public EscPosEtiquetaBuilder finalizarEtiqueta() {

        /*
         * Alimentação inicial simples.
         *
         * Este valor será ajustado de acordo com
         * a altura real da etiqueta.
         */
        alimentar(4);

        return this;
    }

    // =========================================================
    // Alimentação
    // =========================================================

    public EscPosEtiquetaBuilder alimentar(
            int linhas
    ) {

        for (int i = 0; i < linhas; i++) {

            write(0x0A);
        }

        return this;
    }

    // =========================================================
    // Build
    // =========================================================

    public byte[] build() {

        return output.toByteArray();
    }

    // =========================================================
    // Dígito verificador EAN-13
    // =========================================================

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
    // Bitmap EAN-13
    // =========================================================

    private byte[] gerarBitmapEan13(
            String codigo
    ) {

        final int ALTURA_BARRAS = 120;

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

        int primeiro =
                codigo.charAt(0) - '0';

        StringBuilder barras =
                new StringBuilder();

        barras.append("101");

        String padrao =
                PARIDADE[primeiro];

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

        barras.append("01010");

        for (int i = 7; i <= 12; i++) {

            int digito =
                    codigo.charAt(i) - '0';

            barras.append(
                    R[digito]
            );
        }

        barras.append("101");

        if (barras.length() != 95) {

            throw new IllegalStateException(
                    "EAN-13 inválido."
            );
        }

        /*
         * 3 dots por módulo.
         */
        final int escala = 3;

        int larguraCodigo =
                95 * escala;

        int margem =
                (LARGURA - larguraCodigo) / 2;

        int bytesPorLinha =
                LARGURA / 8;

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
         * Largura
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
                ALTURA_BARRAS & 0xFF
        );

        bitmap.write(
                (ALTURA_BARRAS >> 8) & 0xFF
        );

        /*
         * Pixels
         */
        for (int y = 0;
             y < ALTURA_BARRAS;
             y++) {

            for (int byteIndex = 0;
                 byteIndex < bytesPorLinha;
                 byteIndex++) {

                int valor = 0;

                for (int bit = 0;
                     bit < 8;
                     bit++) {

                    int x =
                            byteIndex * 8 + bit;

                    int moduloX =
                            x - margem;

                    boolean preto =
                            false;

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

    // =========================================================
    // Escrita de bytes
    // =========================================================

    private void write(
            int... bytes
    ) {

        for (int b : bytes) {

            output.write(
                    b & 0xFF
            );
        }
    }
}