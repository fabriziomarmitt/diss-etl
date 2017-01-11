package br.com.marmitt.diss_etl.model.pessoa;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Endereco {
    private String cep;
    private String uf;
    private String cidade;
    private String bairro;
    private String tipoLogradouro;
    private String logradouro;
    private String numero;
    private String complemento;

    public Endereco(ResultSet resultSet) throws SQLException {
        cep = resultSet.getString("CEP");
        uf = resultSet.getString("UF");
        cidade = resultSet.getString("CIDADE");
        bairro = resultSet.getString("BAIRRO");
        tipoLogradouro = resultSet.getString("TIPO_LOGRADOURO");
        logradouro = resultSet.getString("LOGRADOURO");
        numero = resultSet.getString("NUMERO");
        complemento = resultSet.getString("COMPLEMENTO");
    }

    public String getCep() {
        return cep;
    }

    public Endereco setCep(String cep) {
        this.cep = cep;
        return this;
    }

    public String getUf() {
        return uf;
    }

    public Endereco setUf(String uf) {
        this.uf = uf;
        return this;
    }

    public String getCidade() {
        return cidade;
    }

    public Endereco setCidade(String cidade) {
        this.cidade = cidade;
        return this;
    }

    public String getBairro() {
        return bairro;
    }

    public Endereco setBairro(String bairro) {
        this.bairro = bairro;
        return this;
    }

    public String getTipoLogradouro() {
        return tipoLogradouro;
    }

    public Endereco setTipoLogradouro(String tipoLogradouro) {
        this.tipoLogradouro = tipoLogradouro;
        return this;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public Endereco setLogradouro(String logradouro) {
        this.logradouro = logradouro;
        return this;
    }

    public String getNumero() {
        return numero;
    }

    public Endereco setNumero(String numero) {
        this.numero = numero;
        return this;
    }

    public String getComplemento() {
        return complemento;
    }

    public Endereco setComplemento(String complemento) {
        this.complemento = complemento;
        return this;
    }
}
