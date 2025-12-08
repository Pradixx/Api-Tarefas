package com.deigo.apiTarefas.controller.dtoTarefas;

import com.deigo.apiTarefas.infrastructure.enumTarefas.Status;

import java.util.UUID;

public record CriarTarefaDto(String titulo, String descricao, Status status, UUID usuarioId) {
}
