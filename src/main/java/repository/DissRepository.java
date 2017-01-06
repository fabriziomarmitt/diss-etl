package repository;

import org.apache.commons.dbutils.ResultSetIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;


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

    public Iterator<Object[]> getAllTmpCpf() {
        return new ResultSetIterator(repository.query("SELECT cpf FROM tmp_cpf"));
    }
}
