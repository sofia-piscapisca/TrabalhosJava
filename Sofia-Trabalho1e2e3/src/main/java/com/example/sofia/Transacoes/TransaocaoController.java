package com.example.sofia.Transacoes;

import java.time.ZonedDateTime;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transacao")
public class TransaocaoController {

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

    @DeleteMapping
    public ResponseEntity<?> limparTransacoes() {
        transacaoService.limpar();
        return ResponseEntity.ok().build();

    }

    @GetMapping("/estatisca")
    public ResponseEntity<Map<String, Object>> calcularEstatisca() {
        List<Transacao> results = transacaoService.ultimos60Segundos();

        Map<String, Object> estatisticas = new HashMap<>();

        if (results.isEmpty()) {
            estatisticas.put("count", 0);
            estatisticas.put("sum", 0.0);
            estatisticas.put("avg", 0.0);
            estatisticas.put("min", 0.0);
            estatisticas.put("max", 0.0);
            return ResponseEntity.ok(estatisticas);
        }

        DoubleSummaryStatistics stats = results.stream()
                .mapToDouble(t -> t.getValor())
                .summaryStatistics();

        estatisticas.put("count", stats.getCount());
        estatisticas.put("sum", stats.getSum());
        estatisticas.put("avg", stats.getAverage());
        estatisticas.put("min", stats.getMin());
        estatisticas.put("max", stats.getMax());

        return ResponseEntity.ok(estatisticas);
    }

    @PostMapping("/periodo")
    public ResponseEntity<?> consultarPeriodo(
        @RequestParam("inicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) ZonedDateTime inicio,
        @RequestParam("fim") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) ZonedDateTime fim) {

        List<Transacao> results = transacaoService.porPeriodo(inicio, fim);

        if (results.isEmpty()) {
            Map<String, Object> emptyStats = new HashMap<>();
            emptyStats.put("count", 0);
            emptyStats.put("sum", 0.0);
            emptyStats.put("avg", 0.0);
            emptyStats.put("min", 0.0);
            emptyStats.put("max", 0.0);
            return ResponseEntity.ok(emptyStats);
        }

        DoubleSummaryStatistics stats = results.stream()
            .mapToDouble(Transacao::getValor) 
            .summaryStatistics();

        Map<String, Object> response = new HashMap<>();
        response.put("count", stats.getCount());
        response.put("sum", stats.getSum());
        response.put("avg", stats.getAverage());
        response.put("min", stats.getMin());
        response.put("max", stats.getMax());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/periodo")
    public ResponseEntity<?> excluirPeriodo(@RequestParam("inicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) ZonedDateTime inicio, @RequestParam("fim") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) ZonedDateTime fim) {

    boolean removido = transacaoService.excluirPeriodo(inicio, fim);

    if (!removido) {
        return ResponseEntity.noContent().build(); 
    }

    return ResponseEntity.ok().build(); 
    }
}
