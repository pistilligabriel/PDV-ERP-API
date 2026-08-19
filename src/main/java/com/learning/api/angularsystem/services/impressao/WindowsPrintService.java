package com.learning.api.angularsystem.services.impressao;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

import org.springframework.stereotype.Service;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static com.sun.jna.Native.POINTER_SIZE;

@Service
public class WindowsPrintService {

    private static final String NOME_IMPRESSORA = "POS-58";

    /*
     * Charset utilizado quando imprimirTexto() for chamado.
     *
     * Se sua impressora aceitar UTF-8, pode deixar UTF_8.
     *
     * Muitas impressoras térmicas POS utilizam CP850,
     * CP437 ou Windows-1252.
     */
    private static final Charset CHARSET = StandardCharsets.UTF_8;

    private final Winspool winspool = Winspool.INSTANCE;

    /**
     * Interface com a API Winspool do Windows.
     */
    private interface Winspool extends StdCallLibrary {

        Winspool INSTANCE = Native.load(
                "winspool.drv",
                Winspool.class,
                W32APIOptions.UNICODE_OPTIONS
        );

        boolean OpenPrinterW(
                String pPrinterName,
                HANDLEByReference phPrinter,
                Pointer pDefault
        );

        boolean ClosePrinter(
                HANDLE hPrinter
        );

        int StartDocPrinterW(
                HANDLE hPrinter,
                int level,
                DOC_INFO_1 pDocInfo
        );

        boolean EndDocPrinter(
                HANDLE hPrinter
        );

        boolean StartPagePrinter(
                HANDLE hPrinter
        );

        boolean EndPagePrinter(
                HANDLE hPrinter
        );

        boolean WritePrinter(
                HANDLE hPrinter,
                byte[] pBuf,
                int cbBuf,
                IntByReference pcWritten
        );
    }

    /**
     * Referência para HANDLE retornado pelo Windows.
     */
    public static class HANDLEByReference extends com.sun.jna.ptr.ByReference {

        public HANDLEByReference() {
            super(POINTER_SIZE);
        }

        public HANDLE getValue() {
            Pointer pointer = getPointer().getPointer(0);

            if (pointer == null) {
                return null;
            }

            return new HANDLE(pointer);
        }
    }

    /**
     * Estrutura DOC_INFO_1 da API Winspool.
     */
    public static class DOC_INFO_1 extends Structure {

