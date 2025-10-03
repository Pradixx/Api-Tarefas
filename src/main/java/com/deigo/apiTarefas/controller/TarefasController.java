package com.deigo.apiTarefas.controller;

import com.deigo.apiTarefas.infrastructure.entitys.Tarefas;
import com.deigo.apiTarefas.service.TarefasService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/tarefas")

public class TarefasController {

    private final TarefasService tarefasService;

    public TarefasController(TarefasService tarefasService) {
        this.tarefasService = tarefasService;
    }

    @PostMapping
    public ResponseEntity<Tarefas> cirarTarefa(@RequestBody CriarTarefaDto criarTarefaDto){
        var tarefaId = tarefasService.criarTarefa(criarTarefaDto);

        return ResponseEntity.created(URI.create("/tarefas" + tarefaId.toString())).build();
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

    @GetMapping
    public ResponseEntity<List<Tarefas>> listarTarefas() {
        var tarefas = tarefasService.listarTarefas();

        return ResponseEntity.ok(tarefas);
    }

    @PutMapping("/{users}")
    public ResponseEntity<Void> deletarPeloId(@PathVariable("tarefas") String tarefasId){
        tarefasService.deletarPeloId(tarefasId);
        return  ResponseEntity.noContent().build();
    }
}
