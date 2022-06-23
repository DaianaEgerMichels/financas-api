package com.github.daianaegermichels.financas.service;

import com.github.daianaegermichels.financas.exception.RegraNegocioException;
import com.github.daianaegermichels.financas.model.Usuario;
import com.github.daianaegermichels.financas.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.springframework.test.context.ActiveProfiles;

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
        //cenario
        when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);

        //ação
        service.validarEmail("email@email.com");
    }

    @Test
    @DisplayName("Email existente")
    public void deveLancarErroAoValidarEmailQuandoExistirEmailCadastrado() {
        //cenario
        when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);
        repository.save(criarUsuario());

        //acao
        assertThrows(RegraNegocioException.class, () -> usuarioService.validarEmail("usuario@email.com"));
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
