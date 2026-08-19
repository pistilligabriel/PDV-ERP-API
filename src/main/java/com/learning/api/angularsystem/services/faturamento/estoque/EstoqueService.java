package com.learning.api.angularsystem.services.faturamento.estoque;

import com.learning.api.angularsystem.entitys.cadastro.item.Item;
import com.learning.api.angularsystem.repositories.cadastro.integrante.IntegranteRepository;
import com.learning.api.angularsystem.repositories.cadastro.item.ItemRepository;
import com.learning.api.angularsystem.repositories.cadastro.item.UnidadeMedidaRepository;
import com.learning.api.angularsystem.repositories.faturamento.pedido.PedidoDetalheRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EstoqueService {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UnidadeMedidaRepository unidadeMedidaRepository;
    @Autowired
    private IntegranteRepository integranteRepository;
    @Autowired
    private PedidoDetalheRepository pedidoDetalheRepository;

    public ResponseEntity<Void> cadastrarEstoque(){
//        EstoqueEntity estoque = new EstoqueEntity(estoqueDto);
//
//        if (estoqueDto.item() != null) {
//            itemRepository.findById(estoqueDto.item().getCODIGO()).ifPresent(estoque::setItem);
//        }
//
//        if (estoqueDto.unidadeMedida() != null) {
//            unidadeMedidaRepository.findById(estoqueDto.unidadeMedida().getCODIGO())
//                    .ifPresent(estoque::setUnidadeMedida);
//        }
//
//        if (estoqueDto.funcionario() != null) {
//            integranteRepository.findById(estoqueDto.funcionario().getCODIGO())
//                    .ifPresent(estoque::setFuncionario);
//        }
//
//        if (estoqueDto.pedidoDetalhe() != null) {
//            pedidoDetalheRepository.findById(estoqueDto.pedidoDetalhe().getCODIGO())
//                    .ifPresent(estoque::setPedidoDetalhe);
//        }
//
//        if (estoqueDto.ordemServicoDetalhe() != null) {
//            ordemServicoDetalheRepository.findById(estoqueDto.ordemServicoDetalhe().getCODIGO())
//                    .ifPresent(estoque::setOrdemServicoDetalhe);
//        }
//
//        if (estoqueDto.notaFiscalDetalhe() != null) {
//            notaFiscalDetalheRepository.findById(estoqueDto.notaFiscalDetalhe().getCODIGO())
//                    .ifPresent(estoque::setNotaFiscalDetalhe);
//        }
//
//        estoqueRepository.save(estoque);
//
//        return new ResponseEntity<>(HttpStatus.CREATED);
        return null;
    }

    public ResponseEntity<Void> atualizarEstoque(){
//        EstoqueEntity estoque = estoqueRepository.findById(estoqueDto.CODIGO())
//                .orElseThrow(() -> new RuntimeException("Estoque não encontrado"));
//
//        estoque.atualizarEstoque(estoqueDto);
//
//        if (estoqueDto.item() != null) {
//            itemRepository.findById(estoqueDto.item().getCODIGO()).ifPresent(estoque::setItem);
//        } else {
//            estoque.setItem(null);
//        }
//
//        if (estoqueDto.unidadeMedida() != null) {
//            unidadeMedidaRepository.findById(estoqueDto.unidadeMedida().getCODIGO())
//                    .ifPresent(estoque::setUnidadeMedida);
//        } else {
//            estoque.setUnidadeMedida(null);
//        }
//
//        if (estoqueDto.funcionario() != null) {
//            integranteRepository.findById(estoqueDto.funcionario().getCODIGO())
//                    .ifPresent(estoque::setFuncionario);
//        } else {
//            estoque.setFuncionario(null);
//        }
//
//        if (estoqueDto.pedidoDetalhe() != null) {
//            pedidoDetalheRepository.findById(estoqueDto.pedidoDetalhe().getCODIGO())
//                    .ifPresent(estoque::setPedidoDetalhe);
//        } else {
//            estoque.setPedidoDetalhe(null);
//        }
//
//        if (estoqueDto.ordemServicoDetalhe() != null) {
//            ordemServicoDetalheRepository.findById(estoqueDto.ordemServicoDetalhe().getCODIGO())
//                    .ifPresent(estoque::setOrdemServicoDetalhe);
//        } else {
//            estoque.setOrdemServicoDetalhe(null);
//        }
//
//        if (estoqueDto.notaFiscalDetalhe() != null) {
//            notaFiscalDetalheRepository.findById(estoqueDto.notaFiscalDetalhe().getCODIGO())
//                    .ifPresent(estoque::setNotaFiscalDetalhe);
//        } else {
//            estoque.setNotaFiscalDetalhe(null);
//        }
//
//        estoqueRepository.save(estoque);
//
//        return ResponseEntity.ok(new EstoqueDto(estoque));
        return null;
    }

    @Transactional
    public void baixarEstoque(Item item, Integer quantidade){

        if(item.getEstoque() < quantidade){
            throw new RuntimeException("Estoque insuficiente.");
        }

        item.setEstoque(item.getEstoque() - quantidade);
    }
}
