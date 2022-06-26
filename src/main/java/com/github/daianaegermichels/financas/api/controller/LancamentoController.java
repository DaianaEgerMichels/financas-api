package com.github.daianaegermichels.financas.api.controller;

import com.github.daianaegermichels.financas.dto.LancamentoDTO;
import com.github.daianaegermichels.financas.enuns.StatusLancamento;
import com.github.daianaegermichels.financas.enuns.TipoLancamento;
import com.github.daianaegermichels.financas.exception.RegraNegocioException;
import com.github.daianaegermichels.financas.model.Lancamento;
import com.github.daianaegermichels.financas.service.LancamentoService;
import com.github.daianaegermichels.financas.service.UsuarioService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/lancamentos")
public class LancamentoController {

    private LancamentoService service;

    private UsuarioService usuarioService;

    public LancamentoController(LancamentoService service, UsuarioService usuarioService) {
        this.service = service;
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity salvar (@RequestBody LancamentoDTO dto){
    return  new ResponseEntity<String>(HttpStatus.OK);
    }

    private Lancamento converter(LancamentoDTO dto){
        var lancamento = new Lancamento();
        lancamento.setId(dto.getId());
        lancamento.setDescricao(dto.getDescricao());
        lancamento.setAno(dto.getAno());
        lancamento.setMes(dto.getMes());
        lancamento.setValor(dto.getValor());

        var usuario = usuarioService.obterPorId(dto.getUsuario()).orElseThrow(()-> new RegraNegocioException("Usuário não encontrado para o Id informado!"));

        lancamento.setUsuario(usuario);
        lancamento.setTipo(TipoLancamento.valueOf(dto.getTipo()));
        lancamento.setStatus(StatusLancamento.valueOf(dto.getStatus()));
        return lancamento;
    }
}
