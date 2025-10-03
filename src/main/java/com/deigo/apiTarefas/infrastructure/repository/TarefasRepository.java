package com.deigo.apiTarefas.infrastructure.repository;

import com.deigo.apiTarefas.infrastructure.entitys.Tarefas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TarefasRepository extends JpaRepository<Tarefas, Integer> {
    
    Optional<Tarefas> findById(UUID id);

    boolean existsById(UUID id);

    void deleteById(UUID id);
}
