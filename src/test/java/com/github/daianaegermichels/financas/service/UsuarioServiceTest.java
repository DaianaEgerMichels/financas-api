package com.github.daianaegermichels.financas.service;

import com.github.daianaegermichels.financas.exception.RegraNegocioException;
import com.github.daianaegermichels.financas.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static com.github.daianaegermichels.financas.repository.UsuarioRepositoryTest.criarUsuario;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

//@ExtendWith(SpringExtension.class)
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
}
