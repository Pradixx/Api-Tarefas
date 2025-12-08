package com.deigo.apiTarefas.controller;

import com.deigo.apiTarefas.controller.dtoTarefas.AtualizarTarefaDto;
import com.deigo.apiTarefas.controller.dtoTarefas.CriarTarefaDto;
import com.deigo.apiTarefas.infrastructure.entitys.Tarefas;
import com.deigo.apiTarefas.service.TarefasService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tarefas")
@RequiredArgsConstructor
public class TarefasController {

    private final TarefasService tarefasService;

    @PostMapping
    public ResponseEntity<Void> criarTarefa(@RequestBody CriarTarefaDto criarTarefaDto){
        tarefasService.criarTarefa(criarTarefaDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{tarefasId}")
    public ResponseEntity<Tarefas> buscarTarefaPeloId(@PathVariable("tarefasId") String tarefasId) {
        var tarefas = tarefasService.buscarTarefaPeloId(tarefasId);

        if (tarefas.isPresent()) {
            return ResponseEntity.ok(tarefas.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{tarefaId}")
    public ResponseEntity<Tarefas> atualizarTarefa (@PathVariable UUID tarefaId, @RequestBody AtualizarTarefaDto tarefas){
        return ResponseEntity.ok(tarefasService.atualizarTarefaPeloId(tarefaId, tarefas));
    }

    @GetMapping
    public ResponseEntity<List<Tarefas>> listarTarefas() {
        return ResponseEntity.ok(tarefasService.listarTarefas());
    }

    @DeleteMapping("/{tarefaId}")
    public ResponseEntity<Void> deletarPeloId(@PathVariable("tarefaId") String tarefasId){
        tarefasService.deletarPeloId(tarefasId);
        return  ResponseEntity.noContent().build();
    }
}
