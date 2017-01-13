package br.com.marmitt.diss_etl.repository;

import br.com.marmitt.diss_etl.model.pessoa.Pessoa;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.logging.Logger;

public class AsRepository extends AbstractRepository{

    Logger logger = Logger.getLogger(String.valueOf(AsRepository.class));

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
                = connection.prepareStatement("select " +
                "   p.pess_cd_cpf CD_CPF " +
                "  ,p.pess_cd_jornal CD_JORNAL " +
                "  ,p.pess_cd_pessoa CD_PESSOA " +
                "  ,p.pess_nm_pessoa NM_PESSOA " +
                "  ,p.pess_id_sexo SEXO " +
                "  ,p.pess_dt_nasc DT_NASC " +
                "  ,p.pess_id_estado_civil ID_ESTADO_CIVIL " +
                "  ,p.pess_id_email ID_EMAIL " +
                "  ,p.pess_dt_inclusao DT_INCLUSAO " +
                "  ,p.pess_dt_alteracao DT_ALTERACAO " +
                "from pessoa p " +
                "where p.pess_cd_jornal = 1 and p.pess_cd_cpf = ?");
        preparedStatement.setBigDecimal(1, cpf);
        return repository.preparedQuery(preparedStatement);
    }

    public Repository.Result getMostLikelyEndereco(BigDecimal[] cdPessoaList) throws SQLException, InterruptedException {
        Connection connection = repository.getConnection();
        String query = "select " +
                "  ve.END_CD_CEP CEP " +
                "  , ve.CID_CD_ESTADO UF " +
                "  , ve.CID_NM_CID CIDADE " +
                "  , ve.BAIR_NM_BAIRRO BAIRRO " +
                "  , ve.LOGR_TP_LOGR TIPO_LOGRADOURO " +
                "  , ve.LOGR_NM_LOGR LOGRADOURO " +
                "  , ve.END_NR_RESID NUMERO " +
                "  , (ve.LOGR_TP_LOGR || ' ' || ve.END_NR_COMPL) COMPLEMENTO " +
                " from periodo_entrega pe   " +
                "  inner join periodo_assinatura pa " +
                "    on pa.PERASS_CD_ASS = pe.PENTR_CD_ASS " +
                "    and pa.perass_cd_jornal = pe.PENTR_CD_JORNAL     " +
                "  inner join v_endereco ve " +
                "    on ve.END_CD_ENDERECO = pe.pentr_cd_endereco " +
                " where pa.PERASS_CD_RESPONSAVEL IN (" + String.join(",", Collections.nCopies(cdPessoaList.length, "?")) + ") " +
                " and rownum = 1 " +
                " order by pa.perass_dt_termino desc";
        PreparedStatement preparedStatement
                = connection.prepareStatement(query);
        for (int i = 0; i < cdPessoaList.length; i++){
            preparedStatement.setBigDecimal(i+1, cdPessoaList[i]);
        }
        return repository.preparedQuery(preparedStatement);
    }

    public HashMap<String, Object> getAssinaturasStats(Pessoa pessoa) throws SQLException {
        Connection connection = repository.getConnection();
        String query = "select   " +
                "  count(1) QTD_ASS  " +
                "  , nvl(sum(CASE WHEN sit_nm_situacao = 'ATIVO' THEN 1 ELSE 0 END),0) QTD_ASS_ATIVA  " +
                "  , nvl(sum(valor_pago),0) TOTAL_PAGO  " +
                "  , nvl(sum(valor_pago_3),0) TOTAL_PAGO_3  " +
                "  , nvl(sum(valor_pago_12),0) TOTAL_PAGO_12  " +
                " from(  " +
                " select   " +
                "  sa.SIT_NM_SITUACAO  " +
                "  , pa.PERASS_CD_ASS  " +
                "  , (select nvl(sum(nvl(par.pass_vl_pago,0)),0)  " +
                "      from parcela_assinatura par  " +
                "      where par.pass_cd_jornal = pa.perass_cd_jornal  " +
                "        and par.pass_cd_ass    = pa.perass_cd_ass  " +
                "        and par.pass_dt_mesano_cont is not null) valor_pago  " +
                "  , (select nvl(sum(nvl(par.pass_vl_pago,0)),0)  " +
                "      from parcela_assinatura par  " +
                "      where par.pass_cd_jornal = pa.perass_cd_jornal  " +
                "        and par.pass_cd_ass    = pa.perass_cd_ass  " +
                "        and par.pass_dt_mesano_cont is not null " +
                "        and par.pass_dt_vcto BETWEEN sysdate-90 and sysdate) valor_pago_3  " +
                "  , (select nvl(sum(nvl(par.pass_vl_pago,0)),0)  " +
                "      from parcela_assinatura par  " +
                "      where par.pass_cd_jornal = pa.perass_cd_jornal  " +
                "        and par.pass_cd_ass    = pa.perass_cd_ass  " +
                "        and par.pass_dt_mesano_cont is not null " +
                "        and par.pass_dt_vcto BETWEEN sysdate-365 and sysdate) valor_pago_12  " +
                " from periodo_assinatura pa   " +
                "  inner join SITUACAO_ASSINATURA sa  " +
                "    on sa.SIT_CD_SITUACAO = pa.PERASS_CD_SITUACAO  " +
                " where pa.perass_cd_jornal = 1  " +
                " and pa.PERASS_CD_RESPONSAVEL IN (" + String.join(",", Collections.nCopies(pessoa.getCdPessoa().size(), "?")) + ") " +
                " and pa.PERASS_CD_PERIODO = pck_cartao_clube_ass.fnc_busca_periodo_atual(pa.PERASS_CD_JORNAL, pa.perass_cd_ass) " +
                ") rs";

        PreparedStatement preparedStatement
                = connection.prepareStatement(query);
        for (int i = 0; i < pessoa.getCdPessoa().size(); i++){
            preparedStatement.setBigDecimal(i+1, pessoa.getCdPessoa().get(i));
        }
        Repository.Result result = repository.preparedQuery(preparedStatement);
        return result.getSingleResult();
    }

    public void close(){
        close();
    }
}
