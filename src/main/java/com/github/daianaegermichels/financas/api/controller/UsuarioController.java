package com.github.daianaegermichels.financas.api.controller;

import com.github.daianaegermichels.financas.dto.TokenDTO;
import com.github.daianaegermichels.financas.dto.UsuarioDTO;
import com.github.daianaegermichels.financas.exception.ErroAutenticacao;
import com.github.daianaegermichels.financas.exception.RegraNegocioException;
import com.github.daianaegermichels.financas.model.Usuario;
import com.github.daianaegermichels.financas.service.JwtService;
import com.github.daianaegermichels.financas.service.LancamentoService;
import com.github.daianaegermichels.financas.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private UsuarioService usuarioService;

    private LancamentoService lancamentoService;

    private JwtService jwtService;

    public UsuarioController(UsuarioService usuarioService, LancamentoService lancamentoService, JwtService jwtService) {
        this.usuarioService = usuarioService;
        this.lancamentoService = lancamentoService;
        this.jwtService = jwtService;
    }

    @PostMapping("/autenticar")
    public ResponseEntity<?> autenticar(@RequestBody UsuarioDTO usuarioDTO){
        try {
            var usuarioAutenticado= usuarioService.autenticar(usuarioDTO.getEmail(), usuarioDTO.getSenha());
            String token = jwtService.gerarToken(usuarioAutenticado);
            TokenDTO tokenDTO = new TokenDTO(usuarioAutenticado.getNome(), token);
            return ResponseEntity.ok(tokenDTO);
        } catch (ErroAutenticacao e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity salvar(@RequestBody UsuarioDTO usuarioDTO){
        var usuario = Usuario.builder()
                .nome(usuarioDTO.getNome())
                .email(usuarioDTO.getEmail())
                .senha(usuarioDTO.getSenha()).build();

        try{
            var usuarioSalvo = usuarioService.salvarUsuario(usuario);

            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(usuarioSalvo.getId()).toUri();
            return ResponseEntity.created(location).body(usuarioSalvo);
        } catch (RegraNegocioException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("{id}/saldo")
    public ResponseEntity obterSaldo(@PathVariable("id") Long id){
        var usuario = usuarioService.obterPorId(id);
        if(!usuario.isPresent()){
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        var saldo = lancamentoService.obterSaldoPorUsuario(id);
        return ResponseEntity.ok(saldo);
    }
}
