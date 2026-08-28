package com.learning.api.angularsystem.web.controllers.faturamento.pedido;

import com.learning.api.angularsystem.entitys.faturamento.pedido.Pedido;
import com.learning.api.angularsystem.entitys.faturamento.pedido.PedidoDetalhe;
import com.learning.api.angularsystem.repositories.faturamento.pedido.PedidoRepository;
import com.learning.api.angularsystem.services.faturamento.pedido.PedidoService;
import com.learning.api.angularsystem.web.dtos.dashboard.DashboardVendasResponseDTO;
import com.learning.api.angularsystem.web.dtos.faturamento.pedido.DetalheResponseDto;
import com.learning.api.angularsystem.web.dtos.faturamento.pedido.PedidoDetalheDto;
import com.learning.api.angularsystem.web.dtos.faturamento.pedido.PedidoDto;
import com.learning.api.angularsystem.web.dtos.faturamento.pedido.ResponsePedidoDto;
import com.learning.api.angularsystem.web.dtos.faturamento.pedido.mapper.PedidoMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;
    @Autowired
    private PedidoRepository pedidoRepository;


    @PostMapping
    public ResponseEntity<ResponsePedidoDto> cadastrarPedido(@RequestBody @Valid PedidoDto pedidoDto) {
        Pedido pedido = pedidoService.criarPedido(pedidoDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(PedidoMapper.toDto(pedido));
    }

    @GetMapping
    public ResponseEntity<List<ResponsePedidoDto>> listarPedidos() {
        List<Pedido> pedidos = pedidoService.buscarPedidos();
        return ResponseEntity.status(HttpStatus.OK).body(PedidoMapper.toResponseListDto(
                pedidos
        ));
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<ResponsePedidoDto> buscarPedido(@PathVariable Long codigo) {
        Pedido pedido = pedidoService.buscarPedidoComRelacionamentos(codigo);
        return ResponseEntity.status(HttpStatus.OK).body(PedidoMapper.toDto(pedido));
    }

    @PutMapping
    public ResponseEntity<ResponsePedidoDto> atualizarPedido(@RequestBody @Valid PedidoDto pedidoDto) {
       return null;
    }

    @PostMapping("/cancelar/{codigo}")
    public ResponseEntity<ResponsePedidoDto> cancelarPedido(@PathVariable Long codigo) {
        Pedido pedido = pedidoService.cancelarPedido(codigo);
        return ResponseEntity.status(HttpStatus.OK).body(PedidoMapper.toDto(pedido));
    }

    @DeleteMapping("/{CODIGO}")
    public ResponseEntity<ResponsePedidoDto> deletarPedido(@PathVariable Long CODIGO) {
        return null;
    }

    @PatchMapping("{codigo}/detalhe")
    public ResponseEntity<DetalheResponseDto> adicionarDetalhe(@PathVariable Long codigo,
                                                            @RequestBody PedidoDetalheDto dto) {
        PedidoDetalhe detalhe = pedidoService.salvarDetalhe(codigo, PedidoMapper.toDetalhe(dto) );
        return ResponseEntity.ok(PedidoMapper.toDto(detalhe));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardVendasResponseDTO> obterDashboard(
            @RequestParam("dataInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam("dataFim") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {

        LocalDateTime inicio = dataInicio.atStartOfDay();
        LocalDateTime fim = dataFim.atTime(LocalTime.MAX);

        DashboardVendasResponseDTO response = pedidoService.obterDadosDashboard(inicio, fim);
        return ResponseEntity.ok(response);
    }
}
