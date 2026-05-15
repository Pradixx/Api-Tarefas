package com.deigo.apiTarefas.repository;

import com.deigo.apiTarefas.model.Tarefas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TarefasRepository extends JpaRepository<Tarefas, UUID> {
    
    Optional<Tarefas> findById(UUID id);

    boolean existsById(UUID id);

    void deleteById(UUID id);

    List<Tarefas> findByUsuarioId(UUID usuarioId);
}
