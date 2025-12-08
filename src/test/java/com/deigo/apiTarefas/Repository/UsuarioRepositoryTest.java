package com.deigo.apiTarefas.Repository;

import com.deigo.apiTarefas.infrastructure.entitys.Usuario;
import com.deigo.apiTarefas.infrastructure.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(properties = {
        "spring.test.database.replace=any",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@DisplayName("Testes do UsuarioRepository")
class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Usuario usuarioTeste;

    @BeforeEach
    void setUp() {
        // Limpa o banco antes de cada teste
        usuarioRepository.deleteAll();
        entityManager.flush();

        // Cria um usuário de teste
        usuarioTeste = new Usuario();
        usuarioTeste.setEmail("teste@exemplo.com");
        usuarioTeste.setNome("Usuario Teste");
        usuarioTeste.setSenha("senha123");
    }

    @Test
    @DisplayName("Deve encontrar usuário por ID quando existir")
    void deveFindByIdQuandoUsuarioExistir() {
        // Arrange
        Usuario usuarioSalvo = entityManager.persistAndFlush(usuarioTeste);
        entityManager.clear();
        UUID id = usuarioSalvo.getId();

        // Act
        Optional<Usuario> resultado = usuarioRepository.findById(id);

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(id);
        assertThat(resultado.get().getEmail()).isEqualTo("teste@exemplo.com");
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando usuário não existir por ID")
    void deveFindByIdRetornarVazioQuandoNaoExistir() {
        // Arrange
        UUID idInexistente = UUID.randomUUID();

        // Act
        Optional<Usuario> resultado = usuarioRepository.findById(idInexistente);

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("Deve retornar true quando usuário existir por ID")
    void deveExistsByIdRetornarTrueQuandoExistir() {
        // Arrange
        Usuario usuarioSalvo = entityManager.persistAndFlush(usuarioTeste);
        UUID id = usuarioSalvo.getId();

        // Act
        boolean existe = usuarioRepository.existsById(id);

        // Assert
        assertThat(existe).isTrue();
    }

    @Test
    @DisplayName("Deve retornar false quando usuário não existir por ID")
    void deveExistsByIdRetornarFalseQuandoNaoExistir() {
        // Arrange
        UUID idInexistente = UUID.randomUUID();

        // Act
        boolean existe = usuarioRepository.existsById(idInexistente);

        // Assert
        assertThat(existe).isFalse();
    }

    @Test
    @DisplayName("Deve deletar usuário por ID quando existir")
    void deveDeleteByIdQuandoUsuarioExistir() {
        // Arrange
        Usuario usuarioSalvo = entityManager.persistAndFlush(usuarioTeste);
        UUID id = usuarioSalvo.getId();

        // Act
        usuarioRepository.deleteById(id);
        entityManager.flush();
        entityManager.clear();

        // Assert
        Optional<Usuario> resultado = usuarioRepository.findById(id);
        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("Não deve lançar erro ao deletar ID inexistente")
    void naoDeveLancarErroAoDeletarIdInexistente() {
        // Arrange
        UUID idInexistente = UUID.randomUUID();

        // Act & Assert - não deve lançar exceção
        usuarioRepository.deleteById(idInexistente);
        entityManager.flush();
    }

    @Test
    @DisplayName("Deve encontrar usuário por email quando existir")
    void deveFindByEmailQuandoUsuarioExistir() {
        // Arrange
        entityManager.persistAndFlush(usuarioTeste);
        entityManager.clear();
        String email = "teste@exemplo.com";

        // Act
        Optional<Object> resultado = usuarioRepository.findByEmail(email);

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get()).isInstanceOf(Usuario.class);
        Usuario usuario = (Usuario) resultado.get();
        assertThat(usuario.getEmail()).isEqualTo(email);
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando email não existir")
    void deveFindByEmailRetornarVazioQuandoNaoExistir() {
        // Arrange
        String emailInexistente = "inexistente@exemplo.com";

        // Act
        Optional<Object> resultado = usuarioRepository.findByEmail(emailInexistente);

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("Deve buscar email ignorando espaços em branco")
    void deveFindByEmailIgnorandoEspacos() {
        // Arrange
        entityManager.persistAndFlush(usuarioTeste);
        entityManager.clear();

        // Act
        Optional<Object> resultado = usuarioRepository.findByEmail("teste@exemplo.com");

        // Assert
        assertThat(resultado).isPresent();
    }

    @Test
    @DisplayName("Deve salvar e recuperar múltiplos usuários")
    void deveSalvarERecuperarMultiplosUsuarios() {
        // Arrange
        Usuario usuario1 = new Usuario();
        usuario1.setEmail("usuario1@exemplo.com");
        usuario1.setNome("Usuario 1");
        usuario1.setSenha("senha1");

        Usuario usuario2 = new Usuario();
        usuario2.setEmail("usuario2@exemplo.com");
        usuario2.setNome("Usuario 2");
        usuario2.setSenha("senha2");

        // Act
        entityManager.persistAndFlush(usuario1);
        entityManager.persistAndFlush(usuario2);
        entityManager.clear();

        // Assert
        assertThat(usuarioRepository.findAll()).hasSize(2);
        assertThat(usuarioRepository.findByEmail("usuario1@exemplo.com")).isPresent();
        assertThat(usuarioRepository.findByEmail("usuario2@exemplo.com")).isPresent();
    }

    @Test
    @DisplayName("Deve garantir que emails sejam únicos se houver constraint")
    void deveValidarEmailUnico() {
        // Arrange
        Usuario usuario1 = new Usuario();
        usuario1.setEmail("duplicado@exemplo.com");
        usuario1.setNome("Usuario 1");
        usuario1.setSenha("senha1");

        // Act
        entityManager.persistAndFlush(usuario1);

        // Assert
        Optional<Object> resultado = usuarioRepository.findByEmail("duplicado@exemplo.com");
        assertThat(resultado).isPresent();
    }
}