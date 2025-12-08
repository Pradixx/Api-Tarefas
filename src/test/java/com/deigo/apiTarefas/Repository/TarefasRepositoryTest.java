package com.deigo.apiTarefas.Repository;


import com.deigo.apiTarefas.infrastructure.entitys.Tarefas;
import com.deigo.apiTarefas.infrastructure.entitys.Usuario;
import com.deigo.apiTarefas.infrastructure.enumTarefas.Status;
import com.deigo.apiTarefas.infrastructure.repository.TarefasRepository;
import com.deigo.apiTarefas.infrastructure.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(properties = {
        "spring.test.database.replace=any",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@DisplayName("Testes do TarefasRepository")
public class TarefasRepositoryTest {

    @Autowired
    private TarefasRepository tarefasRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Tarefas tarefasTest;
    private Usuario usuarioTest;

    @BeforeEach
    void setUp() {
        //Limpa o banco
        tarefasRepository.deleteAll();
        usuarioRepository.deleteAll();
        entityManager.flush();

        //cria um usuario de teste
        usuarioTest = new Usuario();
        usuarioTest.setEmail("usuario@teste.com");
        usuarioTest.setNome("Usuario Teste");
        usuarioTest.setSenha("senha123");

        //cria uma tarefa de teste
        tarefasTest = new Tarefas();
        tarefasTest.setTitulo("Titulo Teste");
        tarefasTest.setDescricao("Testando tarefas");
        tarefasTest.setStatus(Status.PENDENTE);
    }

    @Test
    @DisplayName("Deve encontrar tarefa pelo ID quando existir")
    void deveFindByIdQuandoTarefaExistir() {
        // Arrange
        Usuario usuario = entityManager.persistAndFlush(usuarioTest);
        tarefasTest.setUsuario(usuario);
        Tarefas tarefasSalva = entityManager.persistAndFlush(tarefasTest);
        entityManager.clear();
        UUID id  = tarefasSalva.getId();

        // Act
        Optional<Tarefas> resultado = tarefasRepository.findById(id);

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(id);
        assertThat(resultado.get().getTitulo()).isEqualTo("Titulo Teste");
        assertThat(resultado.get().getStatus()).isEqualTo(Status.PENDENTE);
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando tarefa não exitir por ID")
    void deveFindByIdRetornarVazioQuandoNaoExistir() {
        // Arrange
        UUID idInexistente = UUID.randomUUID();

        // Act
        Optional<Tarefas> resultado = tarefasRepository.findById(idInexistente);

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("Deve retornar true quando tarefa existir por ID")
    void deveExistsByIdRetornarTrueQuandoExistir() {
        // Arrange
        Usuario usuario = entityManager.persistAndFlush(usuarioTest);
        tarefasTest.setUsuario(usuario);
        Tarefas tarefaSalva = entityManager.persistAndFlush(tarefasTest);
        UUID id = tarefaSalva.getId();

        // Act
        boolean existe = tarefasRepository.existsById(id);

        // Assert
        assertThat(existe).isTrue();
    }

    @Test
    @DisplayName("Deve retornar false quando tarefa não existir por ID")
    void deveExistsByIdRetornarFalseQuandoNaoExistir() {
        // Arrange
        UUID idInexistente = UUID.randomUUID();

        // Act
        boolean existe = tarefasRepository.existsById(idInexistente);

        // Assert
        assertThat(existe).isFalse();
    }

    @Test
    @DisplayName("Deve deletar tarefas pelo ID quadno existir")
    void deveDeleteByIdQuandoTarefaExistir() {
        // Arrange
        Usuario usuario = entityManager.persistAndFlush(usuarioTest);
        tarefasTest.setUsuario(usuario);
        Tarefas tarefaSalva = entityManager.persistAndFlush(tarefasTest);
        UUID id = tarefaSalva.getId();

        // Act
        tarefasRepository.deleteById(id);
        entityManager.flush();
        entityManager.clear();

        // Assert
        Optional<Tarefas> resultado = tarefasRepository.findById(id);
        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("Não deve lançar erro ao deletar ID inexistente")
    void naoDeveLancarErroAoDeletarIdInexistente() {
        // Arrange
        UUID idInexistente = UUID.randomUUID();

        // Act e Assert
        tarefasRepository.deleteById(idInexistente);
        entityManager.flush();
    }

    @Test
    @DisplayName("Deve encontrar tarefa por ID do usuário quando existir")
    void deveFindByUsuarioIdQuandoTarefaExistir () {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setEmail("usuario@teste.com");
        usuario.setNome("Usuario Teste");
        usuario.setSenha("senha123");
        entityManager.persistAndFlush(usuario);

        tarefasTest.setUsuario(usuario);
        entityManager.persistAndFlush(tarefasTest);
        entityManager.clear();

        // Act
        List<Tarefas> resultado = tarefasRepository.findByUsuarioId(usuario.getId());

        // Assert
        assertThat(resultado).isNotEmpty();
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getUsuario().getId()).isEqualTo(usuario.getId());
        assertThat(resultado.get(0).getTitulo()).isEqualTo("Titulo Teste");
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando ID não existir")
    void deveFindByUsuarioIDRetornarVazioQuandoNaoExistir() {
        // Arrange
        UUID idInexistente = UUID.randomUUID();

        // Act
        List<Tarefas> resultado = tarefasRepository.findByUsuarioId(idInexistente);

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("Deve encontrar múltiplas tarefas do mesmo usuário")
    void deveFindByUsuarioIdComMultiplasTarefas() {
        // Arrange
        Usuario usuario = entityManager.persistAndFlush(usuarioTest);

        Tarefas tarefa1 = new Tarefas();
        tarefa1.setTitulo("Tarefa 1");
        tarefa1.setDescricao("Descrição 1");
        tarefa1.setStatus(Status.PENDENTE);
        tarefa1.setUsuario(usuario);

        Tarefas tarefa2 = new Tarefas();
        tarefa2.setTitulo("Tarefa 2");
        tarefa2.setDescricao("Descrição 2");
        tarefa2.setStatus(Status.CONCLUIDA);
        tarefa2.setUsuario(usuario);

        Tarefas tarefa3 = new Tarefas();
        tarefa3.setTitulo("Tarefa 3");
        tarefa3.setDescricao("Descrição 3");
        tarefa3.setStatus(Status.EM_ANDAMENTO);
        tarefa3.setUsuario(usuario);

        entityManager.persistAndFlush(tarefa1);
        entityManager.persistAndFlush(tarefa2);
        entityManager.persistAndFlush(tarefa3);
        entityManager.clear();

        // Act
        List<Tarefas> resultado = tarefasRepository.findByUsuarioId(usuario.getId());

        // Assert
        assertThat(resultado).hasSize(3);
        assertThat(resultado).extracting(Tarefas::getTitulo)
                .containsExactlyInAnyOrder("Tarefa 1", "Tarefa 2", "Tarefa 3");
    }

    @Test
    @DisplayName("Deve retornar apenas tarefas do usuário específico")
    void deveFindByUsuarioIdApenasDoUsuarioEspecifico() {
        // Arrange
        Usuario usuario1 = entityManager.persistAndFlush(usuarioTest);

        Usuario usuario2 = new Usuario();
        usuario2.setEmail("usuario2@teste.com");
        usuario2.setNome("Usuario 2");
        usuario2.setSenha("senha456");
        usuario2 = entityManager.persistAndFlush(usuario2);

        Tarefas tarefaUsuario1 = new Tarefas();
        tarefaUsuario1.setTitulo("Tarefa Usuario 1");
        tarefaUsuario1.setDescricao("Tarefa do primeiro usuário");
        tarefaUsuario1.setStatus(Status.PENDENTE);
        tarefaUsuario1.setUsuario(usuario1);

        Tarefas tarefaUsuario2 = new Tarefas();
        tarefaUsuario2.setTitulo("Tarefa Usuario 2");
        tarefaUsuario2.setDescricao("Tarefa do segundo usuário");
        tarefaUsuario2.setStatus(Status.PENDENTE);
        tarefaUsuario2.setUsuario(usuario2);

        entityManager.persistAndFlush(tarefaUsuario1);
        entityManager.persistAndFlush(tarefaUsuario2);
        entityManager.clear();

        // Act
        List<Tarefas> resultadoUsuario1 = tarefasRepository.findByUsuarioId(usuario1.getId());
        List<Tarefas> resultadoUsuario2 = tarefasRepository.findByUsuarioId(usuario2.getId());

        // Assert
        assertThat(resultadoUsuario1).hasSize(1);
        assertThat(resultadoUsuario1.get(0).getTitulo()).isEqualTo("Tarefa Usuario 1");

        assertThat(resultadoUsuario2).hasSize(1);
        assertThat(resultadoUsuario2.get(0).getTitulo()).isEqualTo("Tarefa Usuario 2");
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando usuário não tem tarefas")
    void deveFindByUsuarioIdRetornarVazioQuandoUsuarioSemTarefas() {
        // Arrange
        Usuario usuarioSemTarefas = new Usuario();
        usuarioSemTarefas.setEmail("semtarefas@teste.com");
        usuarioSemTarefas.setNome("Usuario Sem Tarefas");
        usuarioSemTarefas.setSenha("senha789");
        usuarioSemTarefas = entityManager.persistAndFlush(usuarioSemTarefas);
        entityManager.clear();

        // Act
        List<Tarefas> resultado = tarefasRepository.findByUsuarioId(usuarioSemTarefas.getId());

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("Deve salvar tarefa com todos os campos preenchidos")
    void deveSalvarTarefaCompleta() {
        // Arrange
        Usuario usuario = entityManager.persistAndFlush(usuarioTest);

        Tarefas tarefaCompleta = new Tarefas();
        tarefaCompleta.setTitulo("Tarefa Completa");
        tarefaCompleta.setDescricao("Descrição detalhada da tarefa");
        tarefaCompleta.setStatus(Status.EM_ANDAMENTO);
        tarefaCompleta.setUsuario(usuario);

        // Act
        Tarefas tarefaSalva = tarefasRepository.save(tarefaCompleta);
        entityManager.flush();
        entityManager.clear();

        // Assert
        Optional<Tarefas> resultado = tarefasRepository.findById(tarefaSalva.getId());
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getTitulo()).isEqualTo("Tarefa Completa");
        assertThat(resultado.get().getDescricao()).isEqualTo("Descrição detalhada da tarefa");
        assertThat(resultado.get().getStatus()).isEqualTo(Status.EM_ANDAMENTO);
        assertThat(resultado.get().getUsuario().getId()).isEqualTo(usuario.getId());
    }
}
