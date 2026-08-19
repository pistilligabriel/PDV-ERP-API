package com.learning.api.angularsystem.web.controllers.cadastro.item;

import com.learning.api.angularsystem.entitys.cadastro.item.Item;
import com.learning.api.angularsystem.services.cadastro.item.ItemService;
import com.learning.api.angularsystem.web.dtos.cadastro.item.ItemDto;
import com.learning.api.angularsystem.web.dtos.cadastro.item.ItemResponseDto;
import com.learning.api.angularsystem.web.dtos.cadastro.item.mapper.ItemMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/produtos")
public class ItemController {

    @Autowired
    private ItemService itemService;


    @PostMapping
    public ResponseEntity<ItemResponseDto> cadastrarProduto(@RequestBody @Valid ItemDto itemDto) {
        Item item = itemService.criarItem(itemDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ItemMapper.toDto(item));
    }

    @GetMapping("/gerar-codigo-barras")
    public ResponseEntity<String> gerarCodigoBarras() {
        System.out.println("Chegou no controller");
        String codigo = itemService.gerarCodigoBarras();

        return ResponseEntity.ok(codigo);
    }

    @GetMapping
    public List<ItemResponseDto> listarProdutos() {
        List<Item> itens = itemService.listarProdutos();
        return ResponseEntity.status(HttpStatus.OK).body(ItemMapper.toListDto(itens)).getBody();
    }

    @GetMapping("/venda")
    public List<ItemResponseDto> listarProdutosVenda() {
        List<Item> itens = itemService.listarProdutosVenda();
        return ResponseEntity.status(HttpStatus.OK).body(ItemMapper.toListDto(itens)).getBody();
    }

    @GetMapping("/pesquisar")
    public ResponseEntity<List<ItemResponseDto>> pesquisar(
            @RequestParam String termo) {

        List<Item> itens = itemService.pesquisar(termo);

        return ResponseEntity.ok(ItemMapper.toListDto(itens));
    }


    @GetMapping("/{codigo}")
    public ResponseEntity<ItemResponseDto> buscarProduto(@PathVariable Long codigo) {
        Item item = itemService.buscarProduto(codigo);
        return ResponseEntity.status(HttpStatus.OK).body(ItemMapper.toDto(item));
    }

    @GetMapping("/codigo-barras/{codigoBarras}")
    public ResponseEntity<ItemResponseDto> buscarProdutoCodigoBarras(@PathVariable String codigoBarras) {
        Item item = itemService.buscarProdutoCodigoBarras(codigoBarras);
        return ResponseEntity.status(HttpStatus.OK).body(ItemMapper.toDto(item));
    }


    @PutMapping
    public ResponseEntity<ItemResponseDto> atualizarProduto(@RequestBody @Valid ItemDto itemDto) {
        Item item = itemService.editarItem(itemDto);
        return ResponseEntity.status(HttpStatus.OK).body(ItemMapper.toDto(item));
    }

    @PatchMapping("/entrada/{codigo}/{quantidade}")
    public ResponseEntity<ItemResponseDto> entradaEstoqueProduto(
            @PathVariable Long codigo,
            @PathVariable int quantidade) {

        Item item = itemService.entradaEstoqueProduto(codigo, quantidade);

        return ResponseEntity.ok(ItemMapper.toDto(item));
    }

    @PatchMapping("/saida/{codigo}/{quantidade}")
    public ResponseEntity<ItemResponseDto> saidaEstoqueProduto(
            @PathVariable Long codigo,
            @PathVariable int quantidade) {

        Item item = itemService.saidaEstoqueProduto(codigo, quantidade);

        return ResponseEntity.ok(ItemMapper.toDto(item));
    }

    @PostMapping("/alterar-status/{codigo}")
    public ResponseEntity<ItemResponseDto> alterarStatusProduto(@PathVariable Long codigo) {
        itemService.alterarStatus(codigo);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{codigo}")
    public ResponseEntity<ItemResponseDto> deletarProduto(@PathVariable Long codigo) {
        itemService.deletarProduto(codigo);
        return ResponseEntity.noContent().build();
    }
}
