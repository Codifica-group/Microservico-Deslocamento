package com.taxa.deslocamento.dto;

public class CalculoResponse {
    private double distanciaKm;
    private double taxa;
    private double tempoHoras;

    public CalculoResponse(double distanciaKm, double taxa, double tempoHoras) {
        this.distanciaKm = distanciaKm;
        this.taxa = taxa;
        this.tempoHoras = tempoHoras;
    }

    // Getters
    public double getDistanciaKm() { return distanciaKm; }
    public double getTaxa() { return taxa; }
    public double getTempoHoras() { return tempoHoras; }
}
