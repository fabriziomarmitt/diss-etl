package br.com.marmitt.diss_etl.repository;

import br.com.marmitt.diss_etl.model.pessoa.Endereco;
import br.com.marmitt.diss_etl.model.pessoa.Pessoa;
import br.com.marmitt.diss_etl.model.pessoa.Stats;
import br.com.marmitt.diss_etl.model.pessoa.TmpCpf;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


public class DissRepository extends AbstractRepository{

    public static Logger logger = LoggerFactory.getLogger(DissRepository.class);

    public void insertTmpCpfs(List<TmpCpf> tmpCpfList) {
        logger.info("Starting inserting CPF");
        PreparedStatement stmt;
        Connection connection = repository.getConnection();
        try {
            connection.createStatement().execute("TRUNCATE TABLE tmp_cpf");
            connection.setAutoCommit(false);
            int batchSize = 1000;
            int count = 0;
            stmt = connection.prepareStatement("INSERT INTO tmp_cpf (cpf) VALUES (?)");

            logger.info("Starting insert into tmp_cpf");

            for (TmpCpf tmpCpf : tmpCpfList) {
                logger.info("Inserting row " + count);
                stmt.setBigDecimal(1, tmpCpf.getCpf());
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

    public void truncatePessoa() throws SQLException {
        logger.info("Truncating PESSOA");
        this.repository.getConnection().createStatement().execute("TRUNCATE TABLE \"PESSOA\"");
        logger.info("Truncated PESSOA");
    }

    public void insertPessoa(Pessoa pessoa) throws SQLException {
        logger.info("Starting insert into PESSOA with ID: " + pessoa.getCdCpf());

        PreparedStatement  stmt;
        Connection connection = repository.getConnection();
        try{
            Gson gson = new Gson();
            stmt = connection.prepareStatement("INSERT INTO \"PESSOA\"(\"CD_CPF\", \"CD_PESSOA\", \"CD_JORNAL\", \"NM_PESSOA\", \"SEXO\", \"DT_NASC\", \"ID_ESTADO_CIVIL\", \"ID_EMAIL\", \"DT_INCLUSAO\", \"DT_ALTERACAO\") VALUES (?, (?)::jsonb, ?, ?, ?, ?, ?, ?, ?, ?)");

            int i = 1;
            stmt.setBigDecimal(i++, pessoa.getCdCpf());
            stmt.setString(i++, gson.toJson(pessoa.getCdPessoa()));
            stmt.setInt(i++, pessoa.getCdJornal());
            stmt.setString(i++, pessoa.getNmPessoa());
            stmt.setString(i++, pessoa.getSexo());
            stmt.setDate(i++, pessoa.getDtNasc());
            stmt.setString(i++, pessoa.getIdEstadoCivil());
            stmt.setString(i++, pessoa.getIdEmail());
            stmt.setDate(i++, pessoa.getDtInclusao());
            stmt.setDate(i++, pessoa.getDtAlteracao());

            stmt.execute();
            stmt.close();
            logger.info("PESSOA with CPF: " + pessoa.getCdCpf() + " inserted with success");
        } catch (SQLException e) {
            logger.info("Insertion of PESSOA with CPF " + pessoa.getCdCpf() + " failed");
            throw e;
        }

    }

    public List<TmpCpf> getAllTmpCpf() {
        return repository.query("SELECT cpf FROM tmp_cpf", TmpCpf.class);
    }

    public List<TmpCpf> getAllTmpCpfNotInPessoa() {
        return repository.query("SELECT cpf FROM tmp_cpf left join \"PESSOA\" p on p.\"CD_CPF\" = tmp_cpf.cpf where p.\"CD_CPF\" is null", TmpCpf.class);
    }

    public List<Pessoa> getAllPessoasWithEmptyStats() {
        return repository.query("SELECT * FROM \"PESSOA\" WHERE \"QTD_ASS\" = 0", Pessoa.class);
    }

    public List<Pessoa> getAllPessoasWithEmptyEndereco() {
        return repository.query("SELECT * FROM \"PESSOA\" WHERE \"CEP\" IS NULL", Pessoa.class);
    }


    public void setEnderecoFromPessoa(Pessoa pessoa, Endereco endereco) throws SQLException {
        logger.info("Starting update PESSOA with CPF: " + pessoa.getCdCpf());

        PreparedStatement  stmt;
        Connection connection = repository.getConnection();
        try{
            Gson gson = new Gson();
            stmt = connection.prepareStatement("UPDATE \"PESSOA\" SET \"CEP\"=?, \"UF\"=?, \"CIDADE\"=?, \"BAIRRO\"=?, \"TIPO_LOGRADOURO\"=?, \"LOGRADOURO\"=?, \"NUMERO\"=?, \"COMPLEMENTO\"=? WHERE \"CD_CPF\" = ?");
            int i = 1;
            stmt.setString(i++, endereco.getCep());
            stmt.setString(i++, endereco.getUf());
            stmt.setString(i++, endereco.getCidade());
            stmt.setString(i++, endereco.getBairro());
            stmt.setString(i++, endereco.getTipoLogradouro());
            stmt.setString(i++, endereco.getLogradouro());
            stmt.setString(i++, endereco.getNumero());
            stmt.setString(i++, endereco.getComplemento());
            stmt.setBigDecimal(i++, pessoa.getCdCpf());
            stmt.execute();
            stmt.close();
            logger.info("PESSOA with CPF: " + pessoa.getCdCpf() + " updated with success ENDERECO");
        } catch (SQLException e) {
            logger.info("Updating of PESSOA with CPF " + pessoa.getCdCpf() + " failed");
            throw e;
        }
    }

    public void setAssinaturaStatsFromPessoa(Stats stats) throws SQLException {
        logger.info("Setting Assinatura Updating Stats");
        PreparedStatement  stmt;
        Connection connection = repository.getConnection();
        try{
            stmt = connection.prepareStatement("UPDATE \"PESSOA\" SET \"QTD_ASS\"=?, \"QTD_ASS_ATIVA\"=?, \"TOTAL_PAGO\"=?, \"FREQUENCIA\"=?, \"TOTAL_PAGO_3\"=?, \"FREQUENCIA_3\"=?, \"TOTAL_PAGO_12\"=?, \"FREQUENCIA_12\"=?, \"RECENCIA\"=?, \"LONGEVIDADE\"=? WHERE \"CD_CPF\" = ?\n");
            int i = 1;
            stmt.setBigDecimal(i++, stats.getQtdAss());
            stmt.setBigDecimal(i++, stats.getQtdAssAtiva());
            stmt.setBigDecimal(i++, stats.getTotalPago());
            stmt.setBigDecimal(i++, stats.getFrequencia());
            stmt.setBigDecimal(i++, stats.getTotalPago3());
            stmt.setBigDecimal(i++, stats.getFrequencia3());
            stmt.setBigDecimal(i++, stats.getTotalPago12());
            stmt.setBigDecimal(i++, stats.getFrequencia12());
            stmt.setBigDecimal(i++, stats.getRecencia());
            stmt.setBigDecimal(i++, stats.getLongevidade());
            stmt.setBigDecimal(i++, stats.getPessoa().getCdCpf());
            stmt.execute();
            stmt.close();
            logger.info("PESSOA with CPF: " + stats.getPessoa().getCdCpf() + " updated with success STATS");
        } catch (Exception e) {
            logger.info("Updating of PESSOA with CPF " + stats.getPessoa().getCdCpf() + " failed");
            throw e;
        }

    }

}
