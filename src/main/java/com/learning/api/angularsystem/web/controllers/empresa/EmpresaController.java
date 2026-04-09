package com.learning.api.angularsystem.web.controllers.empresa;


import com.learning.api.angularsystem.entitys.empresa.Empresa;
import com.learning.api.angularsystem.enums.TipoEmpresa;
import com.learning.api.angularsystem.services.empresa.EmpresaService;
import com.learning.api.angularsystem.web.dtos.empresa.EmpresaDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;


@RestController
@RequestMapping("/api/v1/empresa")
public class EmpresaController {

    private final EmpresaService empresaService;

    public EmpresaController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }


    @GetMapping("/{id}")
    public ResponseEntity<Empresa> getEmpresa() {
        Empresa configuracao = empresaService.obterConfiguracao();
        return ResponseEntity.status(HttpStatus.OK).body(configuracao);
    }

    @GetMapping("/{id}/logo")
    public ResponseEntity<byte[]> getLogo(@PathVariable Long id) {
        byte[] image = empresaService.getLogoById(id); // retorna o array de bytes da imagem
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(image, headers, HttpStatus.OK);
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Empresa> editarConfiguracao(   @RequestParam(value="file",required = false) MultipartFile file,
                                                         @RequestParam("nomeEmpresa") String nomeEmpresa,
                                                         @RequestParam("tipoEmpresa") TipoEmpresa tipoEmpresa) {
        Empresa empresa = empresaService.editarConfiguracao(nomeEmpresa, file,tipoEmpresa);
        return ResponseEntity.ok(empresa);
    }
}