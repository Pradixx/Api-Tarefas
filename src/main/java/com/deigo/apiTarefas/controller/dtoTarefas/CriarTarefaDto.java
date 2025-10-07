package com.deigo.apiTarefas.controller.dtoTarefas;

import com.deigo.apiTarefas.infrastructure.entitys.Usuario;
import com.deigo.apiTarefas.infrastructure.enumTarefas.Status;

public record CriarTarefaDto(String titulo, String descricao, Status status, Usuario usuarioId) {
}
