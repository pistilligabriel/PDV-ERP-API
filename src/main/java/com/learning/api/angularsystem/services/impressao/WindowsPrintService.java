package com.learning.api.angularsystem.services.impressao;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;


import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static com.sun.jna.Native.POINTER_SIZE;

@Service
public class WindowsPrintService {

    private static final String NOME_IMPRESSORA = "POS-58";

    private interface Winspool extends StdCallLibrary {

        Winspool INSTANCE = Native.load(
                "winspool.drv",
                Winspool.class,
                com.sun.jna.win32.W32APIOptions.UNICODE_OPTIONS
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

    public static class HANDLEByReference
            extends com.sun.jna.ptr.ByReference {

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

    public static class DOC_INFO_1
            extends Structure {

        public WString pDocName;
        public WString pOutputFile;
        public WString pDatatype;

        @Override
        protected List<String> getFieldOrder() {
                    "pDocName",
                    "pOutputFile",
                    "pDatatype"
            );
        }
    }

    public void imprimir(byte[] dados) {

        HANDLEByReference printerReference =
                new HANDLEByReference();

        boolean impressoraAberta =
                Winspool.INSTANCE.OpenPrinterW(
                        NOME_IMPRESSORA,
                        printerReference,
                        null
                );

        if (!impressoraAberta) {
            throw new RuntimeException(
                    "Não foi possível abrir a impressora: "
                            + NOME_IMPRESSORA
            );
        }

        HANDLE printer = printerReference.getValue();

        if (printer == null) {
            throw new RuntimeException(
                    "O Windows abriu a impressora, mas não retornou um HANDLE válido."
            );
        }

        try {

            DOC_INFO_1 docInfo =
                    new DOC_INFO_1();

            docInfo.pDocName =
                    new WString("Impressao PDV");

            docInfo.pDatatype =
                    new WString("RAW");

            docInfo.pOutputFile =
                    null;

            docInfo.write();

            int documentoIniciado =
                    Winspool.INSTANCE.StartDocPrinterW(
                            printer,
                            1,
                            docInfo
                    );

            if (documentoIniciado == 0) {

                int erro =
                        Native.getLastError();

                throw new RuntimeException(
                        "Não foi possível iniciar o trabalho de impressão. "
                                + "Código do Windows: "
                                + erro
                );
            }
            try {

                boolean paginaIniciada =
                        Winspool.INSTANCE.StartPagePrinter(
                                printer
                        );

                if (!paginaIniciada) {
                    throw new RuntimeException(
                            "Não foi possível iniciar a página de impressão."
                    );
                }

                try {

                    IntByReference bytesEscritos =
                            new IntByReference();

                    boolean escrita =
                            Winspool.INSTANCE.WritePrinter(
                                    printer,
                                    dados,
                                    dados.length,
                                    bytesEscritos
                            );

                    if (!escrita) {
                        throw new RuntimeException(
                                "Não foi possível enviar os dados para a impressora."
                        );
                    }

                    int totalEscrito =
                            bytesEscritos.getValue();

                    if (totalEscrito != dados.length) {
                        throw new RuntimeException(
                                "Impressão incompleta. "
                                        + "Esperado: "
                                        + dados.length
                                        + " bytes, enviado: "
                                        + totalEscrito
                        );
                    }

                    System.out.println(
                            "Impressão enviada com sucesso."
                    );

                    System.out.println(
                            "Bytes enviados: "
                                    + totalEscrito
                    );

                } finally {

                    Winspool.INSTANCE.EndPagePrinter(
                            printer
                    );
                }

            } finally {

                Winspool.INSTANCE.EndDocPrinter(
                        printer
                );
            }

        } finally {

            Winspool.INSTANCE.ClosePrinter(
                    printer
            );
        }
    }

    public void imprimirTexto(String texto) {

        byte[] dados =
                texto.getBytes(
                        StandardCharsets.UTF_8
                );

        imprimir(dados);
    }
}