package com.github.daianaegermichels.financas.service;

import com.github.daianaegermichels.financas.enuns.StatusLancamento;
import com.github.daianaegermichels.financas.enuns.TipoLancamento;
import com.github.daianaegermichels.financas.exception.RegraNegocioException;
import com.github.daianaegermichels.financas.model.Lancamento;
import com.github.daianaegermichels.financas.repository.LancamentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static com.github.daianaegermichels.financas.service.UsuarioServiceTest.criarUsuario;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
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

    @Test
    @DisplayName("Atualizar lançamento")
    public void deveAtualizarLancamento(){
        var lancamentoSalvo = criarLancamento();
        lancamentoSalvo.setDescricao("Atualizando lançamento");

        doNothing().when(service).validar(lancamentoSalvo);
        when(repository.save(lancamentoSalvo)).thenReturn(lancamentoSalvo);

        lancamentoService.atualizar(lancamentoSalvo);

        verify(repository, times(1)).save(lancamentoSalvo);
    }

    @Test
    @DisplayName("Atualização inválida")
    public void deveLancarErroAoTentarAtualizarUmLancamentoQueNaoFoiSalvo(){
        var lancamentoNaoSalvo = criarLancamento();
        try{
            lancamentoService.atualizar(lancamentoNaoSalvo);
        } catch (Exception ex){
            assertEquals(NullPointerException.class, ex.getClass());
            verify(repository, never()).save(lancamentoNaoSalvo);
        }

    }

    @Test
    @DisplayName("Deletar lançamento")
    public void deveDeletarUmLancamentoQuandoPassarUmLancamentoComIdValido(){
        //cenário
        var lancamento = criarLancamento();

        //execução
        lancamentoService.deletar(lancamento);

        //verificação
        verify(repository).delete(lancamento);
    }

    @Test
    @DisplayName("Não deletar lançamento")
    public void naoDeveDeletarUmLancamentoQuandoPassarUmLancamentoInvalido(){

        //cenário
        var lancamento = Lancamento.builder()
                .ano(2022)
                .mes(6)
                .descricao("Recebimento de Pix")
                .valor(BigDecimal.valueOf(100.00))
                .tipo(TipoLancamento.RECEITA)
                .status(StatusLancamento.PENDENTE)
                .dataCadastro(LocalDate.now())
                .usuario(criarUsuario())
                .build();

        //execução
        try{
            lancamentoService.deletar(lancamento);
        } catch (Exception ex){
            assertEquals(NullPointerException.class, ex.getClass());
            verify(repository, never()).delete(lancamento);
        }
    }

    @Test
    @DisplayName("Buscar lançamentos")
    public void deveBuscarLancamentosQuandoPassarUmFiltro(){
        //cenário
        var lancamento = criarLancamento();
        var lista = new ArrayList< Lancamento >();
        lista.add(lancamento);
        when(repository.findAll(any(Example.class))).thenReturn(lista);

        //execução
        var resultado = lancamentoService.buscar(lancamento);

        //verificação
        assertThat(resultado).isNotNull();
        assertThat(resultado).contains(lancamento);
    }

    @Test
    @DisplayName("Atualizar status lançamento")
    public void deveAtualizarStatusDoLancamentoQuandoPassarUmStatusValido(){
        //cenário
        var lancamento = criarLancamento();
        var novoStatus = StatusLancamento.EFETIVADO;

        //execução
        lancamentoService.atualizarStatus(lancamento, novoStatus);

        //verificação
        assertThat(lancamento.getStatus()).isEqualByComparingTo(novoStatus);
        verify(repository).save(lancamento);
    }

    @Test
    @DisplayName("Buscar por Id")
    public void deveRetornarUmLancamentoQuandoBuscarPorUmIdValido(){
        //cenário
        var lancamento = criarLancamento();
        when(repository.findById(lancamento.getId())).thenReturn(Optional.of(lancamento));

        //execução
        var resultado = lancamentoService.obterPorId(lancamento.getId());

        //verificação
        assertThat(resultado.isPresent()).isTrue();
        assertEquals(resultado.get().getId(), lancamento.getId());
    }

    @Test
    @DisplayName("Erro ao buscar por Id inválido")
    public void deveRetornarVazioQuandoBuscarPorUmIdInvalido(){
        //cenário
        var lancamento = criarLancamento();
        when(repository.findById(lancamento.getId())).thenReturn(Optional.of(lancamento));
        when(repository.findById(2l)).thenReturn(Optional.empty());

        //execução
        var resultado = lancamentoService.obterPorId(2l);

        //verificação
        assertThat(resultado.isPresent()).isFalse();
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
