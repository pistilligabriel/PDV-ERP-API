package com.learning.api.angularsystem.services.empresa;

import java.io.IOException;

import com.learning.api.angularsystem.enums.TipoEmpresa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.learning.api.angularsystem.entitys.empresa.Empresa;
import com.learning.api.angularsystem.repositories.empresa.EmpresaRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class EmpresaService {
    @Autowired
    private EmpresaRepository empresaRepository;

//TODO Futuramente
    // public Empresa novaConfig(Empresa empresa){
    //     Empresa empresaExistente = obterConfiguracao();
    //     if(empresaExistente == null){
    //     return empresaRepository.save(empresa);
    //     }
    //     return empresaExistente;
    // }

    public Empresa obterConfiguracao() {
        // Para simplicidade, vamos retornar a primeira configuração
        // No futuro, pode-se adicionar lógica para obter uma única configuração
        return empresaRepository.findById(1L).orElse(null);
    }

    public String obterNomeEmpresa(Long id){
        Empresa empresa = empresaRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Empresa não encontrada")
        );
        return empresa.getNomeEmpresa();
    }

    public byte[] getLogoById(Long id) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Empresa não encontrada"));
        return empresa.getLogo(); // Supondo que o campo logo seja byte[]
    }

    public Empresa editarConfiguracao(String nomeEmpresa, MultipartFile file, TipoEmpresa tipoEmpresa) {
        Empresa empresa = obterConfiguracao();

        if (nomeEmpresa != null && !nomeEmpresa.isEmpty()) {
            empresa.setNomeEmpresa(nomeEmpresa);
        }

        if (file != null && !file.isEmpty()) {
            try {
                byte[] fileBytes = file.getBytes();
                empresa.setLogo(fileBytes);
            } catch (IOException e) {
                throw new RuntimeException("Erro ao processar o arquivo", e);
            }
        }

        if (tipoEmpresa != null) {
            empresa.setTipoEmpresa(tipoEmpresa);
        }

        return empresaRepository.save(empresa);
    }
}