import repository.AsRepository;
import repository.DissRepository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

public class Main {

    public static void main(String[] args){
    }

    public static void stepOne(){
        AsRepository asRepository = new AsRepository();
        DissRepository dissRepository = new DissRepository();

        try {
            ResultSet pessoas = asRepository.getPessoasUniqueCpf();
            dissRepository.insertCpfFromResultSet(pessoas);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void stepTwo() throws SQLException {
        // Passo dois consistem em pegar todos os CPF e
        // popular a tabela de pessoas com seus dados

        AsRepository asRepository = new AsRepository();
        DissRepository dissRepository = new DissRepository();

        for (Iterator<Object[]> iter = dissRepository.getAllTmpCpf(); iter.hasNext(); ) {
            Object[] cpf = iter.next();
            ArrayList<String> names = new ArrayList<>();
            ArrayList<String> cdPessoa = new ArrayList<>();

            asRepository.getPessoaByCpf((BigDecimal) cpf[0]).forEachRemaining( e -> e.);
        }


    }
}