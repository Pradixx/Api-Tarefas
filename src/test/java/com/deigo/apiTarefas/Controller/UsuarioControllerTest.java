package com.deigo.apiTarefas.Controller;

import com.deigo.apiTarefas.Security.SecurityConfigTest;
import com.deigo.apiTarefas.controller.UsuarioController;
import com.deigo.apiTarefas.controller.dtoUsuarios.AtualizarUsuariosDto;
import com.deigo.apiTarefas.controller.dtoUsuarios.CriarUsuariosDto;
import com.deigo.apiTarefas.infrastructure.enumTarefas.Status;
import com.deigo.apiTarefas.infrastructure.entitys.Tarefas;
import com.deigo.apiTarefas.infrastructure.entitys.Usuario;
import com.deigo.apiTarefas.service.UsuarioService;
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
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuarioController.class)
@ActiveProfiles("test")
@Import(SecurityConfigTest.class)
@DisplayName("Testes do UsuarioController")
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UsuarioService usuarioService;

    private Usuario usuario;
    private CriarUsuariosDto criarUsuarioDto;
    private AtualizarUsuariosDto atualizarUsuarioDto;
    private UUID usuarioId;

    @BeforeEach
    void setUp() {
        usuarioId = UUID.randomUUID();

        usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setNome("Usuario Teste");
        usuario.setEmail("teste@exemplo.com");
        usuario.setSenha("senha123");

        criarUsuarioDto = new CriarUsuariosDto(
                "Usuario Teste",
                "teste@exemplo.com",
                "senha123"
        );

        atualizarUsuarioDto = new AtualizarUsuariosDto(
                "Nome Atualizado",
                "email@atualizado.com",
                "novaSenha"
        );
    }

    @Test
    @DisplayName("POST /usuarios - Deve criar usuário com sucesso")
    void deveRetornar200AoCriarUsuario() throws Exception {
        // Arrange
        doNothing().when(usuarioService).criarUsuario(any(CriarUsuariosDto.class));

        // Act & Assert
        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(criarUsuarioDto)))
                .andExpect(status().isOk());

        verify(usuarioService, times(1)).criarUsuario(any(CriarUsuariosDto.class));
    }

    @Test
    @DisplayName("POST /usuarios - Deve retornar 400 quando body é inválido")
    void deveRetornar400QuandoBodyInvalido() throws Exception {
        // Arrange
        String jsonInvalido = "{ invalid json }";

        // Act & Assert
        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInvalido))
                .andExpect(status().isBadRequest());

        verify(usuarioService, never()).criarUsuario(any(CriarUsuariosDto.class));
    }

    @Test
    @DisplayName("POST /usuarios - Deve propagar exceção quando service lança erro")
    void devePropararExcecaoQuandoServiceLancaExcecao() throws Exception {
        // Arrange
        doThrow(new RuntimeException("Email já existe"))
                .when(usuarioService).criarUsuario(any(CriarUsuariosDto.class));

        // Act & Assert
        try {
            mockMvc.perform(post("/usuarios")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(criarUsuarioDto)));
        } catch (Exception e) {
            assertThat(e.getCause())
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Email já existe");
        }

        verify(usuarioService, times(1)).criarUsuario(any(CriarUsuariosDto.class));
    }

    // ==================== TESTES DE GET /usuarios ====================

    @Test
    @DisplayName("GET /usuarios - Deve listar todos os usuários")
    void deveRetornar200ComListaDeUsuarios() throws Exception {
        // Arrange
        Usuario usuario2 = new Usuario();
        usuario2.setId(UUID.randomUUID());
        usuario2.setNome("Usuario 2");
        usuario2.setEmail("usuario2@exemplo.com");

        List<Usuario> usuarios = Arrays.asList(usuario, usuario2);
        when(usuarioService.listarUsuarios()).thenReturn(usuarios);

        // Act & Assert
        mockMvc.perform(get("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nome").value("Usuario Teste"))
                .andExpect(jsonPath("$[0].email").value("teste@exemplo.com"))
                .andExpect(jsonPath("$[1].nome").value("Usuario 2"))
                .andExpect(jsonPath("$[1].email").value("usuario2@exemplo.com"));

        verify(usuarioService, times(1)).listarUsuarios();
    }

    @Test
    @DisplayName("GET /usuarios - Deve retornar lista vazia quando não há usuários")
    void deveRetornar200ComListaVazia() throws Exception {
        // Arrange
        when(usuarioService.listarUsuarios()).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(usuarioService, times(1)).listarUsuarios();
    }

    // ==================== TESTES DE PUT /usuarios/{id} ====================

    @Test
    @DisplayName("PUT /usuarios/{id} - Deve atualizar usuário com sucesso")
    void deveRetornar200AoAtualizarUsuario() throws Exception {
        // Arrange
        usuario.setNome("Nome Atualizado");
        usuario.setEmail("email@atualizado.com");
        when(usuarioService.atualizarUsuario(eq(usuarioId), any(AtualizarUsuariosDto.class)))
                .thenReturn(usuario);

        // Act & Assert
        mockMvc.perform(put("/usuarios/{id}", usuarioId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atualizarUsuarioDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(usuarioId.toString()))
                .andExpect(jsonPath("$.nome").value("Nome Atualizado"))
                .andExpect(jsonPath("$.email").value("email@atualizado.com"));

        verify(usuarioService, times(1)).atualizarUsuario(eq(usuarioId), any(AtualizarUsuariosDto.class));
    }

    @Test
    @DisplayName("PUT /usuarios/{id} - Deve propagar exceção quando usuário não existe")
    void devePropararExcecaoAoAtualizarUsuarioInexistente() throws Exception {
        // Arrange
        UUID idInexistente = UUID.randomUUID();
        when(usuarioService.atualizarUsuario(eq(idInexistente), any(AtualizarUsuariosDto.class)))
                .thenThrow(new RuntimeException("Usuário não encontrado"));

        // Act & Assert
        try {
            mockMvc.perform(put("/usuarios/{id}", idInexistente)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(atualizarUsuarioDto)));
        } catch (Exception e) {
            assertThat(e.getCause())
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Usuário não encontrado");
        }

        verify(usuarioService, times(1)).atualizarUsuario(eq(idInexistente), any(AtualizarUsuariosDto.class));
    }

    @Test
    @DisplayName("PUT /usuarios/{id} - Deve aceitar atualização parcial")
    void deveAceitarAtualizacaoParcial() throws Exception {
        // Arrange
        AtualizarUsuariosDto dtoParcial = new AtualizarUsuariosDto(
                "Apenas Nome",
                null,
                null
        );

        usuario.setNome("Apenas Nome");
        when(usuarioService.atualizarUsuario(eq(usuarioId), any(AtualizarUsuariosDto.class)))
                .thenReturn(usuario);

        // Act & Assert
        mockMvc.perform(put("/usuarios/{id}", usuarioId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoParcial)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Apenas Nome"));

        verify(usuarioService, times(1)).atualizarUsuario(eq(usuarioId), any(AtualizarUsuariosDto.class));
    }

    // ==================== TESTES DE DELETE /usuarios/{id} ====================

    @Test
    @DisplayName("DELETE /usuarios/{id} - Deve deletar usuário com sucesso")
    void deveRetornar204AoDeletarUsuario() throws Exception {
        // Arrange
        doNothing().when(usuarioService).deletarUsuario(usuarioId);

        // Act & Assert
        mockMvc.perform(delete("/usuarios/{id}", usuarioId))
                .andExpect(status().isNoContent());

        verify(usuarioService, times(1)).deletarUsuario(usuarioId);
    }

    @Test
    @DisplayName("DELETE /usuarios/{id} - Deve propagar exceção quando usuário não existe")
    void devePropararExcecaoAoDeletarUsuarioInexistente() throws Exception {
        // Arrange
        UUID idInexistente = UUID.randomUUID();
        doThrow(new RuntimeException("Usuário não encontrado"))
                .when(usuarioService).deletarUsuario(idInexistente);

        // Act & Assert
        try {
            mockMvc.perform(delete("/usuarios/{id}", idInexistente));
        } catch (Exception e) {
            assertThat(e.getCause())
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Usuário não encontrado");
        }

        verify(usuarioService, times(1)).deletarUsuario(idInexistente);
    }

    // ==================== TESTES DE GET /usuarios/{id}/tarefas ====================

    @Test
    @DisplayName("GET /usuarios/{id}/tarefas - Deve listar tarefas do usuário")
    void deveRetornar200ComTarefasDoUsuario() throws Exception {
        // Arrange
        Tarefas tarefa1 = Tarefas.builder()
                .id(UUID.randomUUID())
                .titulo("Tarefa 1")
                .descricao("Descrição 1")
                .status(Status.PENDENTE)
                .usuario(usuario)
                .build();

        Tarefas tarefa2 = Tarefas.builder()
                .id(UUID.randomUUID())
                .titulo("Tarefa 2")
                .descricao("Descrição 2")
                .status(Status.CONCLUIDA)
                .usuario(usuario)
                .build();

        List<Tarefas> tarefas = Arrays.asList(tarefa1, tarefa2);
        when(usuarioService.listarTarefasDoUsuario(usuarioId)).thenReturn(tarefas);

        // Act & Assert
        mockMvc.perform(get("/usuarios/{id}/tarefas", usuarioId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].titulo").value("Tarefa 1"))
                .andExpect(jsonPath("$[0].status").value("PENDENTE"))
                .andExpect(jsonPath("$[1].titulo").value("Tarefa 2"))
                .andExpect(jsonPath("$[1].status").value("CONCLUIDA"));

        verify(usuarioService, times(1)).listarTarefasDoUsuario(usuarioId);
    }

    @Test
    @DisplayName("GET /usuarios/{id}/tarefas - Deve retornar lista vazia quando usuário não tem tarefas")
    void deveRetornar200ComListaVaziaDeTarefas() throws Exception {
        // Arrange
        when(usuarioService.listarTarefasDoUsuario(usuarioId)).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/usuarios/{id}/tarefas", usuarioId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(usuarioService, times(1)).listarTarefasDoUsuario(usuarioId);
    }

    @Test
    @DisplayName("GET /usuarios/{id}/tarefas - Deve aceitar UUID válido no path")
    void deveAceitarUUIDValidoNoPath() throws Exception {
        // Arrange
        String uuidValido = "550e8400-e29b-41d4-a716-446655440000";
        UUID uuid = UUID.fromString(uuidValido);
        when(usuarioService.listarTarefasDoUsuario(uuid)).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/usuarios/{id}/tarefas", uuidValido)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(usuarioService, times(1)).listarTarefasDoUsuario(uuid);
    }

    // ==================== TESTES DE VALIDAÇÃO ====================

    @Test
    @DisplayName("Deve rejeitar UUID inválido no path")
    void deveRejeitarUUIDInvalido() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/usuarios/{id}/tarefas", "id-invalido")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(usuarioService, never()).listarTarefasDoUsuario(any(UUID.class));
    }

    @Test
    @DisplayName("Deve retornar 404 para rota inexistente")
    void deveRetornar404ParaRotaInexistente() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/rota-completamente-inexistente")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}