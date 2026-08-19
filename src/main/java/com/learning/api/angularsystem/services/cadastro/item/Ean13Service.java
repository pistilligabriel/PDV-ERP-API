package com.learning.api.angularsystem.services.cadastro.item;

import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Service
public class Ean13Service {

    public String gerar() {

        String base = gerarBase();

        int digitoVerificador = calcularDigitoVerificador(base);

        return base + digitoVerificador;
    }

    private String gerarBase() {

        // Prefixo interno da aplicação.
        // Não representa um prefixo GS1 oficial.
        String prefixo = "200";

        String sequencia = String.format(
                "%09d",
                ThreadLocalRandom.current().nextLong(1_000_000_000L)
        );

        return prefixo + sequencia;
    }

    private int calcularDigitoVerificador(String codigo) {

        int soma = 0;

        for (int i = 0; i < codigo.length(); i++) {

            int numero = Character.getNumericValue(codigo.charAt(i));

            if (i % 2 == 0) {
                soma += numero;
            } else {
                soma += numero * 3;
            }
        }

        int resto = soma % 10;

        return resto == 0 ? 0 : 10 - resto;
    }
}