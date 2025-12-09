package com.deigo.apiTarefas.Service;

import com.deigo.apiTarefas.controller.dtoTarefas.AtualizarTarefaDto;
import com.deigo.apiTarefas.controller.dtoTarefas.CriarTarefaDto;
import com.deigo.apiTarefas.infrastructure.enumTarefas.Status;
import com.deigo.apiTarefas.infrastructure.entitys.Tarefas;
import com.deigo.apiTarefas.infrastructure.entitys.Usuario;
import com.deigo.apiTarefas.infrastructure.repository.TarefasRepository;
import com.deigo.apiTarefas.infrastructure.repository.UsuarioRepository;
import com.deigo.apiTarefas.service.TarefasService;
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
@DisplayName("Testes do TarefasService")
class TarefasServiceTest {

    @Mock
    private TarefasRepository tarefasRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private TarefasService tarefasService;

    private Usuario usuario;
    private Tarefas tarefa;
    private CriarTarefaDto criarTarefaDto;
    private UUID usuarioId;
    private UUID tarefaId;

    @BeforeEach
    void setUp() {
        usuarioId = UUID.randomUUID();
        tarefaId = UUID.randomUUID();

        usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setNome("Usuario Teste");
        usuario.setEmail("teste@exemplo.com");
        usuario.setSenha("senha123");

        tarefa = Tarefas.builder()
                .id(tarefaId)
                .titulo("Tarefa Teste")
                .descricao("Descrição Teste")
                .status(Status.PENDENTE)
                .usuario(usuario)
                .build();

        criarTarefaDto = new CriarTarefaDto(
                "Nova Tarefa",
                "Descrição da nova tarefa",
                Status.PENDENTE,
                usuarioId
        );
    }

