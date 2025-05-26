package com.taxa.deslocamento.controller;

import com.taxa.deslocamento.dto.CalculoResponse;
import com.taxa.deslocamento.dto.EnderecoRequest;
import com.taxa.deslocamento.exception.APIIntegrationException;
import com.taxa.deslocamento.service.DeslocamentoService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(DeslocamentoController.class)
class DeslocamentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DeslocamentoService deslocamentoService;

    @Test
    void testCalcularComSucesso() throws Exception {
        EnderecoRequest request = new EnderecoRequest();
        request.setRua("Rua Teste");
        request.setNumero("123");
        request.setCidade("São Paulo");
        request.setCep("01001-000");

        CalculoResponse response = new CalculoResponse(5.0, 10.0, 0.5);

        Mockito.when(deslocamentoService.calcularDistanciaETaxa(Mockito.any()))
                .thenReturn(response);

        mockMvc.perform(post("/deslocamento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cep\":\"01001-000\",\"rua\":\"Rua Teste\",\"numero\":\"123\",\"cidade\":\"São Paulo\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.distanciaKm").value(5.0))
                .andExpect(jsonPath("$.taxa").value(10.0))
                .andExpect(jsonPath("$.tempoHoras").value(0.5));
    }

    @Test
    void testCalcularComErro() throws Exception {
        Mockito.when(deslocamentoService.calcularDistanciaETaxa(Mockito.any()))
                .thenThrow(new APIIntegrationException("Erro ao calcular distância e taxa"));

        mockMvc.perform(post("/deslocamento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cep\":\"01001-000\",\"rua\":\"Rua Teste\",\"numero\":\"123\",\"cidade\":\"São Paulo\"}"))
                .andExpect(status().isServiceUnavailable()) // Status 503 para APIIntegrationException
                .andExpect(jsonPath("$.error").value("SERVICE_UNAVAILABLE"))
                .andExpect(jsonPath("$.message").value("Erro ao calcular distância e taxa"));
    }
}