package com.deigo.apiTarefas.Service;

import com.deigo.apiTarefas.controller.dtoUsuarios.AtualizarUsuariosDto;
import com.deigo.apiTarefas.controller.dtoUsuarios.CriarUsuariosDto;
import com.deigo.apiTarefas.infrastructure.entitys.Tarefas;
import com.deigo.apiTarefas.infrastructure.entitys.Usuario;
import com.deigo.apiTarefas.infrastructure.repository.TarefasRepository;
import com.deigo.apiTarefas.infrastructure.repository.UsuarioRepository;
import com.deigo.apiTarefas.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do UsuarioService")
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private TarefasRepository tarefasRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    private CriarUsuariosDto criarUsuarioDto;
    private Usuario usuario;
    private UUID usuarioId;

    @BeforeEach
    void setUp() {
        usuarioId = UUID.randomUUID();

        criarUsuarioDto = new CriarUsuariosDto(
                "Usuario Teste",
                "teste@exemplo.com",
                "senha123"
        );

        usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setNome("Usuario Teste");
        usuario.setEmail("teste@exemplo.com");
        usuario.setSenha("senha123");
    }

    @Test
    @DisplayName("Deve criar usuário com sucesso quando email não existe")
    void deveCriarUsuarioComSucesso() {
        // Arrange
        when(usuarioRepository.findByEmail(criarUsuarioDto.email()))
                .thenReturn(Optional.empty());
        when(usuarioRepository.saveAndFlush(any(Usuario.class)))
                .thenReturn(usuario);

        // Act
        usuarioService.criarUsuario(criarUsuarioDto);

        // Assert
        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository, times(1)).findByEmail(criarUsuarioDto.email());
        verify(usuarioRepository, times(1)).saveAndFlush(usuarioCaptor.capture());

        Usuario usuarioSalvo = usuarioCaptor.getValue();
        assertThat(usuarioSalvo.getNome()).isEqualTo("Usuario Teste");
        assertThat(usuarioSalvo.getEmail()).isEqualTo("teste@exemplo.com");
        assertThat(usuarioSalvo.getSenha()).isEqualTo("senha123");
    }

    @Test
    @DisplayName("Deve lançar exceção quando email já existe")
    void deveLancarExcecaoQuandoEmailJaExiste() {
        // Arrange
        when(usuarioRepository.findByEmail(criarUsuarioDto.email()))
                .thenReturn(Optional.of(usuario));

        // Act & Assert
        assertThatThrownBy(() -> usuarioService.criarUsuario(criarUsuarioDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Email já existe");

        verify(usuarioRepository, times(1)).findByEmail(criarUsuarioDto.email());
        verify(usuarioRepository, never()).saveAndFlush(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve listar todos os usuários")
    void deveListarTodosUsuarios() {
        // Arrange
        Usuario usuario2 = new Usuario();
        usuario2.setId(UUID.randomUUID());
        usuario2.setNome("Usuario 2");
        usuario2.setEmail("usuario2@exemplo.com");

        List<Usuario> usuarios = Arrays.asList(usuario, usuario2);
        when(usuarioRepository.findAll()).thenReturn(usuarios);

        // Act
        List<Usuario> resultado = usuarioService.listarUsuarios();

        // Assert
        assertThat(resultado).hasSize(2);
        assertThat(resultado).containsExactly(usuario, usuario2);
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há usuários")
    void deveRetornarListaVaziaQuandoNaoHaUsuarios() {
        // Arrange
        when(usuarioRepository.findAll()).thenReturn(List.of());

        // Act
        List<Usuario> resultado = usuarioService.listarUsuarios();

        // Assert
        assertThat(resultado).isEmpty();
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve atualizar todos os campos do usuário")
    void deveAtualizarTodosCamposDoUsuario() {
        // Arrange
        AtualizarUsuariosDto dto = new AtualizarUsuariosDto(
                "Nome Atualizado",
                "novoemail@exemplo.com",
                "novaSenha123"
        );

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.saveAndFlush(any(Usuario.class))).thenReturn(usuario);

        // Act
        Usuario resultado = usuarioService.atualizarUsuario(usuarioId, dto);

        // Assert
        verify(usuarioRepository, times(1)).findById(usuarioId);
        verify(usuarioRepository, times(1)).saveAndFlush(usuario);
        assertThat(usuario.getNome()).isEqualTo("Nome Atualizado");
        assertThat(usuario.getEmail()).isEqualTo("novoemail@exemplo.com");
        assertThat(usuario.getSenha()).isEqualTo("novaSenha123");
    }

    @Test
    @DisplayName("Deve atualizar apenas o nome quando outros campos são nulos")
    void deveAtualizarApenasNomeQuandoOutrosCamposNulos() {
        // Arrange
        AtualizarUsuariosDto dto = new AtualizarUsuariosDto(
                "Apenas Nome Novo",
                null,
                null
        );

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.saveAndFlush(any(Usuario.class))).thenReturn(usuario);

        // Act
        Usuario resultado = usuarioService.atualizarUsuario(usuarioId, dto);

        // Assert
        assertThat(usuario.getNome()).isEqualTo("Apenas Nome Novo");
        assertThat(usuario.getEmail()).isEqualTo("teste@exemplo.com"); // Não mudou
        assertThat(usuario.getSenha()).isEqualTo("senha123"); // Não mudou
    }

    @Test
    @DisplayName("Deve atualizar apenas email e senha")
    void deveAtualizarApenasEmailESenha() {
        // Arrange
        AtualizarUsuariosDto dto = new AtualizarUsuariosDto(
                null,
                "email@atualizado.com",
                "senhaAtualizada"
        );

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.saveAndFlush(any(Usuario.class))).thenReturn(usuario);

        // Act
        Usuario resultado = usuarioService.atualizarUsuario(usuarioId, dto);

        // Assert
        assertThat(usuario.getNome()).isEqualTo("Usuario Teste"); // Não mudou
        assertThat(usuario.getEmail()).isEqualTo("email@atualizado.com");
        assertThat(usuario.getSenha()).isEqualTo("senhaAtualizada");
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar usuário inexistente")
    void deveLancarExcecaoAoAtualizarUsuarioInexistente() {
        // Arrange
        UUID idInexistente = UUID.randomUUID();
        AtualizarUsuariosDto dto = new AtualizarUsuariosDto(
                "Nome",
                "email@teste.com",
                "senha"
        );

        when(usuarioRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> usuarioService.atualizarUsuario(idInexistente, dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Usuário não encontrado");

        verify(usuarioRepository, times(1)).findById(idInexistente);
        verify(usuarioRepository, never()).saveAndFlush(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve deletar usuário quando existir")
    void deveDeletarUsuarioQuandoExistir() {
        // Arrange
        when(usuarioRepository.existsById(usuarioId)).thenReturn(true);
        doNothing().when(usuarioRepository).deleteById(usuarioId);

        // Act
        usuarioService.deletarUsuario(usuarioId);

        // Assert
        verify(usuarioRepository, times(1)).existsById(usuarioId);
        verify(usuarioRepository, times(1)).deleteById(usuarioId);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar usuário inexistente")
    void deveLancarExcecaoAoDeletarUsuarioInexistente() {
        // Arrange
        UUID idInexistente = UUID.randomUUID();
        when(usuarioRepository.existsById(idInexistente)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> usuarioService.deletarUsuario(idInexistente))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Usuário não encontrado");

        verify(usuarioRepository, times(1)).existsById(idInexistente);
        verify(usuarioRepository, never()).deleteById(any(UUID.class));
    }

    @Test
    @DisplayName("Deve listar tarefas do usuário")
    void deveListarTarefasDoUsuario() {
        // Arrange
        Tarefas tarefa1 = new Tarefas();
        tarefa1.setTitulo("Tarefa 1");

        Tarefas tarefa2 = new Tarefas();
        tarefa2.setTitulo("Tarefa 2");

        List<Tarefas> tarefas = Arrays.asList(tarefa1, tarefa2);
        when(tarefasRepository.findByUsuarioId(usuarioId)).thenReturn(tarefas);

        // Act
        List<Tarefas> resultado = usuarioService.listarTarefasDoUsuario(usuarioId);

        // Assert
        assertThat(resultado).hasSize(2);
        assertThat(resultado).containsExactly(tarefa1, tarefa2);
        verify(tarefasRepository, times(1)).findByUsuarioId(usuarioId);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando usuário não tem tarefas")
    void deveRetornarListaVaziaQuandoUsuarioNaoTemTarefas() {
        // Arrange
        when(tarefasRepository.findByUsuarioId(usuarioId)).thenReturn(List.of());

        // Act
        List<Tarefas> resultado = usuarioService.listarTarefasDoUsuario(usuarioId);

        // Assert
        assertThat(resultado).isEmpty();
        verify(tarefasRepository, times(1)).findByUsuarioId(usuarioId);
    }
}
