package com.learning.api.angularsystem.services.impressao;

import com.learning.api.angularsystem.entitys.cadastro.item.Item;
import com.learning.api.angularsystem.services.cadastro.item.ItemService;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

@Service
public class ImpressaoService {

    private final WindowsPrintService windowsPrintService;
    private final ItemService itemService;

    /*
     * POS-58 / DYJ-5801 (~203 DPI = 8 dots/mm)
     * 40 mm = 320 dots
     */
    private static final int DOTS_POR_MM = 8;
    private static final int LARGURA_ETIQUETA = 40 * DOTS_POR_MM; // 320 dots
    private static final int ALTURA_ETIQUETA = 350;  // 320 dots
    private static final int GAP_DOTS = 3;

    public ImpressaoService(WindowsPrintService windowsPrintService, ItemService itemService) {
        this.windowsPrintService = windowsPrintService;
        this.itemService = itemService;
    }

    // ============================================================
    // ETIQUETA 40 x 40 MM (CORRIGIDA)
    // ============================================================

    // ============================================================
    // ETIQUETA 40 x 40 MM
    // ============================================================

    public void imprimirEtiqueta40x40(Long codigoItem) {

        Item item = itemService.buscarProduto(codigoItem);

        validarCodigo12(item.getCodigoBarras());

        if (item.getDescricao() == null || item.getDescricao().isBlank()) {
            throw new IllegalArgumentException("A descrição da etiqueta não pode ser vazia.");
        }

        if (item.getPrecoVenda() == null) {
            throw new IllegalArgumentException("O preço da etiqueta não pode ser vazio.");
        }



        byte[] inicializacao = { 0x1B, 0x40 };
        byte[] centralizar = { 0x1B, 0x61, 0x01 };

        byte[] etiquetaBitmap = gerarBitmapEtiqueta40x40(
                item.getDescricao(),
                item.getCodigoBarras(),
                String.valueOf(item.getPrecoVenda())
        );

        /*
         * AJUSTE DE AVANÇO (GAP):
         *
         * 1. Comando ESC J (0x1B, 0x4A, n) avança 'n' dots (1 mm ≈ 8 dots).
         *    Aumentamos aqui para avançar o espaço entre etiquetas.
         */
        byte[] avancarGapDots = {
                0x1B,
                0x4A,
                (byte) 16 // Tente valores de 8 a 24 dots (1 mm a 3 mm)
        };

        byte[] esquerda = { 0x1B, 0x61, 0x00 };

// Monte a sequência SEM quebras de linha '\n' no final
        byte[] dados = concatenar(
                inicializacao,
                centralizar,
                etiquetaBitmap,
                avancarGapDots,
                esquerda
        );

        windowsPrintService.imprimir(dados);
    }

    // ============================================================
    // BITMAP DA ETIQUETA 40 x 40 (REPOSICIONADO E FONTE MAIOR)
    // ============================================================

    // ============================================================
    // BITMAP DA ETIQUETA 40 x 40 (COM DESLOCAMENTO PARA A DIREITA)
    // ============================================================

    private byte[] gerarBitmapEtiqueta40x40(
            String descricao,
            String codigoCompleto,
            String preco
    ) {

        final int LARGURA = LARGURA_ETIQUETA; // 320
        final int ALTURA = ALTURA_ETIQUETA;   // 320

        // Ajuste horizontal físico da impressora (Positivo = joga para a direita)
        final int OFFSET_X = 40;

        BufferedImage imagem = new BufferedImage(
                LARGURA,
                ALTURA,
                BufferedImage.TYPE_BYTE_BINARY
        );

        Graphics2D graphics = imagem.createGraphics();

        try {
            // Desativa antialiasing para manter as bordas nítidas
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

            // Fundo Branco
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, LARGURA, ALTURA);

            graphics.setColor(Color.BLACK);

            // ========================================================
            // 1. DESCRIÇÃO
            // ========================================================
            graphics.setFont(new Font("Arial", Font.BOLD, 26));

            String descricaoAjustada = ajustarTexto(
                    graphics,
                    descricao,
                    LARGURA - 20
            );

            FontMetrics metricasDesc = graphics.getFontMetrics();

            // Centralizado + OFFSET_X para empurrar para a direita
            int xDescricao = ((LARGURA - metricasDesc.stringWidth(descricaoAjustada)) / 2) + OFFSET_X;

            graphics.drawString(
                    descricaoAjustada,
                    xDescricao,
                    50
            );

            // ========================================================
            // 2. CÓDIGO DE BARRAS
            // ========================================================
            int alturaCodigoBarra = 120;
            int yCodigoBarra = 75;

            desenharEan13NaImagem(
                    graphics,
                    codigoCompleto,
                    OFFSET_X, // Passamos o offset para alinhar o código de barras junto
                    yCodigoBarra,
                    LARGURA,
                    alturaCodigoBarra
            );

            // ========================================================
            // 3. PREÇO
            // ========================================================
            graphics.setFont(new Font("Arial", Font.BOLD, 30));

            String textoPreco = preco.trim();

            if (!textoPreco.toUpperCase().startsWith("R$")) {
                textoPreco = "R$ " + textoPreco;
            }

            FontMetrics metricasPreco = graphics.getFontMetrics();

            // Centralizado + OFFSET_X para empurrar para a direita
            int xPreco = ((LARGURA - metricasPreco.stringWidth(textoPreco)) / 2) + OFFSET_X;

            graphics.drawString(
                    textoPreco,
                    xPreco,
                    245
            );

        } finally {
            graphics.dispose();
        }

