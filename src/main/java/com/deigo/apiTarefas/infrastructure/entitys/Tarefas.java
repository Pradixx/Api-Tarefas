package com.deigo.apiTarefas.infrastructure.entitys;

import com.deigo.apiTarefas.infrastructure.enumTarefas.Status;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "tarefas")
@Entity

public class Tarefas {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "nome")
    private String titulo;

    @Column(name = "nome")
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(name = "nome")
    private Status status;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuarioId;

}
