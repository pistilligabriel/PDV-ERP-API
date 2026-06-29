package com.learning.api.angularsystem.services.financeiro;

import com.learning.api.angularsystem.entitys.financeiro.Caixa;
import com.learning.api.angularsystem.enums.Status;
import com.learning.api.angularsystem.repositories.financeiro.CaixaRepository;
import com.learning.api.angularsystem.web.dtos.financeiro.CaixaDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CaixaService {
    private final CaixaRepository _repository;

    public CaixaService(CaixaRepository repository) {
        _repository = repository;
    }

    @Transactional
    public Caixa abrirCaixa(Caixa caixa) {
        return _repository.save(caixa);
    }

    @Transactional(readOnly = true)
    public Caixa getCaixaById(Long codigo) {
        return _repository.findById(codigo).orElseThrow(
                () -> new RuntimeException("Caixa não encontrado!")
        );
    }

    @Transactional(readOnly = true)
    public List<Caixa> getAllCaixas() {
        return _repository.findAll();
    }

    @Transactional
    public Caixa fecharCaixa(Long codigo) {
        Caixa caixa = getCaixaById(codigo);

        if (caixa.getStatus() == Status.FECHADO) {
            throw new RuntimeException("Caixa já está fechado");
        } else {
            caixa.setStatus(Status.FECHADO);
            return _repository.save(caixa);
        }
    }
}
