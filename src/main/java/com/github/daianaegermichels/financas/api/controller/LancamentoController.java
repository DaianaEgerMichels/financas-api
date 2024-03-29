package com.github.daianaegermichels.financas.api.controller;

import com.github.daianaegermichels.financas.dto.AtualizaStatusDTO;
import com.github.daianaegermichels.financas.dto.LancamentoDTO;
import com.github.daianaegermichels.financas.enuns.StatusLancamento;
import com.github.daianaegermichels.financas.enuns.TipoLancamento;
import com.github.daianaegermichels.financas.exception.RegraNegocioException;
import com.github.daianaegermichels.financas.model.Lancamento;
import com.github.daianaegermichels.financas.service.LancamentoService;
import com.github.daianaegermichels.financas.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

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

            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(lancamento.getId()).toUri();
            return ResponseEntity.created(location).body(lancamento);
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

    @PutMapping("{id}/atualiza-status")
    public ResponseEntity atualizarStatus(@PathVariable("id") Long id, @RequestBody AtualizaStatusDTO dto){
        return service.obterPorId(id).map( entity -> {
            var statusSelecionado = StatusLancamento.valueOf(dto.getStatus());
            if(statusSelecionado == null){
                return ResponseEntity.badRequest().body("Status inválido, não foi possível atualizar. Informe um status válido!");
            }
            try{
                entity.setStatus(statusSelecionado);
                service.atualizar(entity);
                return ResponseEntity.ok(entity);
            } catch (RegraNegocioException e)
            {
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
        if(!usuario.isPresent()){
            return new ResponseEntity("Não foi possível realizar a consulta. Usuário não encontrado para o Id informado!", HttpStatus.BAD_REQUEST);
        } else {
            lancamentoFiltro.setUsuario(usuario.get());
        }

        var lancamentos = service.buscar(lancamentoFiltro);
        return ResponseEntity.ok(lancamentos);

    }

    @GetMapping("{id_lancamento}")
    public ResponseEntity obterLancamentoPorId (@PathVariable (value = "id_lancamento") Long idLancamento){
        return service.obterPorId(idLancamento).map(lancamento -> new ResponseEntity(converterParaDTO(lancamento), HttpStatus.OK))
                .orElseGet(()-> new ResponseEntity(HttpStatus.NOT_FOUND));
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

    private LancamentoDTO converterParaDTO(Lancamento lancamento){
        return LancamentoDTO.builder()
                .id(lancamento.getId())
                .descricao(lancamento.getDescricao())
                .valor(lancamento.getValor())
                .mes(lancamento.getMes())
                .ano(lancamento.getAno())
                .status(lancamento.getStatus().name())
                .tipo(lancamento.getTipo().name())
                .usuario(lancamento.getUsuario().getId())
                .build();
    }
}
