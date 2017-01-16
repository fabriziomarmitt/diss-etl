package br.com.marmitt.diss_etl.model.pessoa;

import br.com.marmitt.diss_etl.model.IModel;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TmpCpf implements IModel {
    private BigDecimal cpf;
    @Override
    public void setResultSet(ResultSet resultSet) throws SQLException {
        cpf = resultSet.getBigDecimal("cpf");
    }

    public BigDecimal getCpf() {
        return cpf;
    }

    public TmpCpf setCpf(BigDecimal cpf) {
        this.cpf = cpf;
        return this;
    }
}
