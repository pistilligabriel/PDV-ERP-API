package com.learning.api.angularsystem.repositories.financeiro;

import com.learning.api.angularsystem.entitys.financeiro.Caixa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CaixaRepository extends JpaRepository<Caixa,Long> {
}
