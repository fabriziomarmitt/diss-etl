package br.com.marmitt.diss_etl;


import br.com.marmitt.diss_etl.model.pessoa.Endereco;
import br.com.marmitt.diss_etl.model.pessoa.Pessoa;
import br.com.marmitt.diss_etl.model.pessoa.Stats;
import br.com.marmitt.diss_etl.model.pessoa.TmpCpf;
import br.com.marmitt.diss_etl.repository.AbstractRepository;
import br.com.marmitt.diss_etl.repository.AsRepository;
import br.com.marmitt.diss_etl.repository.DissRepository;

import java.math.BigDecimal;
import java.sql.SQLException;
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
            //Main.stepTwo();
            Main.stepThree();
            Main.stepFour();
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
        AsRepository asRepository = new AsRepository();
        DissRepository dissRepository = new DissRepository();
        AbstractRepository.Result allTmpCdPessoas = dissRepository.getAllPessoasWithEmptyEndereco();
        int index = 0;
        while (allTmpCdPessoas.next()){
            Pessoa pessoa = allTmpCdPessoas.getResult(Pessoa.class);
            Endereco getMostLikelyEndereco = asRepository.getMostLikelyEndereco(pessoa);
            if(getMostLikelyEndereco != null){
                dissRepository.setEnderecoFromPessoa(getMostLikelyEndereco);
            }
            dissRepository.setProcessed(pessoa);
            getMostLikelyEndereco = null;
            pessoa = null;
            if(++index % 100 == 0 )
                System.gc();
        }
        allTmpCdPessoas.close();
        asRepository.close();
        dissRepository.close();
    }

    public static void stepFour() throws SQLException {
        // Insert Qtd Assinaturas por pessoa
        AsRepository asRepository = new AsRepository();
        DissRepository dissRepository = new DissRepository();
        AbstractRepository.Result pessoas = dissRepository.getAllPessoasWithEmptyStats();
        while(pessoas.next()){
            Pessoa pessoa = pessoas.getResult(Pessoa.class);
            Stats stats = asRepository.getAssinaturasStats(pessoa);
            if(stats != null){
                dissRepository.setAssinaturaStatsFromPessoa(stats);
                stats = null;
                pessoa = null;
                System.gc();
            }
        }
        pessoas.close();
        asRepository.close();
        dissRepository.close();
    }
}