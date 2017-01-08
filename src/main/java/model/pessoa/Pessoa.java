package model.pessoa;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class Pessoa {
    private BigDecimal cdCpf;
    private List<BigDecimal> cdPessoa;
    private int cdJornal;
    private String nmPessoa;
    private String sexo;
    private Date dtNasc;
    private String idEstadoCivil;
    private String idEmail;
    private BigDecimal cep;
    private String uf;
    private String cidade;
    private String bairro;
    private String tipoLogradouro;
    private String logradouro;
    private String numero;
    private String complemento;
    private Date dtInclusao;
    private Date dtAlteracao;

    public BigDecimal getCdCpf() {
        return cdCpf;
    }

    public Pessoa setCdCpf(BigDecimal cdCpf) {
        this.cdCpf = cdCpf;
        return this;
    }

    public List<BigDecimal> getCdPessoa() {
        return cdPessoa;
    }

    public Pessoa setCdPessoa(List<BigDecimal> cdPessoa) {
        this.cdPessoa = cdPessoa;
        return this;
    }

    public int getCdJornal() {
        return cdJornal;
    }

    public Pessoa setCdJornal(int cdJornal) {
        this.cdJornal = cdJornal;
        return this;
    }

    public String getNmPessoa() {
        return nmPessoa;
    }

    public Pessoa setNmPessoa(String nmPessoa) {
        this.nmPessoa = nmPessoa;
        return this;
    }

    public String getSexo() {
        return sexo;
    }

    public Pessoa setSexo(String sexo) {
        this.sexo = sexo;
        return this;
    }

    public Date getDtNasc() {
        return dtNasc;
    }

    public Pessoa setDtNasc(Date dtNasc) {
        this.dtNasc = dtNasc;
        return this;
    }

    public String getIdEstadoCivil() {
        return idEstadoCivil;
    }

    public Pessoa setIdEstadoCivil(String idEstadoCivil) {
        this.idEstadoCivil = idEstadoCivil;
        return this;
    }

    public String getIdEmail() {
        return idEmail;
    }

    public Pessoa setIdEmail(String idEmail) {
        this.idEmail = idEmail;
        return this;
    }

    public BigDecimal getCep() {
        return cep;
    }

    public Pessoa setCep(BigDecimal cep) {
        this.cep = cep;
        return this;
    }

    public String getUf() {
        return uf;
    }

    public Pessoa setUf(String uf) {
        this.uf = uf;
        return this;
    }

    public String getCidade() {
        return cidade;
    }

    public Pessoa setCidade(String cidade) {
        this.cidade = cidade;
        return this;
    }

    public String getBairro() {
        return bairro;
    }

    public Pessoa setBairro(String bairro) {
        this.bairro = bairro;
        return this;
    }

    public String getTipoLogradouro() {
        return tipoLogradouro;
    }

    public Pessoa setTipoLogradouro(String tipoLogradouro) {
        this.tipoLogradouro = tipoLogradouro;
        return this;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public Pessoa setLogradouro(String logradouro) {
        this.logradouro = logradouro;
        return this;
    }

    public String getNumero() {
        return numero;
    }

    public Pessoa setNumero(String numero) {
        this.numero = numero;
        return this;
    }

    public String getComplemento() {
        return complemento;
    }

    public Pessoa setComplemento(String complemento) {
        this.complemento = complemento;
        return this;
    }

    public Date getDtInclusao() {
        return dtInclusao;
    }

    public Pessoa setDtInclusao(Date dtInclusao) {
        this.dtInclusao = dtInclusao;
        return this;
    }

    public Date getDtAlteracao() {
        return dtAlteracao;
    }

    public Pessoa setDtAlteracao(Date dtAlteracao) {
        this.dtAlteracao = dtAlteracao;
        return this;
    }
}
