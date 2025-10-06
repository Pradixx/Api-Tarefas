package com.deigo.apiTarefas.service;

import com.deigo.apiTarefas.controller.dto.AtualizarTarefaDto;
import com.deigo.apiTarefas.controller.dto.CriarTarefaDto;
import com.deigo.apiTarefas.infrastructure.entitys.Tarefas;
import com.deigo.apiTarefas.infrastructure.repository.TarefasRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TarefasService {

    private final TarefasRepository tarefasRepository;

    public TarefasService(TarefasRepository tarefasRepository) {
        this.tarefasRepository = tarefasRepository;
    }

    public UUID criarTarefa(CriarTarefaDto criarTarefaDto){

        var entity = new Tarefas(
                UUID.randomUUID(),
                criarTarefaDto.titulo(),
                criarTarefaDto.descricao(),
                criarTarefaDto.status(),
                criarTarefaDto.usuarioId());

        var salvarTarefa = tarefasRepository.save(entity);

        return salvarTarefa.getId();
    }

    public Optional<Tarefas> buscarTarefaPeloId(String tarefasId) {
        return tarefasRepository.findById((UUID.fromString(tarefasId)));
    }

    public List<Tarefas> listarTarefas() {
        return tarefasRepository.findAll();
    }

    public void atualizarTarefaPeloId(String tarefasId, AtualizarTarefaDto atualizarTarefaDto){

        var id = UUID.fromString(tarefasId);

        var tarefaEntity = tarefasRepository.findById(id);

        if (tarefaEntity.isPresent()){
            var tarefas = tarefaEntity.get();

            if (atualizarTarefaDto.titulo() != null){
                tarefas.setTitulo(atualizarTarefaDto.titulo());
            }

            if (atualizarTarefaDto.descricao() != null){
                tarefas.setDescricao(atualizarTarefaDto.descricao());
            }

            tarefasRepository.save(tarefas);
        }
    }

    public void deletarPeloId(String tarefaId) {
        var id = UUID.fromString(tarefaId);

        var tarefaExiste = tarefasRepository.existsById(id);

        if(tarefaExiste) {
            tarefasRepository.deleteById(id);
        }
    }
}
