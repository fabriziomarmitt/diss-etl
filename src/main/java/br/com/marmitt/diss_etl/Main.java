package br.com.marmitt.diss_etl;


import br.com.marmitt.diss_etl.model.pessoa.Endereco;
import br.com.marmitt.diss_etl.model.pessoa.Pessoa;
import br.com.marmitt.diss_etl.repository.AsRepository;
import br.com.marmitt.diss_etl.repository.DissRepository;
import br.com.marmitt.diss_etl.repository.Repository;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;

public class Main {

    public static void main(String[] args){
        try {
            Main.stepFour();
        } catch (Exception e) {
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

        Repository.Result allTmpCpfs = dissRepository.getAllTmpCpfNotInPessoa();

        while(allTmpCpfs.next()) {
            BigDecimal cpf = allTmpCpfs.getResultSet().getBigDecimal("cpf");
            ArrayList<BigDecimal> cdPessoa = new ArrayList<>();
            ArrayList<Pessoa> pessoas = new ArrayList<>();

            Repository.Result result = asRepository.getPessoaByCpf(cpf);
            while(result.next()){
                Pessoa pessoa = new Pessoa(result.getResultSet());
                cdPessoa.addAll(pessoa.getCdPessoa());
                pessoas.add(pessoa);
            }
            if(pessoas.size() > 0) {
                Pessoa pessoaChosen;
                if(pessoas.size() == 1){
                    pessoaChosen = pessoas.get(0);
                }else {
                    try {
                        pessoaChosen = pessoas
                                .stream()
                                .filter(p -> p.getDtAlteracao() != null)
                                .max(Comparator.comparing(Pessoa::getDtAlteracao))
                                .get();
                    }catch (Exception e){
                        Random random = new Random();
                        pessoaChosen = pessoas.get(random.nextInt(pessoas.size()));
                    }
                }
                pessoaChosen.setCdPessoa(cdPessoa);
                dissRepository.insertPessoa(pessoaChosen);
            }
        }


    }

    public static void stepThree() throws SQLException, InterruptedException {
        // Buscar Endereço mais provável para cada pessoa
        //
        AsRepository asRepository = new AsRepository();
        DissRepository dissRepository = new DissRepository();

        Repository.Result allTmpCdPessoas = dissRepository.getAllPessoasWithEmptyEndereco();

        while(allTmpCdPessoas.next()){
            Pessoa pessoa = new Pessoa(allTmpCdPessoas.getResultSet());
            BigDecimal[] listOfCdPessoa =pessoa.getCdPessoa().toArray(new BigDecimal[]{});
            Repository.Result getMostLikelyEndereco = asRepository
                    .getMostLikelyEndereco(listOfCdPessoa);
            if(getMostLikelyEndereco.getResultSet().next()){
                Endereco endereco = new Endereco(getMostLikelyEndereco.getResultSet());
                dissRepository.setEnderecoFromPessoa(pessoa, endereco);
                // forçar fechar conexao
                getMostLikelyEndereco.next();
            } else {
                getMostLikelyEndereco.getResultSet().getStatement().close();
                getMostLikelyEndereco.getResultSet().close();
                pessoa.getCdPessoa().stream().forEach( cd -> System.out.print(cd.toString() + ", "));
                System.out.println("\n----");
            }
        }
    }

    public static void stepFour() throws SQLException {
        // Insert Qtd Assinaturas por pessoa
        AsRepository asRepository = new AsRepository();
        DissRepository dissRepository = new DissRepository();

        Repository.Result pessoas = dissRepository.getAllPessoas();

        while(pessoas.next()){
            Pessoa pessoa = new Pessoa(pessoas.getResultSet());
            HashMap<String, Object> assinaturasStatus = asRepository.getAssinaturasStats(pessoa);
            if(assinaturasStatus.size() > 0){
                dissRepository.setAssinaturaStatsFromPessoa(
                        pessoa,
                        (BigDecimal) assinaturasStatus.get("QTY_ASS"),
                        (BigDecimal) assinaturasStatus.get("QTY_ASS_ATIVA"),
                        (BigDecimal) assinaturasStatus.get("TOTAL_PAID")
                );
            }
        }

    }
}