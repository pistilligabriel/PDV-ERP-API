package com.learning.api.angularsystem.web.controllers.impressao;

import com.learning.api.angularsystem.services.impressao.ImpressaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/impressao")
public class ImpressaoController {

    private final ImpressaoService impressaoService;

    public ImpressaoController(
            ImpressaoService impressaoService
    ) {
        this.impressaoService = impressaoService;
    }

    @PostMapping("/teste")
    public ResponseEntity<String> imprimirTeste() {

        impressaoService.imprimirTeste();

        return ResponseEntity.ok(
                "Impressão enviada para a POS-58."
        );
    }
}