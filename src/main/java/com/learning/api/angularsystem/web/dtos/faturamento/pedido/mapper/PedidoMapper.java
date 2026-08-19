package com.learning.api.angularsystem.web.dtos.faturamento.pedido.mapper;

import com.learning.api.angularsystem.entitys.cadastro.integrante.Cliente;
import com.learning.api.angularsystem.entitys.cadastro.item.Item;
import com.learning.api.angularsystem.entitys.faturamento.pedido.Pedido;
import com.learning.api.angularsystem.entitys.faturamento.pedido.PedidoDetalhe;
import com.learning.api.angularsystem.web.dtos.cadastro.integrante.ClienteDto;
import com.learning.api.angularsystem.web.dtos.cadastro.integrante.ClienteResponseDto;
import com.learning.api.angularsystem.web.dtos.cadastro.item.ItemDto;
import com.learning.api.angularsystem.web.dtos.faturamento.pedido.*;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.stream.Collectors;

public class PedidoMapper {
    public static Pedido toPedido(PedidoDto dto){
        return new ModelMapper().map(dto, Pedido.class);
    }

    public static ResponsePedidoDto toDto(Pedido pedido){
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setAmbiguityIgnored(true);
        return new ModelMapper().map(pedido,ResponsePedidoDto.class);
    }

    public static PedidoDto toPedidoDto(Pedido pedido) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setAmbiguityIgnored(true);

        PedidoDto dto = modelMapper.map(pedido, PedidoDto.class);

        if (pedido.getDetalhes() != null) {
            List<ItemVendaDto> produtos = pedido.getDetalhes().stream()
                    .map(detalhe -> {
                        Item item = detalhe.getItem();
                        ItemVendaDto itemDto = new ItemVendaDto();

                        itemDto.setProdutoId(item.getCodigo());
                        itemDto.setObservacao(item.getObservacao());
                        itemDto.setQuantidade(detalhe.getQuantidade());

                        return itemDto;
                    })
                    .collect(Collectors.toList());

            dto.setItens(produtos);
        }
        return dto;
    }

    public static List<ResponsePedidoDto> toResponseListDto(List<Pedido> pedidos) {
        return pedidos.stream()
                .map(pedido -> new ResponsePedidoDto(
                        pedido.getCodigo(),
                        toPedidoDto(pedido) // <-- usa o método manual, não o do ModelMapper direto
                ))
                .collect(Collectors.toList());
    }

    public static PedidoDetalhe toDetalhe(PedidoDetalheDto dto) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setAmbiguityIgnored(true);
        return modelMapper.map(dto, PedidoDetalhe.class);
    }

    public static DetalheResponseDto toDto(PedidoDetalhe detalhe) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setAmbiguityIgnored(true);
        return modelMapper.map(detalhe, DetalheResponseDto.class);
    }

}
