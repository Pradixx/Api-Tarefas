package com.deigo.apiTarefas.service;

import com.deigo.apiTarefas.controller.dtoTarefas.AtualizarTarefaDto;
import com.deigo.apiTarefas.controller.dtoTarefas.CriarTarefaDto;
import com.deigo.apiTarefas.infrastructure.entitys.Tarefas;
import com.deigo.apiTarefas.infrastructure.entitys.Usuario;
import com.deigo.apiTarefas.infrastructure.repository.TarefasRepository;
import com.deigo.apiTarefas.infrastructure.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TarefasService {

    private final TarefasRepository tarefasRepository;
    private final UsuarioRepository usuarioRepository;

    public TarefasService(TarefasRepository tarefasRepository, UsuarioRepository usuarioRepository) {
        this.tarefasRepository = tarefasRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public Tarefas criarTarefa(CriarTarefaDto dto){

        Usuario usuario = usuarioRepository.findById((dto.usuarioId()))
                .orElseThrow(() -> new RuntimeException("Usuario não encontrado com ID"));

        Tarefas novaTarefa = Tarefas.builder()
                .titulo(dto.titulo())
                .descricao(dto.descricao())
                .status(dto.status())
                .usuario(usuario)
                .build();

         return tarefasRepository.saveAndFlush(novaTarefa);
    }

    public Optional<Tarefas> buscarTarefaPeloId(String tarefasId) {
        return tarefasRepository.findById((UUID.fromString(tarefasId)));
    }

    public List<Tarefas> listarTarefas() {
        return tarefasRepository.findAll();
    }

    public Tarefas atualizarTarefaPeloId(UUID tarefasId, AtualizarTarefaDto dto){
        Tarefas tarefasEntity = tarefasRepository.findById(tarefasId).orElseThrow(() ->
                new RuntimeException("Tarefa não encontrada"));

        if (dto.titulo() != null) {
                tarefasEntity.setTitulo(dto.titulo());
        }
        if (dto.descricao() != null) {
            tarefasEntity.setDescricao(dto.descricao());
        }
        if(dto.status() != null) {
            tarefasEntity.setStatus(dto.status());
        }

        return tarefasRepository.saveAndFlush(tarefasEntity);
    }

    public void deletarPeloId(String tarefaId) {
        var id = UUID.fromString(tarefaId);

        var tarefaExiste = tarefasRepository.existsById(id);

        if(tarefaExiste) {
            tarefasRepository.deleteById(id);
        }
    }
}
