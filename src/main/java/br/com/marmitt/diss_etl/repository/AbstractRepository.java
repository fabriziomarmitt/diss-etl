package br.com.marmitt.diss_etl.repository;

import java.io.IOException;
import java.util.Properties;

public class AbstractRepository {
    protected Repository repository;
    public AbstractRepository() {
        String namespace = this.getClass().getSimpleName().toLowerCase().replace("repository", "");
        namespace = String.format("app.db.%s", namespace);
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
}
