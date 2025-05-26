package com.taxa.deslocamento.dto;

public class EnderecoRequest {
    private String cep;
    private String rua;
    private String numero;
    private String cidade;

    public String getCep() { return cep; }
    public String getRua() { return rua; }
    public String getNumero() { return numero; }
    public String getCidade() { return cidade; }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public void setRua(String rua) {
        this.rua = rua;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }
}
