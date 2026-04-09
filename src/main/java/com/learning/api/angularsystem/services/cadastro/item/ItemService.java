package com.learning.api.angularsystem.services.cadastro.item;

import com.learning.api.angularsystem.entitys.cadastro.item.Fabricante;
import com.learning.api.angularsystem.entitys.cadastro.item.Item;
import com.learning.api.angularsystem.entitys.cadastro.item.UnidadeMedida;
import com.learning.api.angularsystem.entitys.empresa.Empresa;
import com.learning.api.angularsystem.enums.Status;
import com.learning.api.angularsystem.enums.TipoEmpresa;
import com.learning.api.angularsystem.repositories.cadastro.item.ItemRepository;
import com.learning.api.angularsystem.services.empresa.EmpresaService;
import com.learning.api.angularsystem.web.dtos.cadastro.item.ItemDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final FabricanteService fabricanteService;
    private final UnidadeMedidaService unidadeService;
    private EmpresaService empresaService = null;

    public ItemService(ItemRepository itemRepository, FabricanteService fabricanteService, UnidadeMedidaService unidadeService, EmpresaService empresaService) {
        this.itemRepository = itemRepository;
        this.fabricanteService = fabricanteService;
        this.unidadeService = unidadeService;
        this.empresaService = empresaService;
    }


    @Transactional()
    public Item salvar(Item item) {
        return itemRepository.save(item);
    }

    @Transactional()
    public Item criarItem(ItemDto item) {
        Empresa empresa = empresaService.obterConfiguracao();

        Item itemEntity = new Item();

        itemEntity.setDataCadastro(LocalDateTime.now());
        itemEntity.setDescricao(item.getDescricao());
        if (empresa.getTipoEmpresa() == TipoEmpresa.PECAS) {
            itemEntity.setTipoProduto(item.getTipoProduto());
        }

        itemEntity.setObservacao(item.getObservacao());

        if (item.getPrecoCusto() == null) {
            itemEntity.setPrecoCusto(0.0);
        } else {
            itemEntity.setPrecoCusto(item.getPrecoCusto());
        }

        itemEntity.setPrecoVenda(item.getPrecoVenda());
        if (empresa.getTipoEmpresa() == TipoEmpresa.PECAS) {
            itemEntity.setEstoque(item.getEstoque());
        }else {
            itemEntity.setEstoque(1);
        }

        itemEntity.setMargemLucro(itemEntity.calcularMargemLucro());

        itemEntity.setModelo(item.getModelo());

        itemEntity.setTamanho(item.getTamanho());

        Fabricante fabricante = fabricanteService.getById(item.getFabricante());
        itemEntity.setFabricante(fabricante);

        UnidadeMedida unidade = unidadeService.getById(item.getUnidadeVenda());
        itemEntity.setUnidadeVenda(unidade);
        return itemRepository.save(itemEntity);
    }

    @Transactional(readOnly = true)
    public List<Item> listarProdutos() {
        return itemRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Item buscarProduto(Long codigo) {
        return itemRepository.findById(codigo).orElseThrow(
                () -> new RuntimeException("Produto não encontrado!")
        );
    }

    @Transactional()
    public Item alterarStatus(Long codigo) {
        Item item = buscarProduto(codigo);
        if (item.getStatus().equals(Status.ATIVO) && item.getEstoque() == 0) {
            item.setStatusInativo();
        } else if (item.getStatus().equals(Status.ATIVO) && item.getEstoque() > 0) {
            throw new RuntimeException("Não é possível desativar produto com estoque!");
        } else {
            item.setStatusAtivo();
        }
        return item;
    }

    @Transactional()
    public Item deletarProduto(Long codigo) {
        Item item = buscarProduto(codigo);
        if (item.getStatus().equals(Status.DESATIVADO)) {
            itemRepository.deleteById(item.getCodigo());
        } else {
            throw new RuntimeException("Produto não pode ser deletado!");
        }
        return item;
    }

    @Transactional()
    public Item editarItem(ItemDto dto) {
        Empresa empresa = empresaService.obterConfiguracao();

        Item itemAtualizar = buscarProduto(dto.getCodigo());


        if (!itemAtualizar.getStatus().equals(Status.ATIVO)) {
            throw new RuntimeException("Produto não pode ser alterado, pois está desativado!");
        }

        itemAtualizar.setDescricao(dto.getDescricao());
        if (empresa.getTipoEmpresa() == TipoEmpresa.PECAS) {
            itemAtualizar.setTipoProduto(dto.getTipoProduto());
        }
        itemAtualizar.setModelo(dto.getModelo());
//        itemAtualizar.setGrupoItem(dto.getGrupoItem());
        itemAtualizar.setObservacao(dto.getObservacao());
        itemAtualizar.setUnidadeVenda(unidadeService.getById(dto.getUnidadeVenda()));
        itemAtualizar.setFabricante(fabricanteService.getById(dto.getFabricante()));
        itemAtualizar.setPrecoCusto(dto.getPrecoCusto());
        itemAtualizar.setPrecoVenda(dto.getPrecoVenda());
        itemAtualizar.setVersao(LocalDateTime.now());

        itemAtualizar.calcularMargemLucro();

        return itemRepository.save(itemAtualizar);
    }

    @Transactional
    public Item acertoEstoqueProduto(Long codigo, int estoque) {
        Item item = buscarProduto(codigo);
        if (item.getStatus().equals(Status.ATIVO)) {
            if (item.getEstoque() + estoque < 0) {
                throw new RuntimeException("Quantidade não pode ser negativa");
            }
            item.setEstoque(item.getEstoque() + estoque);
        }
        return salvar(item);
    }

}
