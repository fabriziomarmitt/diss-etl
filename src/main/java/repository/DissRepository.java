package repository;

import com.google.gson.Gson;
import model.pessoa.Pessoa;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class DissRepository extends AbstractRepository{

    public static Logger logger = LoggerFactory.getLogger(DissRepository.class);

    public void insertCpfFromResultSet(ResultSet resultSet) throws SQLException {

        logger.info("Starting inserting CPF");

        PreparedStatement stmt;
        Connection connection = repository.getConnection();

        connection.createStatement().execute("TRUNCATE TABLE tmp_cpf");

        try {
            connection.setAutoCommit(false);
            int batchSize = 1000;
            int count = 0;
            stmt = connection.prepareStatement("INSERT INTO tmp_cpf (cpf) VALUES (?)");

            logger.info("Starting insert into tmp_cpf");

            while(resultSet.next()){
                logger.info("Inserting row " + count);
                BigDecimal cpf = resultSet.getBigDecimal("PESS_CD_CPF");
                if(resultSet.wasNull()){
                    logger.info(String.format("Ignoring %d because is NULL", count));
                    continue;
                }
                stmt.setBigDecimal(1, cpf);
                stmt.addBatch();
                if(++count % batchSize == 0){
                    logger.info("Executing batch");
                   stmt.executeBatch();
                   connection.commit();
                }
            }
            logger.info("Finished loop. Terminating process");
            stmt.executeBatch();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void insertPessoa(Pessoa pessoa){
        logger.info("Starting insert into PESSOA with ID: " + pessoa.getCdCpf());

        PreparedStatement  stmt;
        Connection connection = repository.getConnection();
        try{
            Gson gson = new Gson();
            stmt = connection.prepareStatement("INSERT INTO PESSOA VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            stmt.setBigDecimal(1, pessoa.getCdCpf());
            stmt.setString(2, gson.toJson(pessoa.getCdPessoa()));
            stmt.setInt(2, pessoa.getCdJornal());
            stmt.setString(3, pessoa.getNmPessoa());
            stmt.setString(4, pessoa.getSexo());
            stmt.setDate(5, pessoa.getDtNasc());
            stmt.setString(7, pessoa.getIdEstadoCivil());
            stmt.setString(8, pessoa.getIdEmail());
            stmt.setBigDecimal(9, pessoa.getCep());
            stmt.setString(10, pessoa.getUf());
            stmt.setString(11, pessoa.getCidade());
            stmt.setString(12, pessoa.getBairro());
            stmt.setString(13, pessoa.getTipoLogradouro());
            stmt.setString(14, pessoa.getLogradouro());
            stmt.setString(15, pessoa.getNumero());
            stmt.setString(16, pessoa.getComplemento());
            stmt.setDate(17, pessoa.getDtInclusao());
            stmt.setDate(18, pessoa.getDtAlteracao());

            stmt.execute();
            stmt.close();
            logger.info("PESSOA with ID: " + pessoa.getCdCpf() + " inserted with success");
        } catch (SQLException e) {
            logger.info("Insertion of PESSOA with ID " + pessoa.getCdCpf() + " failed");
            e.printStackTrace();
        }

    }

    public Repository.Result getAllTmpCpf() {
        return repository.query("SELECT cpf FROM tmp_cpf");
    }
}