        public WString pDocName;
        public WString pOutputFile;
        public WString pDatatype;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(
                    "pDocName",
                    "pOutputFile",
                    "pDatatype"
            );
        }
    }

    /**
     * Envia bytes RAW diretamente para a impressora.
     *
     * Ideal para ESC/POS.
     */
    public void imprimir(byte[] dados) {

        validarDados(dados);

        HANDLEByReference printerReference =
                new HANDLEByReference();

        abrirImpressora(
                printerReference
        );

        HANDLE printer =
                printerReference.getValue();

        try {

            imprimirRaw(
                    printer,
                    dados
            );

        } finally {

            fecharImpressora(printer);
        }
    }

    /**
     * Imprime texto utilizando o charset configurado.
     */
    public void imprimirTexto(String texto) {

        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException(
                    "O texto para impressão não pode ser vazio."
            );
        }

        byte[] dados =
                texto.getBytes(CHARSET);

        imprimir(dados);
    }

    /**
     * Abre a impressora através da API do Windows.
     */
    private void abrirImpressora(
            HANDLEByReference printerReference
    ) {

        boolean aberta =
                winspool.OpenPrinterW(
                        NOME_IMPRESSORA,
                        printerReference,
                        null
                );

        if (!aberta) {

            throw erroWindows(
                    "Não foi possível abrir a impressora '"
                            + NOME_IMPRESSORA
                            + "'."
            );
        }

        if (printerReference.getValue() == null) {

            throw new RuntimeException(
                    "O Windows informou que a impressora foi aberta, "
                            + "mas não retornou um HANDLE válido."
            );
        }
    }

    /**
     * Fecha o HANDLE da impressora.
     */
    private void fecharImpressora(
            HANDLE printer
    ) {

        if (printer == null) {
            return;
        }

        boolean fechada =
                winspool.ClosePrinter(
                        printer
                );

        if (!fechada) {

            int erro =
                    Native.getLastError();

            System.err.println(
                    "Aviso: não foi possível fechar a impressora. "
                            + "Código do Windows: "
                            + erro
            );
        }
    }

    /**
     * Executa todo o fluxo de impressão RAW.
     */
    private void imprimirRaw(
            HANDLE printer,
            byte[] dados
    ) {

        DOC_INFO_1 docInfo =
                criarDocInfo();

        iniciarDocumento(
                printer,
                docInfo
        );

        boolean documentoFinalizado =
                false;

        try {

            iniciarPagina(printer);

            try {

                escreverDados(
                        printer,
                        dados
                );

            } finally {

                finalizarPagina(printer);
            }

        } finally {

            documentoFinalizado =
                    finalizarDocumento(printer);

            if (!documentoFinalizado) {

                System.err.println(
                        "Aviso: o Windows não conseguiu finalizar "
                                + "o documento de impressão."
                );
            }
        }
    }

    /**
     * Cria as informações do documento.
     */
    private DOC_INFO_1 criarDocInfo() {

        DOC_INFO_1 docInfo =
                new DOC_INFO_1();

        docInfo.pDocName =
                new WString(
                        "Impressao PDV"
                );

        /*
         * RAW informa ao Windows que os dados devem ser
         * enviados diretamente para a impressora.
         */
        docInfo.pDatatype =
                new WString("RAW");

        docInfo.pOutputFile =
                null;

        docInfo.write();

        return docInfo;
    }

    /**
     * Inicia o documento de impressão.
     */
    private void iniciarDocumento(
            HANDLE printer,
            DOC_INFO_1 docInfo
    ) {

        int jobId =
                winspool.StartDocPrinterW(
                        printer,
                        1,
                        docInfo
                );

        if (jobId == 0) {

            throw erroWindows(
                    "Não foi possível iniciar o documento "
                            + "de impressão."
            );
        }
    }

    /**
     * Inicia a página.
     */
    private void iniciarPagina(
            HANDLE printer
    ) {

        boolean iniciou =
                winspool.StartPagePrinter(
                        printer
                );

        if (!iniciou) {

            throw erroWindows(
                    "Não foi possível iniciar a página "
                            + "de impressão."
            );
        }
    }

    /**
     * Finaliza a página.
     */
    private void finalizarPagina(
            HANDLE printer
    ) {

        boolean finalizou =
                winspool.EndPagePrinter(
                        printer
                );

        if (!finalizou) {

            int erro =
                    Native.getLastError();

            System.err.println(
                    "Aviso: EndPagePrinter falhou. "
                            + "Código do Windows: "
                            + erro
            );
        }
    }

    /**
     * Finaliza o documento.
     */
    private boolean finalizarDocumento(
            HANDLE printer
    ) {

        return winspool.EndDocPrinter(
                printer
        );
    }

    /**
     * Envia os bytes RAW para a impressora.
     */
    private void escreverDados(
            HANDLE printer,
            byte[] dados
    ) {

        IntByReference bytesEscritos =
                new IntByReference();

        boolean sucesso =
                winspool.WritePrinter(
                        printer,
                        dados,
                        dados.length,
                        bytesEscritos
                );

        if (!sucesso) {

            throw erroWindows(
                    "Não foi possível enviar os dados "
                            + "para a impressora."
            );
        }

        int totalEscrito =
                bytesEscritos.getValue();

        if (totalEscrito != dados.length) {

            throw new RuntimeException(
                    "Impressão incompleta. "
                            + "Esperado: "
                            + dados.length
                            + " bytes, mas o Windows informou "
                            + totalEscrito
                            + " bytes enviados."
            );
        }
    }

    /**
     * Valida os dados antes de abrir a impressora.
     */
    private void validarDados(
            byte[] dados
    ) {

        if (dados == null) {

            throw new IllegalArgumentException(
                    "Os dados da impressão não podem ser null."
            );
        }

        if (dados.length == 0) {

            throw new IllegalArgumentException(
                    "Os dados da impressão não podem estar vazios."
            );
        }
    }

    /**
     * Cria uma exceção contendo o código de erro
     * retornado pelo Windows.
     */
    private RuntimeException erroWindows(
            String mensagem
    ) {

        int codigo =
                Native.getLastError();

        return new RuntimeException(
                mensagem
                        + " Código do Windows: "
                        + codigo
                        + "."
        );
    }
}