package com.github.daianaegermichels.financas.service;

import com.github.daianaegermichels.financas.exception.ErroAutenticacao;
import com.github.daianaegermichels.financas.exception.RegraNegocioException;
import com.github.daianaegermichels.financas.model.Usuario;
import com.github.daianaegermichels.financas.repository.UsuarioRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
public class UsuarioServiceTest {

    @Mock
    UsuarioService service;

    @Mock
    UsuarioRepository repository;

    @InjectMocks
    UsuarioServiceImpl usuarioService;

    @BeforeEach
    public void setup(){
        MockitoAnnotations.openMocks(this);
        criarUsuario();
    }

    @Test
    @DisplayName("Validar email")
    public void deveValidarEmail() {
        //cenário
        when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);

        //ação
        service.validarEmail("email@email.com");
    }

    @Test
    @DisplayName("Email existente")
    public void deveLancarErroAoValidarEmailQuandoExistirEmailCadastrado() {
        //cenário
        when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);
        repository.save(criarUsuario());

        //ação
        assertThrows(RegraNegocioException.class, () -> usuarioService.validarEmail("usuario@email.com"));
    }

    @Test
    @DisplayName("Usuário autenticado com sucesso")
    public void deveAutenticarUmUsuarioComSucesso(){
        //cenário
        when(repository.findByEmail(criarUsuario().getEmail())).thenReturn(Optional.of(criarUsuario()));

        //ação
        var result = usuarioService.autenticar(criarUsuario().getEmail(),criarUsuario().getSenha());

        //verificação
        Assertions.assertNotNull(result);
        assertEquals(criarUsuario().getEmail(), result.getEmail());
        assertEquals(criarUsuario().getSenha(), result.getSenha());
        assertEquals(criarUsuario().getNome(), result.getNome());
    }

    @Test
    @DisplayName("Email inválido")
    public void deveLancarExcecaoQuandoNaoEncontrarUsuarioCadastradoComOEmailInformado(){
        //cenário
        when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());

        //ação
        assertThrows(ErroAutenticacao.class, () -> usuarioService.autenticar("email@email.com", "senha"));
    }

    @Test
    @DisplayName("Senha inválida")
    public void deveLancarExcecaoQuandoPassarUmaSenhaIncorretaParaOEmailInformado(){
        //cenário
        when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());

        //ação
        assertThrows(ErroAutenticacao.class, () -> usuarioService.autenticar("usuario@email.com", "senha2"));
    }

    public static Usuario criarUsuario() {
        return Usuario
                .builder()
                .nome("usuario")
                .email("usuario@email.com")
                .senha("senha")
                .build();
    }
}
