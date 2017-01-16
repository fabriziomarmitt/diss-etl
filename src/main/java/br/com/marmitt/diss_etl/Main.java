package br.com.marmitt.diss_etl;


import br.com.marmitt.diss_etl.model.pessoa.Endereco;
import br.com.marmitt.diss_etl.model.pessoa.Pessoa;
import br.com.marmitt.diss_etl.model.pessoa.Stats;
import br.com.marmitt.diss_etl.model.pessoa.TmpCpf;
import br.com.marmitt.diss_etl.repository.AsRepository;
import br.com.marmitt.diss_etl.repository.DissRepository;
import br.com.marmitt.diss_etl.repository.Repository;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Main {

    public static Logger logger = Logger.getLogger(String.valueOf(Main.class));

    public static void main(String[] args){
        try {
            //Main.stepOne();
            Main.stepTwo();
            //Main.stepThree();
            //Main.stepFour();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void stepOne(){
        AsRepository asRepository = new AsRepository();
        DissRepository dissRepository = new DissRepository();

        try {
            List<TmpCpf> tmpCpfList = asRepository.getPessoasUniqueCpf();
            dissRepository.insertTmpCpfs(tmpCpfList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        asRepository.close();
        dissRepository.close();
    }

    public static void stepTwo() throws SQLException {
        // Passo dois consistem em pegar todos os CPF e
        // popular a tabela de pessoas com seus dados

        AsRepository asRepository = new AsRepository();
        DissRepository dissRepository = new DissRepository();

        List<TmpCpf> allTmpCpfs = dissRepository.getAllTmpCpfNotInPessoa();

        for(TmpCpf tmpCpf : allTmpCpfs){
            List<Pessoa> pessoas = asRepository.getPessoaByCpf(tmpCpf.getCpf());
            List<BigDecimal> cdPessoa = pessoas.stream().map(pessoa -> pessoa.getCdPessoa().get(0)).collect(Collectors.toList());
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
        asRepository.close();
        dissRepository.close();
    }

    public static void stepThree() throws SQLException, InterruptedException {
        // Buscar Endereço mais provável para cada pessoa
        //
        AsRepository asRepository = new AsRepository();
        DissRepository dissRepository = new DissRepository();

        List<Pessoa> allTmpCdPessoas = dissRepository.getAllPessoasWithEmptyEndereco();

        for(Pessoa pessoa : allTmpCdPessoas){
            Endereco getMostLikelyEndereco = asRepository.getMostLikelyEndereco(pessoa.getCdPessoa());

            if(getMostLikelyEndereco != null){
                dissRepository.setEnderecoFromPessoa(getMostLikelyEndereco);
            } else {
                getMostLikelyEndereco.getResultSet().getStatement().close();
                getMostLikelyEndereco.getResultSet().close();
                pessoa.getCdPessoa().stream().forEach( cd -> System.out.print(cd.toString() + ", "));
                System.out.println("\n----");
            }
        }

        asRepository.close();
        dissRepository.close();
    }

    public static void stepFour() throws SQLException {
        // Insert Qtd Assinaturas por pessoa
        AsRepository asRepository = new AsRepository();
        DissRepository dissRepository = new DissRepository();

        Repository.Result pessoas = dissRepository.getAllPessoasWithEmptyStats();

        while(pessoas.next()){
            Pessoa pessoa = new Pessoa();
            pessoa.setResultSet(pessoas.getResultSet());
            Stats stats = new Stats(){{
                setPessoa(pessoa);
            }};
            stats = asRepository.getAssinaturasStats(pessoa, stats);
            if(stats != null){
                dissRepository.setAssinaturaStatsFromPessoa(stats);
            }
        }
        asRepository.close();
        dissRepository.close();

    }
}