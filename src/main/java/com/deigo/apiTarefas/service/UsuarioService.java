package com.deigo.apiTarefas.service;

import com.deigo.apiTarefas.controller.dtoUsuarios.AtualizarUsuariosDto;
import com.deigo.apiTarefas.controller.dtoUsuarios.CriarUsuariosDto;
import com.deigo.apiTarefas.infrastructure.entitys.Tarefas;
import com.deigo.apiTarefas.infrastructure.entitys.Usuario;
import com.deigo.apiTarefas.infrastructure.repository.TarefasRepository;
import com.deigo.apiTarefas.infrastructure.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final TarefasRepository tarefasRepository;

    public UsuarioService(UsuarioRepository usuarioRepository, TarefasRepository tarefasRepository1) {
        this.usuarioRepository = usuarioRepository;
        this.tarefasRepository = tarefasRepository1;
    }

    public void criarUsuario(CriarUsuariosDto dto) {
        if (usuarioRepository.findByEmail(dto.email()).isPresent()) {
            throw new RuntimeException("Email já existe");
        }

        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(dto.nome());
        novoUsuario.setEmail(dto.email());
        novoUsuario.setSenha(dto.senha());

        usuarioRepository.saveAndFlush(novoUsuario);
    }

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    public Usuario atualizarUsuario(UUID id, AtualizarUsuariosDto dto) {
        Usuario usuarioEntity = usuarioRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Usuário não encontrado"));

        if (dto.nome() != null) {
            usuarioEntity.setNome(dto.nome());
        }
        if (dto.email() != null) {
            usuarioEntity.setEmail(dto.email());
        }
        if (dto.senha() != null) {
            usuarioEntity.setSenha(dto.senha());
        }

        return usuarioRepository.saveAndFlush(usuarioEntity);
    }

    public void deletarUsuario(UUID id) {
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
        } else {
            throw new RuntimeException("Usuário não encontrado");
        }
    }

    public List<Tarefas> listarTarefasDoUsuario(UUID id) {
        return tarefasRepository.findByUsuarioId(id);
    }
}