        return converterImagemParaEscPos(imagem);
    }

    // ============================================================
    // DESENHA EAN NA IMAGEM DA ETIQUETA
    // ============================================================

    private void desenharEan13NaImagem(
            Graphics2D graphics,
            String codigo,
            int xOffset,
            int y,
            int larguraDisponivel,
            int altura
    ) {

        String[] L = {"0001101", "0011001", "0010011", "0111101", "0100011", "0110001", "0101111", "0111011", "0110111", "0001011"};
        String[] G = {"0100111", "0110011", "0011011", "0100001", "0011101", "0111001", "0000101", "0010001", "0001001", "0010111"};
        String[] R = {"1110010", "1100110", "1101100", "1000010", "1011100", "1001110", "1010000", "1000100", "1001000", "1110100"};
        String[] PARIDADE = {"LLLLLL", "LLGLGG", "LLGGLG", "LLGGGL", "LGLLGG", "LGGLLG", "LGGGLL", "LGLGLG", "LGLGGL", "LGGLGL"};

        int primeiroDigito = codigo.charAt(0) - '0';
        String padrao = PARIDADE[primeiroDigito];

        StringBuilder barras = new StringBuilder();
        barras.append("101"); // Guarda inicial

        for (int i = 1; i <= 6; i++) {
            int digito = codigo.charAt(i) - '0';
            barras.append(padrao.charAt(i - 1) == 'L' ? L[digito] : G[digito]);
        }

        barras.append("01010"); // Guarda central

        for (int i = 7; i <= 12; i++) {
            int digito = codigo.charAt(i) - '0';
            barras.append(R[digito]);
        }

        barras.append("101"); // Guarda final

        final int ESCALA = 2;
        final int LARGURA_CODIGO = 95 * ESCALA; // 190 dots

        // Centralização exata somando o deslocamento físico da impressora
        int inicioX = xOffset + (larguraDisponivel - LARGURA_CODIGO) / 2;

        graphics.setColor(Color.BLACK);
        for (int i = 0; i < 95; i++) {
            if (barras.charAt(i) == '1') {
                graphics.fillRect(
                        inicioX + (i * ESCALA),
                        y,
                        ESCALA,
                        altura
                );
            }
        }
    }

    // ============================================================
    // MÉTODOS AUXILIARES (INALTERADOS / MANTIDOS)
    // ============================================================

    private byte[] converterImagemParaEscPos(BufferedImage imagem) {
        int largura = imagem.getWidth();
        int altura = imagem.getHeight();
        int bytesPorLinha = (largura + 7) / 8;

        ByteArrayOutputStream saida = new ByteArrayOutputStream();

        saida.write(0x1D);
        saida.write(0x76);
        saida.write(0x30);
        saida.write(0x00);

        saida.write(bytesPorLinha & 0xFF);
        saida.write((bytesPorLinha >> 8) & 0xFF);
        saida.write(altura & 0xFF);
        saida.write((altura >> 8) & 0xFF);

        for (int y = 0; y < altura; y++) {
            for (int byteIndex = 0; byteIndex < bytesPorLinha; byteIndex++) {
                int valor = 0;
                for (int bit = 0; bit < 8; bit++) {
                    int x = byteIndex * 8 + bit;
                    if (x >= largura) continue;

                    int pixel = imagem.getRGB(x, y);
                    int vermelho = (pixel >> 16) & 0xFF;
                    int verde = (pixel >> 8) & 0xFF;
                    int azul = pixel & 0xFF;
                    int luminosidade = (vermelho + verde + azul) / 3;

                    if (luminosidade < 128) {
                        valor |= 1 << (7 - bit);
                    }
                }
                saida.write(valor);
            }
        }
        return saida.toByteArray();
    }

    private int calcularDigitoEan13(String codigo12) {
        int soma = 0;
        for (int i = 0; i < codigo12.length(); i++) {
            int digito = codigo12.charAt(i) - '0';
            soma += (i % 2 == 0) ? digito : digito * 3;
        }
        return (10 - (soma % 10)) % 10;
    }

    private void validarCodigo12(String codigo12) {
        if (codigo12 == null || !codigo12.matches("\\d{13}")) {
            throw new IllegalArgumentException("Informe exatamente 12 dígitos para o EAN-13.");
        }
    }

    private String ajustarTexto(Graphics2D graphics, String texto, int larguraMaxima) {
        texto = texto.trim().toUpperCase();
        FontMetrics metricas = graphics.getFontMetrics();
        if (metricas.stringWidth(texto) <= larguraMaxima) {
            return texto;
        }
        String resultado = texto;
        while (resultado.length() > 3 && metricas.stringWidth(resultado + "...") > larguraMaxima) {
            resultado = resultado.substring(0, resultado.length() - 1);
        }
        return resultado + "...";
    }

    private byte[] concatenar(byte[]... arrays) {
        int tamanho = 0;
        for (byte[] array : arrays) tamanho += array.length;
        byte[] resultado = new byte[tamanho];
        int posicao = 0;
        for (byte[] array : arrays) {
            System.arraycopy(array, 0, resultado, posicao, array.length);
            posicao += array.length;
        }
        return resultado;
    }
}