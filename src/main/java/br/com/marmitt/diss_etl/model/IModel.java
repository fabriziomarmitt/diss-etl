package br.com.marmitt.diss_etl.model;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface IModel {
    void setResultSet(ResultSet resultSet) throws SQLException;
}
