package com.example.sofia.Transacoes;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class TransacaoService {
    
    private final List<Transacao> transacoes = new ArrayList<>();

    public void adicionar(Transacao t) {
        transacoes.add(t);
    }

    public void limpar() {
        transacoes.clear();
    }

    public Transacao ultimaTransacao() {
        if (transacoes.isEmpty())
            return null;
        return transacoes.get(transacoes.size() - 1);
    }

    public List<Transacao> ultimos60Segundos() {
        ZonedDateTime agora = ZonedDateTime.now();
        return transacoes.stream()
                .filter(t -> t.getDataHora().isAfter(agora.minusSeconds(60)))
                .collect(Collectors.toList());
    }

    public List<Transacao> porPeriodo(ZonedDateTime inicio, ZonedDateTime fim) {
        return transacoes.stream()
                .filter(t -> !t.getDataHora().isBefore(inicio) && !t.getDataHora().isAfter(fim))
                .collect(Collectors.toList());
    }

    public void excluirPeriodo(ZonedDateTime inicio, ZonedDateTime fim) {
        transacoes.removeIf(t -> !t.getDataHora().isBefore(inicio) && !t.getDataHora().isAfter(fim));
    }
}

