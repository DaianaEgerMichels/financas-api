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
import org.springframework.web.bind.annotation.*;

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
        try{
            var lancamento = converter(dto);
            lancamento = service.salvar(lancamento);
            return new ResponseEntity(lancamento, HttpStatus.CREATED);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("{id}")
    public ResponseEntity atualizar (@PathVariable("id") Long id, @RequestBody LancamentoDTO dto){
            return service.obterPorId(id).map(entity -> {
                try {
                    var lancamento = converter(dto);
                    lancamento.setId(entity.getId());
                    service.atualizar(lancamento);
                    return new ResponseEntity(lancamento, HttpStatus.OK);
                } catch (RegraNegocioException e){
                    return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
                }
                }).orElseGet(()-> ResponseEntity.badRequest().body("Lançamento não encontrado na base de dados!"));

    }

    @DeleteMapping("{id}")
    public ResponseEntity deletar(@PathVariable("id") Long id){
        return service.obterPorId(id).map(entity -> {
            service.deletar(entity);
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }).orElseGet(()-> ResponseEntity.badRequest().body("Lançamento não encontrado na base de dados!"));
    }

    @GetMapping
    public ResponseEntity buscar (
            @RequestParam(value = "descricao", required = false) String descricao,
            @RequestParam(value = "mes", required = false) Integer mes,
            @RequestParam(value = "ano", required = false) Integer ano,
            @RequestParam(value = "usuario") Long idUsuario
    ){
        var lancamentoFiltro = new Lancamento();
        lancamentoFiltro.setDescricao(descricao);
        lancamentoFiltro.setMes(mes);
        lancamentoFiltro.setAno(ano);

        var usuario = usuarioService.obterPorId(idUsuario);
        if(usuario.isPresent()){
            return new ResponseEntity("Não foi possível realizar a consulta. Usuário não encontrado para o Id informado!", HttpStatus.BAD_REQUEST);
        } else {
            lancamentoFiltro.setUsuario(usuario.get());
        }

        var lancamentos = service.buscar(lancamentoFiltro);
        return ResponseEntity.ok(lancamentos);

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

        if(dto.getTipo() != null) {
            lancamento.setTipo(TipoLancamento.valueOf(dto.getTipo()));
        }

        if (dto.getStatus() != null) {
            lancamento.setStatus(StatusLancamento.valueOf(dto.getStatus()));
        }

        return lancamento;
    }
}
