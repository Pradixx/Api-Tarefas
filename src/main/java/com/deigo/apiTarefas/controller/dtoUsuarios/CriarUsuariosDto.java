package com.deigo.apiTarefas.controller.dtoUsuarios;

import com.deigo.apiTarefas.infrastructure.entitys.Tarefas;

public record CriarUsuariosDto(String nome, String email, String senha) {
}
