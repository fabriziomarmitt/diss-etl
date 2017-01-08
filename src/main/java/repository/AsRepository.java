package repository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLDataException;
import java.sql.SQLException;

public class AsRepository extends AbstractRepository{

    public Repository.Result getPessoas() throws SQLDataException {
        return repository.query("SELECT * FROM pessoa");
    }

    public Repository.Result getPessoasUniqueCpf() throws SQLDataException {
        String query = "SELECT pess_cd_cpf "
                     + " FROM pessoa p "
                     + " WHERE p.pess_id_empresa = 'N'"
                     + " AND p.pess_cd_jornal = 1"
                     + " AND p.pess_cd_cpf IS NOT NULL"
                     + " GROUP BY p.pess_cd_cpf";
        return repository.query(query);
    }

    public Repository.Result getPessoaByCpf(BigDecimal cpf) throws SQLException {
        Connection connection = repository.getConnection();
        PreparedStatement preparedStatement
                = connection.prepareStatement("SELECT * FROM pessoa p WHERE p.pess_cd_cpf = ?");
        preparedStatement.setBigDecimal(1, cpf);
        return repository.preparedQuery(preparedStatement);
    }
}
