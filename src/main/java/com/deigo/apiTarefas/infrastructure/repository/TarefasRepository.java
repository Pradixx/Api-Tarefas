package com.deigo.apiTarefas.infrastructure.repository;

import com.deigo.apiTarefas.infrastructure.entitys.Tarefas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface TarefasRepository extends JpaRepository<Tarefas, Integer> {

    @Override
    Optional<Tarefas> findById(Integer integer);

    @Override
    <S extends Tarefas> S save(S entity);

    @Transactional
    void deleteById(Long id);

}
