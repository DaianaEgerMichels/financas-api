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

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

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
        when(repository.findByEmail(anyString())).thenThrow(new ErroAutenticacao("Usuário não encontrado para o email informado!"));

        //ação
        try{
            usuarioService.autenticar("email@email.com", criarUsuario().getSenha());
        } catch (Exception ex){
            assertEquals(ErroAutenticacao.class, ex.getClass());
            assertEquals("Usuário não encontrado para o email informado!", ex.getMessage());
        }
    }

    @Test
    @DisplayName("Senha inválida")
    public void deveLancarExcecaoQuandoPassarUmaSenhaIncorretaParaOEmailInformado(){
        //cenário
        when(repository.findByEmail(criarUsuario().getEmail())).thenReturn(Optional.of(criarUsuario()));

        //ação
        try{
            usuarioService.autenticar("usuario@email.com", "12345");
        } catch (Exception ex){
            assertEquals(ErroAutenticacao.class, ex.getClass());
            assertEquals("Senha inválida!", ex.getMessage());
        }

    }

    @Test
    @DisplayName("Salvar usuário")
    public void deveSalvarUmUsuario(){
        //cenário
        doNothing().when(service).validarEmail(anyString());
        when(repository.save(Mockito.any(Usuario.class))).thenReturn(criarUsuario());

        //ação
        var usuarioSalvo = usuarioService.salvarUsuario(criarUsuario());
        assertThat(usuarioSalvo).isNotNull();
        assertEquals(usuarioSalvo.getId(), criarUsuario().getId());
        assertEquals(usuarioSalvo.getNome(), criarUsuario().getNome());
        assertEquals(usuarioSalvo.getEmail(), criarUsuario().getEmail());
        assertEquals(usuarioSalvo.getSenha(), criarUsuario().getSenha());

    }

    @Test
    @DisplayName("Não salvar usuário")
    public void naoDeveSalvarUsuarioComEmailJaCadastrado(){
        //cenário
        String email = "usuario@email.com";
        var usuario = Usuario.builder().email(email).build();
        doThrow(RegraNegocioException.class).when(service).validarEmail(email);

        //ação
        try{
            usuarioService.salvarUsuario(usuario);
        } catch (Exception ex){
            assertEquals(RegraNegocioException.class, ex.getClass());
            assertEquals("Já existe um usuário com este email!", ex.getMessage());
            verify(repository, never()).save(usuario);
        }

    }

    public static Usuario criarUsuario() {
        return Usuario
                .builder()
                .id(1L)
                .nome("usuario")
                .email("usuario@email.com")
                .senha("senha")
                .build();
    }
}
