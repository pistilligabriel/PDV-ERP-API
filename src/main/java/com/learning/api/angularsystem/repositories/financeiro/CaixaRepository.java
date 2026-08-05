package com.learning.api.angularsystem.repositories.financeiro;

import com.learning.api.angularsystem.entitys.financeiro.Caixa;
import com.learning.api.angularsystem.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CaixaRepository extends JpaRepository<Caixa,Long> {
    boolean existsByStatus(Status status);
}
