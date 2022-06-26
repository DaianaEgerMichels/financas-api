package com.github.daianaegermichels.financas.api.controller;

import com.github.daianaegermichels.financas.dto.LancamentoDTO;
import com.github.daianaegermichels.financas.service.LancamentoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/lancamentos")
public class LancamentoController {

    private LancamentoService service;

    public LancamentoController(LancamentoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity salvar (@RequestBody LancamentoDTO dto){

    }
}
