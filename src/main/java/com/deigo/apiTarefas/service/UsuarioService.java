package com.deigo.apiTarefas.service;

import com.deigo.apiTarefas.infrastructure.entitys.Usuario;
import com.deigo.apiTarefas.infrastructure.repository.TarefasRepository;
import com.deigo.apiTarefas.infrastructure.repository.UsuarioRepository;

import java.util.List;
import java.util.UUID;

public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final TarefasRepository tarefasRepository;

    public UsuarioService(UsuarioRepository usuarioRepository, TarefasRepository tarefasRepository, TarefasRepository tarefasRepository1) {
        this.usuarioRepository = usuarioRepository;
        this.tarefasRepository = tarefasRepository1;
    }

    public Usuario salvar(Usuario usuario) {
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new RuntimeException("Email já existe");
        }
        return usuarioRepository.save(usuario);
    }

    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    public Usuario atualizar(UUID id, Usuario usuarioAtualizado) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        // Atualize campos, re-hash senha se alterada
        return usuarioRepository.save(usuario);
    }

    public void deletar(UUID id) {
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
        } else {
            throw new RuntimeException("Usuário não encontrado");
        }
    }

    public List<String> listarTarefasDoUsuario(UUID usuario) {
        return tarefasRepository.findByUsuarioId(usuario);
    }
}
