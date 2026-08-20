package com.learning.api.angularsystem.services.cadastro.item;

import com.learning.api.angularsystem.entitys.cadastro.item.Fabricante;
import com.learning.api.angularsystem.entitys.cadastro.item.Item;
import com.learning.api.angularsystem.entitys.cadastro.item.UnidadeMedida;
import com.learning.api.angularsystem.entitys.empresa.Empresa;
import com.learning.api.angularsystem.enums.Status;
import com.learning.api.angularsystem.repositories.cadastro.item.ItemRepository;
import com.learning.api.angularsystem.services.empresa.EmpresaService;
import com.learning.api.angularsystem.web.dtos.cadastro.item.ItemDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final FabricanteService fabricanteService;
    private final UnidadeMedidaService unidadeService;
    private EmpresaService empresaService = null;
    private final Ean13Service ean13Service;

    public ItemService(ItemRepository itemRepository, FabricanteService fabricanteService, UnidadeMedidaService unidadeService, EmpresaService empresaService,Ean13Service ean13Service) {
        this.itemRepository = itemRepository;
        this.fabricanteService = fabricanteService;
        this.unidadeService = unidadeService;
        this.empresaService = empresaService;
        this.ean13Service = ean13Service;
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

        itemEntity.setObservacao(item.getObservacao());

        if (item.getPrecoCusto() == null) {
            itemEntity.setPrecoCusto(new BigDecimal(0));
        } else {
            itemEntity.setPrecoCusto(item.getPrecoCusto());
        }

        itemEntity.setPrecoVenda(item.getPrecoVenda());

        itemEntity.setEstoque(item.getEstoque());

        itemEntity.setCodigoBarras(item.getCodigoBarras());

        itemEntity.setMargemLucro(itemEntity.calcularMargemLucro());

//        itemEntity.setModelo(item.getModelo());

//        itemEntity.setTamanho(item.getTamanho());

        Fabricante fabricante = fabricanteService.getById(1L);
        itemEntity.setFabricante(fabricante);

        UnidadeMedida unidade = unidadeService.getById(1L);
        itemEntity.setUnidadeVenda(unidade);
        return itemRepository.save(itemEntity);
    }

    @Transactional(readOnly = true)
    public List<Item> listarProdutos() {
        return itemRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Item> listarProdutosVenda(){
        return itemRepository.findByStatusAndEstoqueGreaterThan(Status.ATIVO,0);
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

//        itemAtualizar.setTipoProduto(dto.getTipoProduto());
//
//        itemAtualizar.setModelo(dto.getModelo());
//        itemAtualizar.setGrupoItem(dto.getGrupoItem());
        itemAtualizar.setObservacao(dto.getObservacao());
        itemAtualizar.setCodigoBarras(dto.getCodigoBarras());
        itemAtualizar.setUnidadeVenda(unidadeService.getById(1L));
        itemAtualizar.setFabricante(fabricanteService.getById(1L));
        itemAtualizar.setPrecoCusto(dto.getPrecoCusto());
        itemAtualizar.setPrecoVenda(dto.getPrecoVenda());
        itemAtualizar.setVersao(LocalDateTime.now());

        itemAtualizar.calcularMargemLucro();

        return itemRepository.save(itemAtualizar);
    }

    @Transactional
    public Item entradaEstoqueProduto(Long codigo, int quantidade) {
        Item item = buscarProduto(codigo);

        validarProdutoAtivo(item);
        validarQuantidade(quantidade);

        item.setEstoque(item.getEstoque() + quantidade);

        return salvar(item);
    }

    @Transactional
    public Item saidaEstoqueProduto(Long codigo, int quantidade) {
        Item item = buscarProduto(codigo);

        validarProdutoAtivo(item);
        validarQuantidade(quantidade);

        if (item.getEstoque() < quantidade) {
            throw new RuntimeException("Estoque insuficiente");
        }

        item.setEstoque(item.getEstoque() - quantidade);

        return salvar(item);
    }

    private void validarProdutoAtivo(Item item) {
        if (item.getStatus() != Status.ATIVO) {
            throw new RuntimeException("Produto está inativo");
        }
    }

    private void validarQuantidade(int quantidade) {
        if (quantidade <= 0) {
            throw new RuntimeException("Quantidade deve ser maior que zero");
        }
    }


    public Item buscarProdutoCodigoBarras(String codigoBarras) {
        return itemRepository.findByCodigoBarras(codigoBarras);
    }

    @Transactional(readOnly = true)
    public List<Item> pesquisar(String termo) {
        return itemRepository.pesquisar(termo);
    }

    @Transactional(readOnly = true)
    public String gerarCodigoBarras() {

        String codigo;

        do {
            codigo = ean13Service.gerar();
        } while (itemRepository.existsByCodigoBarras(codigo));

        return codigo;
    }

}
