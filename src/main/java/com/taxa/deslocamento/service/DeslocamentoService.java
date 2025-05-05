package com.taxa.deslocamento.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.taxa.deslocamento.dto.CalculoResponse;
import com.taxa.deslocamento.dto.EnderecoRequest;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Locale;

@Service
public class DeslocamentoService {
    private static final String ORS_API_KEY = "SUA_CHAVE_API_AQUI"; // Substitua pela sua chave da OpenRouteService
    private static final double LAT_PETSHOP = -23.5505;
    private static final double LON_PETSHOP = -46.6333;

    private final RestTemplate restTemplate = new RestTemplate();

    public CalculoResponse calcularDistanciaETaxa(EnderecoRequest request) {
        try {
            double[] coordsCliente = buscarCoordenadas(request);
            DistanciaETempo distanciaETempo = calcularDistanciaETempo(coordsCliente[1], coordsCliente[0]);
            double taxa = calcularTaxa(distanciaETempo.distanciaKm);
            return new CalculoResponse(distanciaETempo.distanciaKm, taxa, distanciaETempo.tempoHoras);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao calcular: " + e.getMessage());
        }
    }

    private double[] buscarCoordenadas(EnderecoRequest request) {
        String enderecoFormatado = String.format("%s %s, %s, SP, %s, Brazil",
                request.getRua(), request.getNumero(), request.getCidade(), request.getCep());

        String url = UriComponentsBuilder.fromHttpUrl("https://api.openrouteservice.org/geocode/search")
                .queryParam("api_key", ORS_API_KEY)
                .queryParam("text", enderecoFormatado)
                .build().toUriString();

        try {
            JsonNode body = restTemplate.getForObject(url, JsonNode.class);
            JsonNode coords = body.get("features").get(0).get("geometry").get("coordinates");
            System.out.println("Coordenadas: " + coords);
            return new double[]{coords.get(0).asDouble(), coords.get(1).asDouble()};
        } catch (Exception e) {
            throw new RuntimeException("Não foi possível obter coordenadas para o endereço: " + e.getMessage());
        }
    }

    private DistanciaETempo calcularDistanciaETempo(double latCliente, double lonCliente) {
        String url = String.format(Locale.US,
                "https://api.openrouteservice.org/v2/directions/driving-car?api_key=%s&start=%f,%f&end=%f,%f",
                ORS_API_KEY, lonCliente, latCliente, LON_PETSHOP, LAT_PETSHOP);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/geo+json;charset=UTF-8");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        int tentativas = 0;
        while (tentativas < 3) {
            try {
                ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.GET, entity, JsonNode.class);
                JsonNode body = response.getBody();
                double distanciaMetros = body.get("features").get(0)
                        .get("properties").get("segments").get(0)
                        .get("distance").asDouble();
                long tempoSegundos = body.get("features").get(0)
                        .get("properties").get("segments").get(0)
                        .get("duration").asLong();

                double distanciaKm = distanciaMetros / 1000;
                double tempoHoras = tempoSegundos / 3600.0;

                System.out.println("Distância: " + distanciaKm + " km");
                System.out.println("Tempo estimado: " + tempoHoras + " horas");

                return new DistanciaETempo(distanciaKm, tempoHoras);

            } catch (HttpStatusCodeException e) {
                HttpStatusCode status = e.getStatusCode();

                if (status == HttpStatus.SERVICE_UNAVAILABLE || status == HttpStatus.GATEWAY_TIMEOUT) {
                    tentativas++;
                    esperar(1000);  // Espera 1 segundo entre tentativas
                } else if (status == HttpStatus.FORBIDDEN) {
                    throw new RuntimeException("Erro: acesso negado à API (403). Verifique sua chave.");
                } else if (status == HttpStatus.NOT_ACCEPTABLE) {
                    throw new RuntimeException("Erro: formato de resposta inválido (406). Verifique o header 'Accept'.");
                } else {
                    throw new RuntimeException("Erro ao consultar distância: "
                            + status + " - " + e.getResponseBodyAsString());
                }
            } catch (Exception e) {
                throw new RuntimeException("Erro inesperado ao consultar distância: " + e.getMessage());
            }
        }
        throw new RuntimeException("Falha após múltiplas tentativas ao calcular distância.");
    }

    private void esperar(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {
        }
    }

    private double calcularTaxa(double distanciaKm) {
        if (distanciaKm <= 3) return 10;
        if (distanciaKm <= 10) return distanciaKm * 2;
        return distanciaKm * 3;
    }

    // Classe para representar Distância e Tempo
    private static class DistanciaETempo {
        double distanciaKm;
        double tempoHoras;

        public DistanciaETempo(double distanciaKm, double tempoHoras) {
            this.distanciaKm = distanciaKm;
            this.tempoHoras = tempoHoras;
        }
    }
}
