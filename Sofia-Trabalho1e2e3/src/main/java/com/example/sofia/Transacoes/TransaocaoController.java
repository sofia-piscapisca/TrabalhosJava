package com.example.sofia.Transacoes;

import java.time.ZonedDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transacao")
public class TransaocaoController  {
    
    @Autowired  
    private TransacaoService transacaoService;
     
    @PostMapping
    public ResponseEntity<?> criarTransacao(@RequestBody Transacao transacao) {
        try {
            if (transacao.getValor() < 0)
                return ResponseEntity.unprocessableEntity().build();
            if (transacao.getDataHora() == null || transacao.getDataHora().isAfter(ZonedDateTime.now()))
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();

            transacaoService.adicionar(transacao);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/ultima")
    public ResponseEntity<?> ultimaTransacao() {
        Transacao ultima = transacaoService.ultimaTransacao();
        if (ultima == null)
            return ResponseEntity.noContent().build();
        return ResponseEntity.ok(ultima);
    }
}
