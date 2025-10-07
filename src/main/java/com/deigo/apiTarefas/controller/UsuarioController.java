package com.deigo.apiTarefas.controller;

import com.deigo.apiTarefas.infrastructure.entitys.Usuario;
import com.deigo.apiTarefas.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/usuarios")

public class UsuarioController {
    public final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<Usuario> criarUsuario(@RequestBody @Validated Usuario usuario){
        return ResponseEntity.ok(usuarioService.salvar(usuario));
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        return ResponseEntity.ok(usuarioService.listar());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> atualizarUsuarios (@PathVariable UUID id, @RequestBody Usuario usuario){
        return ResponseEntity.ok(usuarioService.atualizar(id, usuario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarUsuario (@PathVariable UUID id){
        usuarioService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/tarefas")
    public ResponseEntity<List<String>> listarTarefasDoUsuario (@PathVariable UUID id){
        return ResponseEntity.ok(usuarioService.listarTarefasDoUsuario(id));
    }

}
