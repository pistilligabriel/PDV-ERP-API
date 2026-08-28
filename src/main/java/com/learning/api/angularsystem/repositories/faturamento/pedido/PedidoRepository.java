package com.learning.api.angularsystem.repositories.faturamento.pedido;

import com.learning.api.angularsystem.entitys.faturamento.pedido.Pedido;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    @EntityGraph(attributePaths = {
            "integrante",
            "detalhes",
            "detalhes.item",
            "detalhes.item.unidadeVenda",
            "detalhes.item.fabricante"
    })
    @Query("SELECT p FROM Pedido p")
    List<Pedido> findAllWithDetalhes();

    @EntityGraph(attributePaths = {
            "integrante",
            "detalhes",
            "detalhes.item",
            "detalhes.item.unidadeVenda",
            "detalhes.item.fabricante"
    })
    @Query("SELECT p FROM Pedido p WHERE p.codigo = :id")
    Optional<Pedido> findByIdComRelacionamentos(@Param("id") Long id);

    @Query("SELECT p FROM Pedido p LEFT JOIN FETCH p.detalhes WHERE p.dataEmissao BETWEEN :inicio AND :fim")
    List<Pedido> findByDataEmissaoBetweenWithDetalhes(
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim
    );
}
