package repository;

import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.ResultSetIterator;
import org.apache.commons.dbutils.RowProcessor;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.*;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AsRepository extends AbstractRepository{

    public ResultSet getPessoas() throws SQLDataException {
        return repository.query("SELECT * FROM pessoa");
    }

    public ResultSet getPessoasUniqueCpf() throws SQLDataException {
        String query = "SELECT pess_cd_cpf "
                     + " FROM pessoa p "
                     + " WHERE p.pess_id_empresa = 'N'"
                     + " AND p.pess_cd_cpf IS NOT NULL"
                     + " GROUP BY p.pess_cd_cpf";
        return repository.query(query);
    }

    public Iterator<Object[]> getPessoaByCpf(BigDecimal cpf) throws SQLException {
        Connection connection = repository.getConnection();
        PreparedStatement preparedStatement
                = connection.prepareStatement("SELECT * FROM pessoa p WHERE p.pess_cd_cpf = ?");
        preparedStatement.setBigDecimal(1, cpf);
        return new ResultSetIterator(preparedStatement.executeQuery(), new RowProcessor(){

            @Override
            public Object[] toArray(ResultSet rs) throws SQLException {
                return new Object[0];
            }

            @Override
            public <T> T toBean(ResultSet rs, Class<T> type) throws SQLException {
                return null;
            }

            @Override
            public <T> List<T> toBeanList(ResultSet rs, Class<T> type) throws SQLException {
                return null;
            }

            @Override
            public Map<String, Object> toMap(ResultSet rs) throws SQLException {
                return null;
            }
        });
    }
}
