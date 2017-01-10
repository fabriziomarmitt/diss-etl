package br.com.marmitt.diss_etl.repository;

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
                = connection.prepareStatement("select " +
                "   p.pess_cd_jornal cd_jornal " +
                "  ,p.pess_cd_pessoa cd_pessoa " +
                "  ,p.pess_nm_pessoa nm_pessoa " +
                "  ,p.pess_id_sexo id_sexo " +
                "  ,p.pess_dt_nasc dt_nasc " +
                "  ,p.pess_id_estado_civil id_estado_civil " +
                "  ,p.pess_id_email id_email " +
                "  ,ve.end_cd_cep cep " +
                "  ,ve.cid_cd_estado uf " +
                "  ,ve.cid_nm_cid cidade " +
                "  ,ve.bair_nm_bairro bairro " +
                "  ,ve.logr_tp_logr tipo_logradouro " +
                "  ,ve.logr_nm_logr logradouro " +
                "  ,ve.end_nr_resid numero " +
                "  ,(ve.end_tp_compl || ' ' || ve.end_nr_compl) complemento " +
                "  ,p.pess_dt_inclusao pess_dt_inclusao " +
                "  ,p.pess_dt_alteracao pess_dt_alteracao " +
                "from pessoa p " +
                "  left join ( " +
                "    select  " +
                "      PENTR_CD_JORNAL " +
                "      ,PENTR_CD_PESSOA " +
                "      ,PENTR_CD_ENDERECO  " +
                "      , row_number() over( partition by periodo_entrega.PENTR_CD_PESSOA, periodo_entrega.PENTR_CD_JORNAL order by periodo_entrega.PENTR_CD_PERIODO desc ) as rnk " +
                "    from periodo_entrega " +
                "  ) pe " +
                "  on pe.PENTR_CD_JORNAL = p.pess_cd_jornal " +
                "  and pe.PENTR_CD_PESSOA = p.pess_cd_pessoa " +
                "  left join v_endereco ve " +
                "    on ve.END_CD_ENDERECO = pe.PENTR_CD_ENDERECO   " +
                "where pe.rnk = 1 and p.pess_cd_jornal = 1 and p.pess_cd_cpf = ?");
        preparedStatement.setBigDecimal(1, cpf);
        return repository.preparedQuery(preparedStatement);
    }
}
