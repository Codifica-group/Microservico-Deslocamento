# 🐾 Sistema de Cálculo de Deslocamento - Petshop

Este projeto é um microserviço desenvolvido em **Java com Spring Boot** que realiza o **cálculo automático da taxa de deslocamento** de um petshop até a casa do cliente, utilizando uma API gratuita de geocodificação e rotas.

## 🚀 Objetivo

Facilitar o cálculo de taxas de deslocamento para serviços domiciliares prestados pelo proprietário do petshop, tornando o processo automatizado e livre de erros manuais.

## 🛠 Tecnologias Utilizadas

- Java 22
- Spring Boot 3.4.5
- Spring Web
- Spring Data JPA
- H2 Database (para testes locais)
- Lombok
- API de geocodificação gratuita (OpenRouteService)

## 📌 Funcionalidades

- Busca de coordenadas (latitude e longitude) a partir de endereços usando a API externa
- Cálculo da distância e tempo entre o petshop e o cliente usando a API externa
- Cálculo do valor da taxa de deslocamento com base na distância (ex: R$ 2,00 por km)
- Exposição de endpoint REST para o frontend React

## 🔗 Exemplo de Requisição

**Endpoint:**
```
POST /deslocamento
```

**Request Body:**
```json
{
  "rua": "Avenida Paulista",
  "numero": "1000",
  "cidade": "São Paulo",
  "cep": "01310-100"
}
```

**Response:**
```json
{
  "distanciaKm": 3.9925,
  "taxa": 7.985,
  "tempoHoras": 0.13777777777777778
}
```
## 📍 Observações

- Para que o cálculo de distância funcione corretamente, é necessário obter uma chave da API da [OpenRouteService](https://openrouteservice.org/).
- Após obter a chave, substitua o valor da constante `ORS_API_KEY` localizada na classe `DeslocamentoService.java` pelo valor da sua chave.
