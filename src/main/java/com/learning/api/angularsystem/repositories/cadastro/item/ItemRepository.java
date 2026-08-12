package com.learning.api.angularsystem.repositories.cadastro.item;

import com.learning.api.angularsystem.entitys.cadastro.item.Item;
import com.learning.api.angularsystem.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Item findByCodigoBarras(String codigoBarras);
    @Query("""
    SELECT i
    FROM Item i
    WHERE i.status = com.learning.api.angularsystem.enums.Status.ATIVO
    AND (
        LOWER(i.descricao) LIKE LOWER(CONCAT('%', :termo, '%'))
        OR CAST(i.codigo AS string) LIKE CONCAT('%', :termo, '%')
        OR i.codigoBarras LIKE CONCAT('%', :termo, '%')
    )
    ORDER BY i.descricao
    """)
    List<Item> pesquisar(@Param("termo") String termo);

    List<Item> findByStatusAndEstoqueGreaterThan(Status status, Integer estoque);

    boolean existsByCodigoBarras(String codigoBarras);
}
