package com.deigo.apiTarefas.controller;

import com.deigo.apiTarefas.controller.dtoUsuarios.AtualizarUsuariosDto;
import com.deigo.apiTarefas.controller.dtoUsuarios.CriarUsuariosDto;
import com.deigo.apiTarefas.infrastructure.entitys.Tarefas;
import com.deigo.apiTarefas.infrastructure.entitys.Usuario;
import com.deigo.apiTarefas.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor

public class UsuarioController {

    public final UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<Void> criarUsuario(@RequestBody CriarUsuariosDto usuario){
        usuarioService.criarUsuario(usuario);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        return ResponseEntity.ok(usuarioService.listarUsuarios());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> atualizarUsuarios (@PathVariable UUID id, @RequestBody AtualizarUsuariosDto usuario){
        return ResponseEntity.ok(usuarioService.atualizarUsuario(id, usuario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarUsuario (@PathVariable UUID id){
        usuarioService.deletarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/tarefas")
    public ResponseEntity<List<Tarefas>> listarTarefasDoUsuario (@PathVariable UUID id){
        return ResponseEntity.ok(usuarioService.listarTarefasDoUsuario(id));
    }

}