    @Test
    @DisplayName("Deve criar tarefa com sucesso quando usuário existe")
    void deveCriarTarefaComSucesso() {
        // Arrange
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(tarefasRepository.saveAndFlush(any(Tarefas.class))).thenReturn(tarefa);

        // Act
        Tarefas resultado = tarefasService.criarTarefa(criarTarefaDto);

        // Assert
        ArgumentCaptor<Tarefas> tarefaCaptor = ArgumentCaptor.forClass(Tarefas.class);
        verify(usuarioRepository, times(1)).findById(usuarioId);
        verify(tarefasRepository, times(1)).saveAndFlush(tarefaCaptor.capture());

        Tarefas tarefaSalva = tarefaCaptor.getValue();
        assertThat(tarefaSalva.getTitulo()).isEqualTo("Nova Tarefa");
        assertThat(tarefaSalva.getDescricao()).isEqualTo("Descrição da nova tarefa");
        assertThat(tarefaSalva.getStatus()).isEqualTo(Status.PENDENTE);
        assertThat(tarefaSalva.getUsuario()).isEqualTo(usuario);
        assertThat(resultado).isNotNull();
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não existe ao criar tarefa")
    void deveLancarExcecaoQuandoUsuarioNaoExisteAoCriarTarefa() {
        // Arrange
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> tarefasService.criarTarefa(criarTarefaDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Usuario não encontrado com ID");

        verify(usuarioRepository, times(1)).findById(usuarioId);
        verify(tarefasRepository, never()).saveAndFlush(any(Tarefas.class));
    }

    @Test
    @DisplayName("Deve criar tarefa com diferentes status")
    void deveCriarTarefaComDiferentesStatus() {
        // Arrange - Tarefa EM_ANDAMENTO
        CriarTarefaDto dtoEmAndamento = new CriarTarefaDto(
                "Tarefa em andamento",
                "Descrição",
                Status.EM_ANDAMENTO,
                usuarioId
        );

        Tarefas tarefaEmAndamento = Tarefas.builder()
                .titulo("Tarefa em andamento")
                .descricao("Descrição")
                .status(Status.EM_ANDAMENTO)
                .usuario(usuario)
                .build();

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(tarefasRepository.saveAndFlush(any(Tarefas.class))).thenReturn(tarefaEmAndamento);

        // Act
        Tarefas resultado = tarefasService.criarTarefa(dtoEmAndamento);

        // Assert
        ArgumentCaptor<Tarefas> captor = ArgumentCaptor.forClass(Tarefas.class);
        verify(tarefasRepository).saveAndFlush(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(Status.EM_ANDAMENTO);
    }

    @Test
    @DisplayName("Deve buscar tarefa por ID quando existe")
    void deveBuscarTarefaPorIdQuandoExiste() {
        // Arrange
        String tarefaIdString = tarefaId.toString();
        when(tarefasRepository.findById(tarefaId)).thenReturn(Optional.of(tarefa));

        // Act
        Optional<Tarefas> resultado = tarefasService.buscarTarefaPeloId(tarefaIdString);

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(tarefaId);
        assertThat(resultado.get().getTitulo()).isEqualTo("Tarefa Teste");
        verify(tarefasRepository, times(1)).findById(tarefaId);
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando tarefa não existe")
    void deveRetornarOptionalVazioQuandoTarefaNaoExiste() {
        // Arrange
        String tarefaIdString = tarefaId.toString();
        when(tarefasRepository.findById(tarefaId)).thenReturn(Optional.empty());

        // Act
        Optional<Tarefas> resultado = tarefasService.buscarTarefaPeloId(tarefaIdString);

        // Assert
        assertThat(resultado).isEmpty();
        verify(tarefasRepository, times(1)).findById(tarefaId);
    }

    @Test
    @DisplayName("Deve converter String para UUID corretamente ao buscar")
    void deveConverterStringParaUUIDCorretamenteAoBuscar() {
        // Arrange
        String uuidValido = "550e8400-e29b-41d4-a716-446655440000";
        UUID uuidEsperado = UUID.fromString(uuidValido);

        Tarefas tarefaEsperada = Tarefas.builder()
                .id(uuidEsperado)
                .titulo("Tarefa UUID")
                .build();

        when(tarefasRepository.findById(uuidEsperado)).thenReturn(Optional.of(tarefaEsperada));

        // Act
        Optional<Tarefas> resultado = tarefasService.buscarTarefaPeloId(uuidValido);

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(uuidEsperado);
        verify(tarefasRepository, times(1)).findById(uuidEsperado);
    }

    @Test
    @DisplayName("Deve listar todas as tarefas")
    void deveListarTodasTarefas() {
        // Arrange
        Tarefas tarefa2 = Tarefas.builder()
                .id(UUID.randomUUID())
                .titulo("Tarefa 2")
                .descricao("Descrição 2")
                .status(Status.CONCLUIDA)
                .usuario(usuario)
                .build();

        List<Tarefas> tarefas = Arrays.asList(tarefa, tarefa2);
        when(tarefasRepository.findAll()).thenReturn(tarefas);

        // Act
        List<Tarefas> resultado = tarefasService.listarTarefas();

        // Assert
        assertThat(resultado).hasSize(2);
        assertThat(resultado).containsExactly(tarefa, tarefa2);
        verify(tarefasRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há tarefas")
    void deveRetornarListaVaziaQuandoNaoHaTarefas() {
        // Arrange
        when(tarefasRepository.findAll()).thenReturn(List.of());

        // Act
        List<Tarefas> resultado = tarefasService.listarTarefas();

        // Assert
        assertThat(resultado).isEmpty();
        verify(tarefasRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve atualizar todos os campos da tarefa")
    void deveAtualizarTodosCamposDaTarefa() {
        // Arrange
        AtualizarTarefaDto dto = new AtualizarTarefaDto(
                "Título Atualizado",
                "Descrição Atualizada",
                Status.CONCLUIDA
        );

        when(tarefasRepository.findById(tarefaId)).thenReturn(Optional.of(tarefa));
        when(tarefasRepository.saveAndFlush(any(Tarefas.class))).thenReturn(tarefa);

        // Act
        Tarefas resultado = tarefasService.atualizarTarefaPeloId(tarefaId, dto);

        // Assert
        verify(tarefasRepository, times(1)).findById(tarefaId);
        verify(tarefasRepository, times(1)).saveAndFlush(tarefa);
        assertThat(tarefa.getTitulo()).isEqualTo("Título Atualizado");
        assertThat(tarefa.getDescricao()).isEqualTo("Descrição Atualizada");
        assertThat(tarefa.getStatus()).isEqualTo(Status.CONCLUIDA);
    }

    @Test
    @DisplayName("Deve atualizar apenas o título quando outros campos são nulos")
    void deveAtualizarApenasOTituloQuandoOutrosCamposNulos() {
        // Arrange
        AtualizarTarefaDto dto = new AtualizarTarefaDto(
                "Novo Título",
                null,
                null
        );

        when(tarefasRepository.findById(tarefaId)).thenReturn(Optional.of(tarefa));
        when(tarefasRepository.saveAndFlush(any(Tarefas.class))).thenReturn(tarefa);

        // Act
        Tarefas resultado = tarefasService.atualizarTarefaPeloId(tarefaId, dto);

        // Assert
        assertThat(tarefa.getTitulo()).isEqualTo("Novo Título");
        assertThat(tarefa.getDescricao()).isEqualTo("Descrição Teste"); // Não mudou
        assertThat(tarefa.getStatus()).isEqualTo(Status.PENDENTE); // Não mudou
    }

    @Test
    @DisplayName("Deve atualizar apenas status")
    void deveAtualizarApenasStatus() {
        // Arrange
        AtualizarTarefaDto dto = new AtualizarTarefaDto(
                null,
                null,
                Status.EM_ANDAMENTO
        );

        when(tarefasRepository.findById(tarefaId)).thenReturn(Optional.of(tarefa));
        when(tarefasRepository.saveAndFlush(any(Tarefas.class))).thenReturn(tarefa);

        // Act
        Tarefas resultado = tarefasService.atualizarTarefaPeloId(tarefaId, dto);

        // Assert
        assertThat(tarefa.getTitulo()).isEqualTo("Tarefa Teste"); // Não mudou
        assertThat(tarefa.getDescricao()).isEqualTo("Descrição Teste"); // Não mudou
        assertThat(tarefa.getStatus()).isEqualTo(Status.EM_ANDAMENTO);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar tarefa inexistente")
    void deveLancarExcecaoAoAtualizarTarefaInexistente() {
        // Arrange
        UUID idInexistente = UUID.randomUUID();
        AtualizarTarefaDto dto = new AtualizarTarefaDto(
                "Título",
                "Descrição",
                Status.PENDENTE
        );

        when(tarefasRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> tarefasService.atualizarTarefaPeloId(idInexistente, dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Tarefa não encontrada");

        verify(tarefasRepository, times(1)).findById(idInexistente);
        verify(tarefasRepository, never()).saveAndFlush(any(Tarefas.class));
    }


    @Test
    @DisplayName("Deve deletar tarefa quando existe")
    void deveDeletarTarefaQuandoExiste() {
        // Arrange
        String tarefaIdString = tarefaId.toString();
        when(tarefasRepository.existsById(tarefaId)).thenReturn(true);
        doNothing().when(tarefasRepository).deleteById(tarefaId);

        // Act
        tarefasService.deletarPeloId(tarefaIdString);

        // Assert
        verify(tarefasRepository, times(1)).existsById(tarefaId);
        verify(tarefasRepository, times(1)).deleteById(tarefaId);
    }

    @Test
    @DisplayName("Não deve deletar quando tarefa não existe")
    void naoDeveDeletarQuandoTarefaNaoExiste() {
        // Arrange
        String tarefaIdString = tarefaId.toString();
        when(tarefasRepository.existsById(tarefaId)).thenReturn(false);

        // Act
        tarefasService.deletarPeloId(tarefaIdString);

        // Assert
        verify(tarefasRepository, times(1)).existsById(tarefaId);
        verify(tarefasRepository, never()).deleteById(any(UUID.class));
    }

    @Test
    @DisplayName("Deve converter String para UUID corretamente ao deletar")
    void deveConverterStringParaUUIDCorretamenteAoDeletar() {
        // Arrange
        String uuidValido = "550e8400-e29b-41d4-a716-446655440000";
        UUID uuidEsperado = UUID.fromString(uuidValido);

        when(tarefasRepository.existsById(uuidEsperado)).thenReturn(true);
        doNothing().when(tarefasRepository).deleteById(uuidEsperado);

        // Act
        tarefasService.deletarPeloId(uuidValido);

        // Assert
        verify(tarefasRepository, times(1)).existsById(uuidEsperado);
        verify(tarefasRepository, times(1)).deleteById(uuidEsperado);
    }
}
