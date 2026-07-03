package com.learning.api.angularsystem.web.controllers.financeiro;

import com.learning.api.angularsystem.entitys.financeiro.Caixa;
import com.learning.api.angularsystem.services.financeiro.CaixaService;
import com.learning.api.angularsystem.web.dtos.financeiro.CaixaDto;
import com.learning.api.angularsystem.web.dtos.financeiro.CaixaResponseDto;
import com.learning.api.angularsystem.web.dtos.financeiro.mapper.CaixaMapper;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/caixas")
public class CaixaController {

    private final CaixaService _service;

    public CaixaController(CaixaService _service) {
        this._service = _service;
    }

    @PostMapping
    public ResponseEntity<CaixaResponseDto> abrirCaixa(@RequestBody CaixaDto dto){
        Caixa caixa = _service.abrirCaixa(CaixaMapper.toCaixa(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(CaixaMapper.toDto(caixa));
    }

    @GetMapping
    public ResponseEntity<List<CaixaResponseDto>> listarCaixas(){
        List<Caixa> caixas = _service.getAllCaixas();
        return ResponseEntity.status(HttpStatus.OK).body(CaixaMapper.toListDto(caixas));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CaixaResponseDto> listarCaixa(@PathVariable Long id){
        Caixa caixa = _service.getCaixaById(id);
        return ResponseEntity.status(HttpStatus.OK).body(CaixaMapper.toDto(caixa));
    }

    @PatchMapping("/fechar-caixa/{id}")
    public String fecharCaixa(@PathVariable Long id){
        Caixa caixa = _service.fecharCaixa(id);
        return "Caixa Fechado com Sucesso!";
    }
}
