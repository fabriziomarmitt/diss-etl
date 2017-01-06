import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

public class AsRepository{
    private Repository repository;
    final private String namespace = "app.db.as";

    public AsRepository() {
        Properties prop = new Properties();
        try {
            prop.load(Repository.class.getClassLoader().getResourceAsStream("application.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String url = prop.getProperty(namespace + ".url");
        String username = prop.getProperty(namespace + ".username");
        String password = prop.getProperty(namespace + ".password");
        String driverClassName = prop.getProperty(namespace + ".driverClassName");
        repository = new Repository(driverClassName, url, username, password);
    }

    public List<String> getPessoas() {
        ResultSet rs = repository.query("SELECT * FROM pessoa");
        while(rs.next()){

        }
    }


}
