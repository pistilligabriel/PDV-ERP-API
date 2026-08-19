package com.learning.api.angularsystem.services.impressao;

import com.learning.api.angularsystem.services.impressao.EscPosBuilder;
import com.learning.api.angularsystem.services.impressao.WindowsPrintService;
import org.springframework.stereotype.Service;

@Service
public class ImpressaoCupomService {

    private final WindowsPrintService printer;

    public ImpressaoCupomService(
            WindowsPrintService printer
    ) {
        this.printer = printer;
    }

    public void imprimirTeste() {

        EscPosBuilder cupom =
                new EscPosBuilder();

        cupom
                .centralizar()
                .negrito(true)
                .tamanho2x()
                .texto("MINHA LOJA")
                .quebraLinha()
                .tamanhoNormal()
                .negrito(false)

                .texto("CUPOM DE TESTE")
                .quebraLinha()
                .quebraLinha()

                .esquerda()
                .texto("Produto: Coca-Cola")
                .quebraLinha()
                .texto("Valor: R$ 10,00")
                .quebraLinha()
                .quebraLinha()

                .ean13("789123456789")

                .alimentar(5);

        printer.imprimir(
                cupom.build()
        );
    }
}