package br.com.marmitt.diss_etl.model.pessoa;

import br.com.marmitt.diss_etl.model.IModel;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Stats implements IModel{
    private Pessoa pessoa;
    private BigDecimal qtdAss;
    private BigDecimal qtdAssAtiva;
    private BigDecimal totalPago;
    private BigDecimal frequencia;
    private BigDecimal totalPago3;
    private BigDecimal frequencia3;
    private BigDecimal totalPago12;
    private BigDecimal frequencia12;
    private BigDecimal recencia;
    private BigDecimal longevidade;

    public Pessoa getPessoa() {
        return pessoa;
    }

    public Stats setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
        return this;
    }

    public BigDecimal getQtdAss() {
        return qtdAss;
    }

    public Stats setQtdAss(BigDecimal qtdAss) {
        this.qtdAss = qtdAss;
        return this;
    }

    public BigDecimal getQtdAssAtiva() {
        return qtdAssAtiva;
    }

    public Stats setQtdAssAtiva(BigDecimal qtdAssAtiva) {
        this.qtdAssAtiva = qtdAssAtiva;
        return this;
    }

    public BigDecimal getTotalPago() {
        return totalPago;
    }

    public Stats setTotalPago(BigDecimal totalPago) {
        this.totalPago = totalPago;
        return this;
    }

    public BigDecimal getFrequencia() {
        return frequencia;
    }

    public Stats setFrequencia(BigDecimal frequencia) {
        this.frequencia = frequencia;
        return this;
    }

    public BigDecimal getTotalPago3() {
        return totalPago3;
    }

    public Stats setTotalPago3(BigDecimal totalPago3) {
        this.totalPago3 = totalPago3;
        return this;
    }

    public BigDecimal getFrequencia3() {
        return frequencia3;
    }

    public Stats setFrequencia3(BigDecimal frequencia3) {
        this.frequencia3 = frequencia3;
        return this;
    }

    public BigDecimal getTotalPago12() {
        return totalPago12;
    }

    public Stats setTotalPago12(BigDecimal totalPago12) {
        this.totalPago12 = totalPago12;
        return this;
    }

    public BigDecimal getFrequencia12() {
        return frequencia12;
    }

    public Stats setFrequencia12(BigDecimal frequencia12) {
        this.frequencia12 = frequencia12;
        return this;
    }

    public BigDecimal getRecencia() {
        return recencia;
    }

    public Stats setRecencia(BigDecimal recencia) {
        this.recencia = recencia;
        return this;
    }

    public BigDecimal getLongevidade() {
        return longevidade;
    }

    public Stats setLongevidade(BigDecimal longevidade) {
        this.longevidade = longevidade;
        return this;
    }

    @Override
    public void setResultSet(ResultSet resultSet) throws SQLException {
        qtdAss = resultSet.getBigDecimal("QTD_ASS");
        qtdAssAtiva = resultSet.getBigDecimal("QTD_ASS_ATIVA");
        totalPago = resultSet.getBigDecimal("TOTAL_PAGO");
        frequencia = resultSet.getBigDecimal("FREQUENCIA");
        totalPago3 = resultSet.getBigDecimal("TOTAL_PAGO_3");
        frequencia3 = resultSet.getBigDecimal("FREQUENCIA_3");
        totalPago12 = resultSet.getBigDecimal("TOTAL_PAGO_12");
        frequencia12 = resultSet.getBigDecimal("FREQUENCIA_12");
        recencia = resultSet.getBigDecimal("RECENCIA");
        longevidade = resultSet.getBigDecimal("LONGEVIDADE");
    }
}
