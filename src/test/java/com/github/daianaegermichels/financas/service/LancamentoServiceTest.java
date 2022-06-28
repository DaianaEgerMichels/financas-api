package com.github.daianaegermichels.financas.service;

import com.github.daianaegermichels.financas.enuns.StatusLancamento;
import com.github.daianaegermichels.financas.enuns.TipoLancamento;
import com.github.daianaegermichels.financas.exception.RegraNegocioException;
import com.github.daianaegermichels.financas.model.Lancamento;
import com.github.daianaegermichels.financas.model.Usuario;
import com.github.daianaegermichels.financas.repository.LancamentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.github.daianaegermichels.financas.service.UsuarioServiceTest.criarUsuario;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


@ActiveProfiles("Test")
public class LancamentoServiceTest {

    @Mock
    LancamentoService service;

    @Mock
    LancamentoRepository repository;

    @InjectMocks
    LancamentoServiceImpl lancamentoService;

    @BeforeEach
    public void setup(){
        MockitoAnnotations.openMocks(this);
        criarLancamento();
    }


    @Test
    @DisplayName("Salvar Lançamento")
    public void deveSalvarUmLancamento(){
        //cenário
        var lancamentoASalvar = criarLancamento();
        doNothing().when(service).validar(lancamentoASalvar);
        when(repository.save(Mockito.any(Lancamento.class))).thenReturn(criarLancamento());

        //ação
        var lancamentoSalvo = lancamentoService.salvar(criarLancamento());

        //validação
        assertThat(lancamentoSalvo).isNotNull();
        assertEquals(lancamentoSalvo.getId(), criarLancamento().getId());
        assertThat(lancamentoSalvo.getStatus()).isEqualByComparingTo(StatusLancamento.PENDENTE);
    }

    @Test
    @DisplayName("Não salvar lançamento")
    public void naoDeveSalvarUmLancamentoQuandoHouverErroDeValidacao(){

        //cenário
        var lancamento = Lancamento.builder().ano(2021).descricao("Teste lançamento inválido").mes(5).dataCadastro(LocalDate.now()).build();
        doThrow(RegraNegocioException.class).when(service).validar(lancamento);

        //ação
        try{
            lancamentoService.salvar(lancamento);
        } catch (Exception ex){
            assertEquals(RegraNegocioException.class, ex.getClass());
            verify(repository, never()).save(lancamento);
        }
    }

    public static Lancamento criarLancamento() {
        return Lancamento.builder()
                .id(1l)
                .ano(2022)
                .mes(6)
                .descricao("Recebimento de Pix")
                .valor(BigDecimal.valueOf(100.00))
                .tipo(TipoLancamento.RECEITA)
                .status(StatusLancamento.PENDENTE)
                .dataCadastro(LocalDate.now())
                .usuario(criarUsuario())
                .build();
    }

}
