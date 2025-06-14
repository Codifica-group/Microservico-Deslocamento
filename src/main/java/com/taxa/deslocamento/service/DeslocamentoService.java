package com.taxa.deslocamento.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.taxa.deslocamento.dto.CalculoResponse;
import com.taxa.deslocamento.dto.EnderecoRequest;
import com.taxa.deslocamento.exception.APIIntegrationException;
import com.taxa.deslocamento.exception.CoordenadasNotFoundException;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Locale;

@Service
public class DeslocamentoService {
    private static final String ORS_API_KEY = System.getenv("ORS_API_KEY"); //Adicione a chave da OpenRouteService
    private static final double LAT_PETSHOP = -23.551859924742757;
    private static final double LON_PETSHOP = -46.76177857372966;

    private final RestTemplate restTemplate = new RestTemplate();

    public CalculoResponse calcularDistanciaETaxa(EnderecoRequest request) {
        try {
            double[] coordsCliente = buscarCoordenadas(request);
            DistanciaETempo distanciaETempo = calcularDistanciaETempo(coordsCliente[1], coordsCliente[0]);
            double taxa = calcularTaxa(distanciaETempo.distanciaKm);
            return new CalculoResponse(distanciaETempo.distanciaKm, taxa, distanciaETempo.tempoMinutos);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao calcular: " + e.getMessage());
        }
    }

    public double[] buscarCoordenadas(EnderecoRequest request) {
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
        } catch (HttpStatusCodeException e) {
            throw new APIIntegrationException("Erro ao buscar coordenadas: " + e.getMessage());
        } catch (Exception e) {
            throw new CoordenadasNotFoundException("Não foi possível obter coordenadas para o endereço informado.");
        }
    }

    public DistanciaETempo calcularDistanciaETempo(double latCliente, double lonCliente) {
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
                double tempoMinutos = tempoSegundos / 60.0;

                System.out.println("Distância: " + distanciaKm + " km");
                System.out.println("Tempo estimado: " + tempoMinutos + " minutos");

                return new DistanciaETempo(distanciaKm, tempoMinutos);

            } catch (HttpStatusCodeException e) {
                HttpStatusCode status = e.getStatusCode();

                if (status == HttpStatus.SERVICE_UNAVAILABLE || status == HttpStatus.GATEWAY_TIMEOUT) {
                    tentativas++;
                    esperar(1000);  // Espera 1 segundo entre tentativas
                } else if (status == HttpStatus.FORBIDDEN) {
                    throw new APIIntegrationException("Erro: acesso negado à API (403). Verifique sua chave.");
                } else if (status == HttpStatus.NOT_ACCEPTABLE) {
                    throw new APIIntegrationException("Erro: formato de resposta inválido (406). Verifique o header 'Accept'.");
                } else {
                    throw new APIIntegrationException("Erro ao consultar distância: "
                            + status + " - " + e.getResponseBodyAsString());
                }
            } catch (Exception e) {
                throw new APIIntegrationException("Erro inesperado ao consultar distância: " + e.getMessage());
            }
        }
        throw new APIIntegrationException("Falha após múltiplas tentativas ao calcular distância.");
    }

    private void esperar(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {
        }
    }

    private double calcularTaxa(double distanciaKm) {
        Double precoGasolina = 6.2;
        Double kmPorLitro = 12.0;
        Double custoCombustivel = (distanciaKm / kmPorLitro) * precoGasolina;

        Double margemLucro = 1.5;
        if (distanciaKm >= 3 && distanciaKm < 5) {
            margemLucro = 1.8;
        } else if (distanciaKm >= 5) {
            margemLucro = 2.0;
        }

        return (custoCombustivel * margemLucro) * 2;
    }

    // Classe para representar Distância e Tempo
    public static class DistanciaETempo {
        double distanciaKm;
        double tempoMinutos;

        public DistanciaETempo(double distanciaKm, double tempoMinutos) {
            this.distanciaKm = distanciaKm;
            this.tempoMinutos = tempoMinutos;
        }
    }
}
