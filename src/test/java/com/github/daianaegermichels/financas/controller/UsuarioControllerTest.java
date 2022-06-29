package com.github.daianaegermichels.financas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.daianaegermichels.financas.api.controller.UsuarioController;
import com.github.daianaegermichels.financas.dto.UsuarioDTO;
import com.github.daianaegermichels.financas.exception.ErroAutenticacao;
import com.github.daianaegermichels.financas.exception.RegraNegocioException;
import com.github.daianaegermichels.financas.model.Usuario;
import com.github.daianaegermichels.financas.service.LancamentoService;
import com.github.daianaegermichels.financas.service.UsuarioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = UsuarioController.class)
@AutoConfigureMockMvc
public class UsuarioControllerTest {

    static final String API = "/api/usuarios";
    static final MediaType JSON = MediaType.APPLICATION_JSON;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private LancamentoService lancamentoService;

    @Test
    @DisplayName("Autenticação usuário - ok")
    public void deveAutenticarUmUsuario() throws Exception {
        //cenario
        String email = "usuario@email.com";
        String senha = "123";
        String nome = "usuario";

        var dto = UsuarioDTO.builder().email(email).senha(senha).nome(nome).build();
        var usuario = Usuario.builder().id(1l).email(email).senha(senha).nome(nome).build();

        when(usuarioService.autenticar(email, senha)).thenReturn(usuario);

        var json = new ObjectMapper().writeValueAsString(dto);

        //execução e verificação
        var request = MockMvcRequestBuilders.post(API.concat("/autenticar"))
                .accept(JSON)
                .contentType(JSON)
                .content(json);

        mockMvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(usuario.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("nome").value(usuario.getNome()))
                .andExpect(MockMvcResultMatchers.jsonPath("email").value(usuario.getEmail()));
    }

    @Test
    @DisplayName("Autenticação usuário - bad request")
    public void deveRetornarBadRequestAoObterErroDeAutenticacao() throws Exception {
        //cenario
        String email = "usuario@email.com";
        String senha = "123";
        String nome = "usuario";

        var dto = UsuarioDTO.builder().email(email).senha(senha).nome(nome).build();

        when(usuarioService.autenticar(email, senha)).thenThrow(ErroAutenticacao.class);

        var json = new ObjectMapper().writeValueAsString(dto);

        //execução e verificação
        var request = MockMvcRequestBuilders.post(API.concat("/autenticar"))
                .accept(JSON)
                .contentType(JSON)
                .content(json);

        mockMvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @DisplayName("Salvar usuário - created")
    public void deveSalvarUmUsuario() throws Exception {
        //cenario
        String email = "usuario@email.com";
        String senha = "123";
        String nome = "usuario";

        var dto = UsuarioDTO.builder().email(email).senha(senha).nome(nome).build();
        var usuario = Usuario.builder().id(1l).email(email).senha(senha).nome(nome).build();

        when(usuarioService.salvarUsuario(any(Usuario.class))).thenReturn(usuario);

        var json = new ObjectMapper().writeValueAsString(dto);

        //execução e verificação
        var request = MockMvcRequestBuilders.post(API)
                .accept(JSON)
                .contentType(JSON)
                .content(json);

        mockMvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(usuario.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("nome").value(usuario.getNome()))
                .andExpect(MockMvcResultMatchers.jsonPath("email").value(usuario.getEmail()));
    }

    @Test
    @DisplayName("Salvar usuário - bad request")
    public void naoDeveSalvarUmUsuario() throws Exception {
        //cenario
        String email = "usuario@email.com";
        String senha = "123";
        String nome = "usuario";

        var dto = UsuarioDTO.builder().email(email).senha(senha).nome(nome).build();

        when(usuarioService.salvarUsuario(any(Usuario.class))).thenThrow(RegraNegocioException.class);

        var json = new ObjectMapper().writeValueAsString(dto);

        //execução e verificação
        var request = MockMvcRequestBuilders.post(API)
                .accept(JSON)
                .contentType(JSON)
                .content(json);

        mockMvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

}
