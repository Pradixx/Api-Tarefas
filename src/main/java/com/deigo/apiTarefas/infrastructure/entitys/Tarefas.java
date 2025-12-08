package com.deigo.apiTarefas.infrastructure.entitys;

import com.deigo.apiTarefas.infrastructure.enumTarefas.Status;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "tarefas")
@Entity

public class Tarefas {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "titulo")
    private String titulo;

    @Column(name = "descricao")
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    @ManyToOne
    @JoinColumn(name = "usuarioId")
    @JsonIgnoreProperties({"tarefas"})
    private Usuario usuario;
}
