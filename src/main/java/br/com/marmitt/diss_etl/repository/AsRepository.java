package br.com.marmitt.diss_etl.repository;

import br.com.marmitt.diss_etl.model.pessoa.Endereco;
import br.com.marmitt.diss_etl.model.pessoa.Pessoa;
import br.com.marmitt.diss_etl.model.pessoa.Stats;
import br.com.marmitt.diss_etl.model.pessoa.TmpCpf;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class AsRepository extends AbstractRepository{

    Logger logger = Logger.getLogger(String.valueOf(AsRepository.class));

    public Result getPessoas() throws SQLDataException {
        return query("SELECT * FROM pessoa");
    }

    public List<TmpCpf> getPessoasUniqueCpf() throws SQLDataException {
        String query = "SELECT pess_cd_cpf cpf "
                + " FROM pessoa p "
                + " WHERE p.pess_id_empresa = 'N'"
                + " AND p.pess_cd_jornal = 1"
                + " AND p.pess_cd_cpf IS NOT NULL"
                + " GROUP BY p.pess_cd_cpf";
        return query(query, TmpCpf.class);
    }

    public List<Pessoa> getPessoaByCpf(BigDecimal cpf) throws SQLException {
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
        return query(preparedStatement, Pessoa.class);
    }

    public Endereco getMostLikelyEndereco(Pessoa pessoa) throws SQLException, InterruptedException {
        List<BigDecimal> cdPessoaList = pessoa.getCdPessoa();
        String query = "select   ve.END_CD_CEP CEP   , ve.CID_CD_ESTADO UF   , ve.CID_NM_CID CIDADE   , ve.BAIR_NM_BAIRRO BAIRRO   , ve.LOGR_TP_LOGR TIPO_LOGRADOURO   , ve.LOGR_NM_LOGR LOGRADOURO   , ve.END_NR_RESID NUMERO   , (ve.LOGR_TP_LOGR || ' ' || ve.END_NR_COMPL) COMPLEMENTO  from periodo_entrega pe     inner join periodo_assinatura pa     on pa.PERASS_CD_ASS = pe.PENTR_CD_ASS     and pa.perass_cd_jornal = pe.PENTR_CD_JORNAL       inner join v_endereco ve     on ve.END_CD_ENDERECO = pe.pentr_cd_endereco  where pa.PERASS_CD_RESPONSAVEL IN (" + String.join(",", Collections.nCopies(cdPessoaList.size(), "?")) + ")  and rownum = 1  order by pa.perass_dt_termino desc";
        PreparedStatement preparedStatement
                = connection.prepareStatement(query);
        for (int i = 0; i < cdPessoaList.size(); i++){
            preparedStatement.setBigDecimal(i+1, cdPessoaList.get(i));
        }
        List<Endereco> enderecoList = query(preparedStatement, Endereco.class);
        if(enderecoList.size() > 0){
            enderecoList.get(0).setPessoa(pessoa);
            return enderecoList.get(0);
        }
        return null;
    }

    public Stats getAssinaturasStats(Pessoa pessoa) throws SQLException {
        String query = " select " +
                "   count(1) QTD_ASS   " +
                "   , nvl(sum(CASE WHEN sit_nm_situacao = 'ATIVO' THEN 1 ELSE 0 END),0) QTD_ASS_ATIVA   " +
                "   , nvl(sum(valor_pago),0) TOTAL_PAGO   " +
                "   , max(frequencia) FREQUENCIA " +
                "   , nvl(sum(valor_pago_3),0) TOTAL_PAGO_3   " +
                "   , max(frequencia_3) FREQUENCIA_3 " +
                "   , nvl(sum(valor_pago_12),0) TOTAL_PAGO_12   " +
                "   , max(frequencia_12) FREQUENCIA_12 " +
                "   , max(recencia) RECENCIA " +
                "   , min(longevidade) LONGEVIDADE " +
                "  from( " +
                "   select    " +
                "     sa.SIT_NM_SITUACAO   " +
                "     , pa.PERASS_CD_ASS   " +
                "     , (select nvl(sum(nvl(par.pass_vl_pago,0)),0)   " +
                "         from parcela_assinatura par   " +
                "         where par.pass_cd_jornal = pa.perass_cd_jornal   " +
                "           and par.pass_cd_ass    = pa.perass_cd_ass   " +
                "           and par.pass_dt_mesano_cont is not null) valor_pago   " +
                "     , (select nvl(count(1),0)   " +
                "         from parcela_assinatura par   " +
                "         where par.pass_cd_jornal = pa.perass_cd_jornal   " +
                "           and par.pass_cd_ass    = pa.perass_cd_ass   " +
                "           and par.pass_dt_mesano_cont is not null " +
                "           and (par.pass_vl_pago is not null or par.pass_vl_pago > 0)) frequencia           " +
                "     , (select nvl(sum(nvl(par.pass_vl_pago,0)),0)   " +
                "         from parcela_assinatura par   " +
                "         where par.pass_cd_jornal = pa.perass_cd_jornal   " +
                "           and par.pass_cd_ass    = pa.perass_cd_ass   " +
                "           and par.pass_dt_mesano_cont is not null " +
                "           and par.pass_dt_vcto BETWEEN sysdate-90 and sysdate) valor_pago_3   " +
                "     , (select nvl(count(1),0)   " +
                "         from parcela_assinatura par   " +
                "         where par.pass_cd_jornal = pa.perass_cd_jornal   " +
                "           and par.pass_cd_ass    = pa.perass_cd_ass   " +
                "           and par.pass_dt_mesano_cont is not null " +
                "           and (par.pass_vl_pago is not null or par.pass_vl_pago > 0) " +
                "           and par.pass_dt_vcto BETWEEN sysdate-90 and sysdate) frequencia_3           " +
                "     , (select nvl(sum(nvl(par.pass_vl_pago,0)),0)   " +
                "         from parcela_assinatura par   " +
                "         where par.pass_cd_jornal = pa.perass_cd_jornal   " +
                "           and par.pass_cd_ass    = pa.perass_cd_ass   " +
                "           and par.pass_dt_mesano_cont is not null " +
                "           and par.pass_dt_vcto BETWEEN sysdate-365 and sysdate) valor_pago_12  " +
                "     , (select nvl(count(1),0)   " +
                "         from parcela_assinatura par   " +
                "         where par.pass_cd_jornal = pa.perass_cd_jornal   " +
                "           and par.pass_cd_ass    = pa.perass_cd_ass   " +
                "           and par.pass_dt_mesano_cont is not null " +
                "           and (par.pass_vl_pago is not null or par.pass_vl_pago > 0) " +
                "           and par.pass_dt_vcto BETWEEN sysdate-365 and sysdate) frequencia_12   " +
                "     , (select round(sysdate-max(par.pass_dt_vcto)) " +
                "         from parcela_assinatura par   " +
                "         where par.pass_cd_jornal = pa.perass_cd_jornal   " +
                "           and par.pass_cd_ass    = pa.perass_cd_ass   " +
                "           and par.pass_dt_mesano_cont is not null " +
                "           and (par.pass_vl_pago is not null or par.pass_vl_pago > 0)) recencia   " +
                "     , (select round(sysdate-min(par.pass_dt_vcto)) " +
                "         from parcela_assinatura par   " +
                "         where par.pass_cd_jornal = pa.perass_cd_jornal   " +
                "           and par.pass_cd_ass    = pa.perass_cd_ass   " +
                "           and par.pass_dt_mesano_cont is not null " +
                "           and (par.pass_vl_pago is not null or par.pass_vl_pago > 0)) longevidade   " +
                "   from periodo_assinatura pa    " +
                "   inner join SITUACAO_ASSINATURA sa   " +
                "   on sa.SIT_CD_SITUACAO = pa.PERASS_CD_SITUACAO   " +
                "   where pa.perass_cd_jornal = 1   " +
                "   and pa.PERASS_CD_RESPONSAVEL IN (" + String.join(",", Collections.nCopies(pessoa.getCdPessoa().size(), "?")) + ") " +
                "   and pa.PERASS_CD_PERIODO = pck_cartao_clube_ass.fnc_busca_periodo_atual(pa.PERASS_CD_JORNAL, pa.perass_cd_ass)  " +
                " ) rs ";
        PreparedStatement preparedStatement
                = connection.prepareStatement(query);
        for (int i = 0; i < pessoa.getCdPessoa().size(); i++){
            preparedStatement.setBigDecimal(i+1, pessoa.getCdPessoa().get(i));
        }
        List<Stats> statsList = query(preparedStatement, Stats.class);
        if(statsList.size() > 0){
            statsList.get(0).setPessoa(pessoa);
            return statsList.get(0);
        }
        return null;
    }
}
