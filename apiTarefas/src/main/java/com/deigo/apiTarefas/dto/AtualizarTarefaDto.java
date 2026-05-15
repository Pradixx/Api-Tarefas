package com.deigo.apiTarefas.dto;

import com.deigo.apiTarefas.model.Status;

public record AtualizarTarefaDto(String titulo, String descricao, Status status) {
}
