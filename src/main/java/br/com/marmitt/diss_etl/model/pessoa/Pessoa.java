package br.com.marmitt.diss_etl.model.pessoa;

import br.com.marmitt.diss_etl.model.IModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;


public class Pessoa implements IModel {
    private BigDecimal cdCpf;
    private List<BigDecimal> cdPessoa;
    private int cdJornal;
    private String nmPessoa;
    private String sexo;
    private Date dtNasc;
    private String idEstadoCivil;
    private String idEmail;
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

    @Override
    public void setResultSet(ResultSet resultSet) throws SQLException {
        Gson gson = new Gson();
        cdCpf = resultSet.getBigDecimal("CD_CPF");
        try {
            cdPessoa = gson.fromJson(resultSet.getString("CD_PESSOA"), new TypeToken<ArrayList<BigDecimal>>() {
            }.getType());
        }catch (Exception e){
            cdPessoa = new ArrayList<BigDecimal>(){{ add(resultSet.getBigDecimal("CD_PESSOA")); }};
        }
        cdJornal = resultSet.getInt("CD_JORNAL");
        nmPessoa = resultSet.getString("NM_PESSOA");
        sexo = resultSet.getString("SEXO");
        dtNasc = resultSet.getDate("DT_NASC");
        idEstadoCivil = resultSet.getString("ID_ESTADO_CIVIL");
        idEmail = resultSet.getString("ID_EMAIL");
        dtInclusao = resultSet.getDate("DT_INCLUSAO");
        dtAlteracao = resultSet.getDate("DT_ALTERACAO");
    }
}
