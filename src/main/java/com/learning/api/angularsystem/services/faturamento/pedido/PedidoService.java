package com.learning.api.angularsystem.services.faturamento.pedido;

import com.learning.api.angularsystem.entitys.cadastro.integrante.Cliente;
import com.learning.api.angularsystem.entitys.cadastro.item.Item;
import com.learning.api.angularsystem.entitys.faturamento.pedido.Pedido;
import com.learning.api.angularsystem.entitys.faturamento.pedido.PedidoDetalhe;
import com.learning.api.angularsystem.enums.Status;
import com.learning.api.angularsystem.enums.pedido.TipoPedido;
import com.learning.api.angularsystem.repositories.faturamento.pedido.PedidoDetalheRepository;
import com.learning.api.angularsystem.repositories.faturamento.pedido.PedidoRepository;
import com.learning.api.angularsystem.services.cadastro.integrante.IntegranteService;
import com.learning.api.angularsystem.services.cadastro.item.ItemService;
import com.learning.api.angularsystem.services.faturamento.estoque.EstoqueService;
import com.learning.api.angularsystem.web.dtos.cadastro.item.ItemDto;
import com.learning.api.angularsystem.web.dtos.dashboard.DashboardVendasResponseDTO;
import com.learning.api.angularsystem.web.dtos.dashboard.VendaPorPagamentoDTO;
import com.learning.api.angularsystem.web.dtos.faturamento.pedido.ItemVendaDto;
import com.learning.api.angularsystem.web.dtos.faturamento.pedido.PedidoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PedidoService {


    private final PedidoRepository pedidoRepository;

    private final IntegranteService integranteService;

    private final ItemService itemService;

    private final PedidoDetalheRepository detalheRepository;

    private final EstoqueService estoqueService;

    public PedidoService(PedidoRepository pedidoRepository, IntegranteService integranteService, ItemService itemService, PedidoDetalheRepository detalheRepository, EstoqueService estoqueService) {
        this.pedidoRepository = pedidoRepository;
        this.integranteService = integranteService;
        this.itemService = itemService;
        this.detalheRepository = detalheRepository;
        this.estoqueService = estoqueService;
    }

    @Transactional
    public Pedido criarPedido(PedidoDto dto) {

        Cliente cliente = null;

        if(dto.getClienteId() != null){
            cliente = integranteService.getIntegranteById(dto.getClienteId());
        }

        if(cliente == null && (dto.getNomeCliente() == null || dto.getNomeCliente().isBlank())){
            throw new RuntimeException("Informe um cliente cadastrado ou o nome do cliente.");
        }

        Pedido pedido = new Pedido();

        pedido.setIntegrante(cliente);
        pedido.setNomeCliente(
                dto.getNomeCliente() != null
                        ? dto.getNomeCliente().trim()
                        : null
        );
        pedido.setTipoVenda(TipoPedido.VENDA);
        pedido.setFormaPagamento(dto.getFormaPagamento());
        pedido.setParcelas(
                dto.getParcelas() == null ? 1 : dto.getParcelas()
        );
        pedido.setDataEmissao(LocalDateTime.now());


        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        int ordem = 1;

        BigDecimal total = new BigDecimal(0);
        BigDecimal custo = new BigDecimal(0);

        for (ItemVendaDto itemDto : dto.getItens()) {

            Item item = itemService.buscarProduto(itemDto.getProdutoId());

            estoqueService.baixarEstoque(item, itemDto.getQuantidade());

            PedidoDetalhe detalhe = new PedidoDetalhe();

            detalhe.setPedido(pedidoSalvo);

            detalhe.setItem(item);

            detalhe.setDescricao(item.getDescricao());

            detalhe.setQuantidade(itemDto.getQuantidade());

            detalhe.setValorUnitario(item.getPrecoVenda());

            detalhe.setValorTotal(item.getPrecoVenda().multiply(new BigDecimal(itemDto.getQuantidade())));

            detalhe.setOrdem(ordem++);

            detalheRepository.save(detalhe);

            total = total.add(detalhe.getValorTotal());

            custo = custo.add(item.getPrecoCusto()).multiply(new BigDecimal(itemDto.getQuantidade()));
        }

        pedido.setTotal(total);

        pedido.setTotalSemDesconto(total);

        pedido.setDesconto(0.0);

        pedido.setPorcentagemDesconto(0.0);

        pedido.setCusto(custo);

        pedido.setLucro(total.subtract(custo));

        return pedidoRepository.save(pedido);
    }

    public ResponseEntity<Void> atualizarPedido(){
//        PedidoEntity pedido = pedidoRepository.findById(pedidoDto.CODIGO())
//                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));
//
//        pedido.atualizarPedido(pedidoDto);
//
//        if (pedidoDto.integrante() != null) {
//            IntegranteEntity integrante = integranteRepository.findById(pedidoDto.integrante().getCODIGO())
//                    .orElseThrow(() -> new RuntimeException("Integrante não encontrado"));
//
//            pedido.setIntegrante(integrante);
//        } else {
//            pedido.setIntegrante(null);
//        }
//
//        pedidoRepository.save(pedido);
//
//        return ResponseEntity.ok(new PedidoDto(pedido));
        return null;
    }

    @Transactional(readOnly = true)
    public Pedido buscarPedidoPorId(Long id) {
        return pedidoRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Pedido não encontrado")
        );
    }

    @Transactional()
    public PedidoDetalhe salvarDetalhe(Long codigo, PedidoDetalhe detalhe) {
        Pedido pedido = buscarPedidoPorId(codigo);

        Item item = itemService.buscarProduto(detalhe.getItem().getCodigo());

        PedidoDetalhe pedidoDetalhe = new PedidoDetalhe();
        pedidoDetalhe.setPedido(pedido);
        pedidoDetalhe.setStatus(Status.ATIVO);
        pedidoDetalhe.setItem(item);
        pedidoDetalhe.setDescricao(item.getDescricao());
        return detalheRepository.save(pedidoDetalhe);
    }

    @Transactional(readOnly = true)
    public List<Pedido> buscarPedidos() {
        return pedidoRepository.findAllWithDetalhes();
    }

    @Transactional(readOnly = true)
    public Pedido buscarPedidoComRelacionamentos(Long id) {
        Pedido pedido = pedidoRepository.findByIdComRelacionamentos(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));


        // Aqui os detalhes já estão carregados e prontos para uso
        pedido.getDetalhes().forEach(d -> {
            Item itemExistente = itemService.buscarProduto(d.getItem().getCodigo());
            System.out.println(d.getCodigo() + " " + d.getDescricao() + " " + itemExistente.getEstoque());
        });



        return pedido;
    }

    @Transactional
    public Pedido cancelarPedido(Long id) {
        Pedido pedido = buscarPedidoComRelacionamentos(id);

        if (Status.CANCELADO.equals(pedido.getStatus())) {
            throw new RuntimeException("Pedido já cancelado");
        }

        pedido.setStatus(Status.CANCELADO);

        pedido.getDetalhes().forEach(d -> {
            d.setStatus(Status.CANCELADO);

            // Corrigido: buscar pelo código do item, não do detalhe
            Item item = itemService.buscarProduto(d.getItem().getCodigo());

            // Atualizar o estoque
            item.setEstoque(item.getEstoque() + d.getQuantidade());

            // Salvar item com novo estoque
            itemService.salvar(item);
        });

        return pedidoRepository.save(pedido);
    }

    @Transactional(readOnly = true)
    public DashboardVendasResponseDTO obterDadosDashboard(LocalDateTime inicio, LocalDateTime fim) {
        List<Pedido> pedidos = pedidoRepository.findByDataEmissaoBetweenWithDetalhes(inicio, fim);

        // 1. Quantidade de vendas (excluindo os cancelados, se necessário)
        long quantidadeVendas = pedidos.stream()
                .filter(p -> p.getStatus() != Status.CANCELADO)
                .count();

        // 2. Valor vendido (soma do campo 'total' do pedido)
        BigDecimal valorVendido = pedidos.stream()
                .filter(p -> p.getStatus() != Status.CANCELADO)
                .map(Pedido::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 3. Quantidade de produtos (soma da quantidade de cada item dentro dos detalhes)
        long quantidadeProdutos = pedidos.stream()
                .filter(p -> p.getStatus() != Status.CANCELADO)
                .flatMap(p -> p.getDetalhes().stream())
                .filter(d -> d.getStatus() != Status.CANCELADO)
                .mapToLong(PedidoDetalhe::getQuantidade)
                .sum();

        // 4. Ticket médio
        BigDecimal ticketMedio = BigDecimal.ZERO;
        if (quantidadeVendas > 0) {
            ticketMedio = valorVendido.divide(BigDecimal.valueOf(quantidadeVendas), 2, java.math.RoundingMode.HALF_UP);
        }

        // 5. Agrupamento por forma de pagamento
        Map<String, BigDecimal> agrupadoPorPagamento = pedidos.stream()
                .filter(p -> p.getStatus() != Status.CANCELADO)
                .collect(Collectors.groupingBy(
                        p -> p.getFormaPagamento() != null ? p.getFormaPagamento().name() : "Não Informado",
                        Collectors.mapping(Pedido::getTotal, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));

        List<VendaPorPagamentoDTO> vendasPorPagamento = agrupadoPorPagamento.entrySet().stream()
                .map(entry -> new VendaPorPagamentoDTO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        return new DashboardVendasResponseDTO(
                quantidadeVendas,
                valorVendido,
                quantidadeProdutos,
                ticketMedio,
                vendasPorPagamento
        );
    }
    }
