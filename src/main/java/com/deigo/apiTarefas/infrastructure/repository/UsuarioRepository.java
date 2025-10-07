package com.deigo.apiTarefas.infrastructure.repository;

import com.deigo.apiTarefas.infrastructure.entitys.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findById(UUID id);

    boolean existsById(UUID id);

    void deleteById(UUID id);

    Optional<Object> findByEmail(String email);

    List<Tarefa> findByUsuarioId(Long usuarioId);
}
