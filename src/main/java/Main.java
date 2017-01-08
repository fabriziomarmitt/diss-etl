import model.pessoa.Pessoa;
import repository.AsRepository;
import repository.DissRepository;
import repository.Repository;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

public class Main {

    public static void main(String[] args){
        try {
            Main.stepTwo();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void stepOne(){
        AsRepository asRepository = new AsRepository();
        DissRepository dissRepository = new DissRepository();

        try {
            Repository.Result pessoas = asRepository.getPessoasUniqueCpf();
            dissRepository.insertCpfFromResultSet(pessoas.getResultSet());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void stepTwo() throws SQLException {
        // Passo dois consistem em pegar todos os CPF e
        // popular a tabela de pessoas com seus dados

        AsRepository asRepository = new AsRepository();
        DissRepository dissRepository = new DissRepository();

        Repository.Result allTmpCpfs = dissRepository.getAllTmpCpf();

        while(allTmpCpfs.next()) {
            BigDecimal cpf = allTmpCpfs.getResultSet().getBigDecimal("cpf");
            ArrayList<BigDecimal> cdPessoa = new ArrayList<>();
            ArrayList<Pessoa> pessoas = new ArrayList<>();

            Repository.Result result = asRepository.getPessoaByCpf(cpf);
            while(result.next()){
                Pessoa pessoa = new Pessoa(){{
                    setCdCpf(cpf);
                    setCdPessoa(
                        new ArrayList<BigDecimal>(){{
                            add(result.getResultSet().getBigDecimal("pess_cd_pessoa"));
                        }}
                    );
                    setCdJornal(result.getResultSet().getInt("pess_cd_jornal"));
                    setNmPessoa(result.getResultSet().getString("pess_nm_pessoa"));
                    setSexo(result.getResultSet().getString("pess_id_sexo"));
                    setDtNasc(result.getResultSet().getDate("pess_dt_nasc"));
                    setIdEstadoCivil(result.getResultSet().getString("pess_id_estado_civil"));
                    setIdEmail(result.getResultSet().getString("pess_id_email"));
                    setCep(result.getResultSet().getBigDecimal("cep"));
                    setUf(result.getResultSet().getString("uf"));
                    setCidade(result.getResultSet().getString("cidade"));
                    setBairro(result.getResultSet().getString("bairro"));
                    setTipoLogradouro(result.getResultSet().getString("tipo_logradouro"));
                    setLogradouro(result.getResultSet().getString("logradouro"));
                    setNumero(result.getResultSet().getString("numero"));
                    setComplemento(result.getResultSet().getString("complemento"));
                    setDtInclusao(result.getResultSet().getDate("pess_dt_inclusao"));
                    setDtAlteracao(result.getResultSet().getDate("pess_dt_alteracao"));

                }};
                cdPessoa.addAll(pessoa.getCdPessoa());
            }
            Random random = new Random();
            Pessoa pessoaChosen = pessoas.get(random.nextInt(pessoas.size()));
            pessoaChosen.setCdPessoa(cdPessoa);
            dissRepository.insertPessoa(pessoaChosen);
        }


    }
}