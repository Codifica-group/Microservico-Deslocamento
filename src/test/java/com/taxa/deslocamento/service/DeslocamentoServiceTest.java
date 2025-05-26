package com.taxa.deslocamento.service;

import com.taxa.deslocamento.dto.CalculoResponse;
import com.taxa.deslocamento.dto.EnderecoRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DeslocamentoServiceTest {

    private final RestTemplate restTemplate = mock(RestTemplate.class);
    private final DeslocamentoService deslocamentoService = Mockito.spy(new DeslocamentoService());

    @Test
    void testCalcularDistanciaETaxaComSucesso() {
        EnderecoRequest request = new EnderecoRequest();
        request.setRua("Rua Teste");
        request.setNumero("123");
        request.setCidade("São Paulo");
        request.setCep("01001-000");

        Mockito.doReturn(new double[]{-46.6333, -23.5505})
                .when(deslocamentoService).buscarCoordenadas(request);

        DeslocamentoService.DistanciaETempo distanciaETempo =
                new DeslocamentoService.DistanciaETempo(5.0, 0.5);
        Mockito.doReturn(distanciaETempo)
                .when(deslocamentoService).calcularDistanciaETempo(-23.5505, -46.6333);

        CalculoResponse response = deslocamentoService.calcularDistanciaETaxa(request);

        assertNotNull(response);
        assertEquals(5.0, response.getDistanciaKm());
        assertEquals(10.0, response.getTaxa());
        assertEquals(0.5, response.getTempoHoras());
    }

    @Test
    void testCalcularDistanciaETaxaErroAoBuscarCoordenadas() {
        EnderecoRequest request = new EnderecoRequest();
        request.setRua("Rua Teste");
        request.setNumero("123");
        request.setCidade("São Paulo");
        request.setCep("01001-000");

        Mockito.doThrow(new RuntimeException("Erro ao buscar coordenadas"))
                .when(deslocamentoService).buscarCoordenadas(request);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            deslocamentoService.calcularDistanciaETaxa(request);
        });

        assertEquals("Erro ao calcular: Erro ao buscar coordenadas", exception.getMessage());
    }

    @Test
    void testCalcularDistanciaETaxaErroAoCalcularDistancia() {
        EnderecoRequest request = new EnderecoRequest();
        request.setRua("Rua Teste");
        request.setNumero("123");
        request.setCidade("São Paulo");
        request.setCep("01001-000");

        Mockito.doReturn(new double[]{-46.6333, -23.5505})
                .when(deslocamentoService).buscarCoordenadas(request);

        Mockito.doThrow(new RuntimeException("Erro ao calcular distância e tempo"))
                .when(deslocamentoService).calcularDistanciaETempo(-23.5505, -46.6333);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            deslocamentoService.calcularDistanciaETaxa(request);
        });

        assertEquals("Erro ao calcular: Erro ao calcular distância e tempo", exception.getMessage());
    }
}