package com.learning.api.angularsystem.services.cadastro.usuario;

import com.learning.api.angularsystem.entitys.cadastro.usuario.Usuario;
import com.learning.api.angularsystem.enums.Status;
import com.learning.api.angularsystem.enums.usuario.Tipo;
import com.learning.api.angularsystem.repositories.cadastro.usuario.UsuarioRepository;
import com.learning.api.angularsystem.web.dtos.cadastro.usuario.AlterarSenhaDto;
import com.learning.api.angularsystem.web.dtos.cadastro.usuario.UsuarioAtualizarDto;
import com.learning.api.angularsystem.web.dtos.cadastro.usuario.UsuarioDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.security.auth.login.CredentialException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;


    @Transactional
    public Usuario createUsuario(UsuarioDto dto){
        Usuario usuario = new Usuario();

        usuario.setNomeCompleto(dto.getNomeCompleto());
        usuario.setEmail(dto.getEmail());
        usuario.setDocumento(dto.getDocumento());
        usuario.setTelefone(dto.getTelefone());
        usuario.setLogin(dto.getLogin());
        usuario.setPassword(new BCryptPasswordEncoder().encode(dto.getPassword()));
        return repository.save(usuario);
    }

    @Transactional(readOnly = true)
    public List<Usuario> getAllUsuarios(){
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Usuario getById(Long codigo){
        return repository.findById(codigo).orElseThrow(
                () -> new RuntimeException("Usuario não encontrada")
        );
    }

    @Transactional()
    public Usuario salvarToken(Long id,String token){
            Usuario usuario = getById(id);
            usuario.setToken(token);
            return repository.save(usuario);
    }

    @Transactional()
    public Usuario alterarStatus(Long codigo){
        Usuario usuario = getById(codigo);
        if(usuario.getStatus().equals(Status.ATIVO)){
            usuario.setStatusInativo();
        } else {
            usuario.setStatusAtivo();
        }
        return usuario;
    }

    @Transactional()
    public Usuario deletarUsuario(Long codigo){
        Usuario usuario = getById(codigo);
        if(usuario.getStatus().equals(Status.DESATIVADO)){
            repository.deleteById(usuario.getCodigo());
        } else{
            throw new RuntimeException("Usuario não pode ser deletado!");
        }
        return usuario;
    }

    @Transactional
    public Usuario atualizarUsuario(UsuarioAtualizarDto dto){
        Usuario usuarioAtualizar = getById(dto.getCodigo());

        if(!usuarioAtualizar.getStatus().equals(Status.ATIVO)){
            throw new RuntimeException("Usuário não pode ser alterado, pois está desativado!");
        }

        usuarioAtualizar.setNomeCompleto(dto.getNomeCompleto());
        usuarioAtualizar.setTelefone(dto.getTelefone());
        usuarioAtualizar.setDocumento(dto.getDocumento());
        usuarioAtualizar.setEmail(dto.getEmail());
        usuarioAtualizar.setLogin(dto.getLogin());
        if(dto.getPassword() != null) {
            usuarioAtualizar.setPassword(new BCryptPasswordEncoder().encode(dto.getPassword()));
        }
        usuarioAtualizar.setVersao(LocalDateTime.now());
        return repository.save(usuarioAtualizar);
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorLogin(String login){
        return repository.findByLogin(login).orElseThrow(
                ()-> new RuntimeException("Usuário não encontrado.")
                );
    }

    @Transactional
    public Usuario alterarTipo(Long codigo, Tipo tipo){
        Usuario usuario = getById(codigo);
        if(usuario.getStatus().equals(Status.DESATIVADO)){
            throw new RuntimeException("Não é possível alterar o tipo de usuário desativado");
        }else{
            usuario.setTipo(tipo);
        }
        return repository.save(usuario);
    }

    @Transactional
    public Usuario logout(Long id){
        Usuario usuario = getById(id);
        usuario.setToken(null);

        return repository.save(usuario);
    }

    @Transactional
    public void alterarSenha(AlterarSenhaDto request) throws RuntimeException {
        Usuario usuario = buscarPorLogin(request.getLogin());

        boolean validarSenha = new BCryptPasswordEncoder().matches(request.getPassword(), usuario.getPassword());

        if(usuario.getStatus().equals(Status.ATIVO) && validarSenha){
            if(request.getNewPassword().equals(request.getConfirmPassword())){
                usuario.setPassword(new BCryptPasswordEncoder().encode(request.getNewPassword()));
                repository.save(usuario);
            }else{
                throw new RuntimeException("As senhas não conferem!");
            }
        }else{
            throw new RuntimeException("Senha do usuário incorreta!");
        }
    }

}
