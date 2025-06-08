package com.taxa.deslocamento.dto;

public class CalculoResponse {
    private double distanciaKm;
    private double taxa;
    private double tempoMinutos;

    public CalculoResponse(double distanciaKm, double taxa, double tempoMinutos) {
        this.distanciaKm = distanciaKm;
        this.taxa = taxa;
        this.tempoMinutos = tempoMinutos;
    }

    // Getters
    public double getDistanciaKm() { return distanciaKm; }
    public double getTaxa() { return taxa; }
    public double getTempoMinutos() { return tempoMinutos; }

    public void setDistanciaKm(double distanciaKm) {
        this.distanciaKm = distanciaKm;
    }

    public void setTaxa(double taxa) {
        this.taxa = taxa;
    }

    public void setTempoMinutos(double tempoMinutos) {
        this.tempoMinutos = tempoMinutos;
    }
}
