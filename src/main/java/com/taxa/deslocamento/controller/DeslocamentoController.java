package com.taxa.deslocamento.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.taxa.deslocamento.dto.CalculoResponse;
import com.taxa.deslocamento.dto.EnderecoRequest;
import com.taxa.deslocamento.service.DeslocamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/deslocamento")
public class DeslocamentoController {

    @Autowired
    private DeslocamentoService deslocamentoService;

    @PostMapping
    public ResponseEntity<CalculoResponse> calcular(@RequestBody EnderecoRequest request) {
        return ResponseEntity.ok(deslocamentoService.calcularDistanciaETaxa(request));
    }
}
