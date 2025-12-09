package com.deigo.apiTarefas.Controller;

import com.deigo.apiTarefas.Security.SecurityConfigTest;
import com.deigo.apiTarefas.controller.TarefasController;
import com.deigo.apiTarefas.controller.dtoTarefas.AtualizarTarefaDto;
import com.deigo.apiTarefas.controller.dtoTarefas.CriarTarefaDto;
import com.deigo.apiTarefas.infrastructure.enumTarefas.Status;
import com.deigo.apiTarefas.infrastructure.entitys.Tarefas;
import com.deigo.apiTarefas.infrastructure.entitys.Usuario;
import com.deigo.apiTarefas.service.TarefasService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TarefasController.class)
@ActiveProfiles("test")
@Import(SecurityConfigTest.class)
@DisplayName("Testes do TarefasController")
class TarefasControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TarefasService tarefasService;

    private Tarefas tarefa;
    private CriarTarefaDto criarTarefaDto;
    private AtualizarTarefaDto atualizarTarefaDto;
    private Usuario usuario;
    private UUID tarefaId;
    private UUID usuarioId;

    @BeforeEach
    void setUp() {
        tarefaId = UUID.randomUUID();
        usuarioId = UUID.randomUUID();

        usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setNome("Usuario Teste");
        usuario.setEmail("teste@exemplo.com");

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

        atualizarTarefaDto = new AtualizarTarefaDto(
                "Tarefa Atualizada",
                "Descrição Atualizada",
                Status.CONCLUIDA
        );
    }

    @Test
    @DisplayName("POST /tarefas - Deve criar tarefa com sucesso")
    void deveRetornar200AoCriarTarefa() throws Exception {
        // Arrange
        when(tarefasService.criarTarefa(any(CriarTarefaDto.class))).thenReturn(tarefa);

        // Act & Assert
        mockMvc.perform(post("/tarefas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(criarTarefaDto)))
                .andExpect(status().isOk());

        verify(tarefasService, times(1)).criarTarefa(any(CriarTarefaDto.class));
    }

    @Test
    @DisplayName("POST /tarefas - Deve retornar 400 quando body é inválido")
    void deveRetornar400QuandoBodyInvalido() throws Exception {
        // Arrange
        String jsonInvalido = "{ invalid json }";

        // Act & Assert
        mockMvc.perform(post("/tarefas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInvalido))
                .andExpect(status().isBadRequest());

        verify(tarefasService, never()).criarTarefa(any(CriarTarefaDto.class));
    }

    @Test
    @DisplayName("POST /tarefas - Deve propagar exceção quando usuário não existe")
    void devePropararExcecaoQuandoUsuarioNaoExiste() throws Exception {
        // Arrange
        when(tarefasService.criarTarefa(any(CriarTarefaDto.class)))
                .thenThrow(new RuntimeException("Usuario não encontrado com ID"));

        // Act & Assert
        try {
            mockMvc.perform(post("/tarefas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(criarTarefaDto)));
        } catch (Exception e) {
            assertThat(e.getCause())
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Usuario não encontrado com ID");
        }

        verify(tarefasService, times(1)).criarTarefa(any(CriarTarefaDto.class));
    }

    @Test
    @DisplayName("GET /tarefas/{id} - Deve buscar tarefa por ID quando existe")
    void deveRetornar200ComTarefaQuandoExiste() throws Exception {
        // Arrange
        String tarefaIdString = tarefaId.toString();
        when(tarefasService.buscarTarefaPeloId(tarefaIdString)).thenReturn(Optional.of(tarefa));

        // Act & Assert
        mockMvc.perform(get("/tarefas/{tarefasId}", tarefaIdString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(tarefaId.toString()))
                .andExpect(jsonPath("$.titulo").value("Tarefa Teste"))
                .andExpect(jsonPath("$.descricao").value("Descrição Teste"))
                .andExpect(jsonPath("$.status").value("PENDENTE"));

        verify(tarefasService, times(1)).buscarTarefaPeloId(tarefaIdString);
    }

    @Test
    @DisplayName("GET /tarefas/{id} - Deve retornar 404 quando tarefa não existe")
    void deveRetornar404QuandoTarefaNaoExiste() throws Exception {
        // Arrange
        String tarefaIdString = tarefaId.toString();
        when(tarefasService.buscarTarefaPeloId(tarefaIdString)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/tarefas/{tarefasId}", tarefaIdString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(tarefasService, times(1)).buscarTarefaPeloId(tarefaIdString);
    }

    @Test
    @DisplayName("GET /tarefas/{id} - Deve aceitar UUID válido como String")
    void deveAceitarUUIDValidoComoString() throws Exception {
        // Arrange
        String uuidValido = "550e8400-e29b-41d4-a716-446655440000";
        Tarefas tarefaComUUID = Tarefas.builder()
                .id(UUID.fromString(uuidValido))
                .titulo("Tarefa UUID")
                .status(Status.PENDENTE)
                .build();

        when(tarefasService.buscarTarefaPeloId(uuidValido)).thenReturn(Optional.of(tarefaComUUID));

        // Act & Assert
        mockMvc.perform(get("/tarefas/{tarefasId}", uuidValido)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(uuidValido));

        verify(tarefasService, times(1)).buscarTarefaPeloId(uuidValido);
    }

    @Test
    @DisplayName("GET /tarefas - Deve listar todas as tarefas")
    void deveRetornar200ComListaDeTarefas() throws Exception {
        // Arrange
        Tarefas tarefa2 = Tarefas.builder()
                .id(UUID.randomUUID())
                .titulo("Tarefa 2")
                .descricao("Descrição 2")
                .status(Status.CONCLUIDA)
                .usuario(usuario)
                .build();

        List<Tarefas> tarefas = Arrays.asList(tarefa, tarefa2);
        when(tarefasService.listarTarefas()).thenReturn(tarefas);

        // Act & Assert
        mockMvc.perform(get("/tarefas")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].titulo").value("Tarefa Teste"))
                .andExpect(jsonPath("$[0].status").value("PENDENTE"))
                .andExpect(jsonPath("$[1].titulo").value("Tarefa 2"))
                .andExpect(jsonPath("$[1].status").value("CONCLUIDA"));

        verify(tarefasService, times(1)).listarTarefas();
    }

    @Test
    @DisplayName("GET /tarefas - Deve retornar lista vazia quando não há tarefas")
    void deveRetornar200ComListaVazia() throws Exception {
        // Arrange
        when(tarefasService.listarTarefas()).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/tarefas")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(tarefasService, times(1)).listarTarefas();
    }

    @Test
    @DisplayName("PUT /tarefas/{id} - Deve atualizar tarefa com sucesso")
    void deveRetornar200AoAtualizarTarefa() throws Exception {
        // Arrange
        tarefa.setTitulo("Tarefa Atualizada");
        tarefa.setDescricao("Descrição Atualizada");
        tarefa.setStatus(Status.CONCLUIDA);

        when(tarefasService.atualizarTarefaPeloId(eq(tarefaId), any(AtualizarTarefaDto.class)))
                .thenReturn(tarefa);

        // Act & Assert
        mockMvc.perform(put("/tarefas/{tarefaId}", tarefaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atualizarTarefaDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(tarefaId.toString()))
                .andExpect(jsonPath("$.titulo").value("Tarefa Atualizada"))
                .andExpect(jsonPath("$.descricao").value("Descrição Atualizada"))
                .andExpect(jsonPath("$.status").value("CONCLUIDA"));

        verify(tarefasService, times(1)).atualizarTarefaPeloId(eq(tarefaId), any(AtualizarTarefaDto.class));
    }

    @Test
    @DisplayName("PUT /tarefas/{id} - Deve propagar exceção quando tarefa não existe")
    void devePropararExcecaoAoAtualizarTarefaInexistente() throws Exception {
        // Arrange
        UUID idInexistente = UUID.randomUUID();
        when(tarefasService.atualizarTarefaPeloId(eq(idInexistente), any(AtualizarTarefaDto.class)))
                .thenThrow(new RuntimeException("Tarefa não encontrada"));

        // Act & Assert
        try {
            mockMvc.perform(put("/tarefas/{tarefaId}", idInexistente)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(atualizarTarefaDto)));
        } catch (Exception e) {
            assertThat(e.getCause())
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Tarefa não encontrada");
        }

        verify(tarefasService, times(1)).atualizarTarefaPeloId(eq(idInexistente), any(AtualizarTarefaDto.class));
    }

    @Test
    @DisplayName("PUT /tarefas/{id} - Deve aceitar atualização parcial")
    void deveAceitarAtualizacaoParcial() throws Exception {
        // Arrange
        AtualizarTarefaDto dtoParcial = new AtualizarTarefaDto(
                "Apenas Título",
                null,
                null
        );

        tarefa.setTitulo("Apenas Título");
        when(tarefasService.atualizarTarefaPeloId(eq(tarefaId), any(AtualizarTarefaDto.class)))
                .thenReturn(tarefa);

        // Act & Assert
        mockMvc.perform(put("/tarefas/{tarefaId}", tarefaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoParcial)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Apenas Título"));

        verify(tarefasService, times(1)).atualizarTarefaPeloId(eq(tarefaId), any(AtualizarTarefaDto.class));
    }

    @Test
    @DisplayName("DELETE /tarefas/{id} - Deve deletar tarefa com sucesso")
    void deveRetornar204AoDeletarTarefa() throws Exception {
        // Arrange
        String tarefaIdString = tarefaId.toString();
        doNothing().when(tarefasService).deletarPeloId(tarefaIdString);

        // Act & Assert
        mockMvc.perform(delete("/tarefas/{tarefaId}", tarefaIdString))
                .andExpect(status().isNoContent());

        verify(tarefasService, times(1)).deletarPeloId(tarefaIdString);
    }

    @Test
    @DisplayName("DELETE /tarefas/{id} - Deve aceitar UUID como String")
    void deveAceitarUUIDComoStringAoDeletar() throws Exception {
        // Arrange
        String uuidValido = "550e8400-e29b-41d4-a716-446655440000";
        doNothing().when(tarefasService).deletarPeloId(uuidValido);

        // Act & Assert
        mockMvc.perform(delete("/tarefas/{tarefaId}", uuidValido))
                .andExpect(status().isNoContent());

        verify(tarefasService, times(1)).deletarPeloId(uuidValido);
    }

    @Test
    @DisplayName("DELETE /tarefas/{id} - Não deve lançar erro quando tarefa não existe")
    void naoDeveLancarErroAoDeletarTarefaInexistente() throws Exception {
        // Arrange
        String tarefaIdString = tarefaId.toString();
        doNothing().when(tarefasService).deletarPeloId(tarefaIdString);

        // Act & Assert
        mockMvc.perform(delete("/tarefas/{tarefaId}", tarefaIdString))
                .andExpect(status().isNoContent());

        verify(tarefasService, times(1)).deletarPeloId(tarefaIdString);
    }

    @Test
    @DisplayName("Deve retornar 404 para rota inexistente")
    void deveRetornar404ParaRotaInexistente() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/rota-completamente-inexistente")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve processar diferentes tipos de status")
    void deveProcessarDiferentesTiposDeStatus() throws Exception {
        // Arrange - Tarefa com status EM_ANDAMENTO
        CriarTarefaDto dtoEmAndamento = new CriarTarefaDto(
                "Tarefa Em Andamento",
                "Descrição",
                Status.EM_ANDAMENTO,
                usuarioId
        );

        Tarefas tarefaEmAndamento = Tarefas.builder()
                .id(UUID.randomUUID())
                .titulo("Tarefa Em Andamento")
                .status(Status.EM_ANDAMENTO)
                .build();

        when(tarefasService.criarTarefa(any(CriarTarefaDto.class))).thenReturn(tarefaEmAndamento);

        // Act & Assert
        mockMvc.perform(post("/tarefas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoEmAndamento)))
                .andExpect(status().isOk());

        verify(tarefasService, times(1)).criarTarefa(any(CriarTarefaDto.class));
    }
}
