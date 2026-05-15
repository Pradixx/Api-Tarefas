package com.deigo.apiTarefas.dto;

import com.deigo.apiTarefas.model.Status;

import java.util.UUID;

public record CriarTarefaDto(String titulo, String descricao, Status status, UUID usuarioId) {
}
