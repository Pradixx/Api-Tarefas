package com.deigo.apiTarefas.controller;

import com.deigo.apiTarefas.infrastructure.enumTarefas.Status;

public record AtualizarTarefaDto(String titulo, String descricao, Status status) {
}
