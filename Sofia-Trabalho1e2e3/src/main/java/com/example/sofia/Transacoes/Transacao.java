package com.example.sofia.Transacoes;

import java.time.ZonedDateTime;

public class Transacao {
    private double valor;
    private ZonedDateTime dataHora;

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public ZonedDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(ZonedDateTime dataHora) {
        this.dataHora = dataHora;
    }

    }