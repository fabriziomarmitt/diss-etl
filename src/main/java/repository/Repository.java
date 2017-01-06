package repository;

import java.sql.*;

public class Repository {

    private Connection connection;

    public Repository(String driver, String dsn, String usr, String pwd) {
        try {
            Class.forName(driver).newInstance();
            connection = DriverManager.getConnection(dsn, usr, pwd);
        } catch (SQLException | InstantiationException | ClassNotFoundException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection(){
        return connection;
    }

    public ResultSet query(String query) {
        Statement stmt = null;
        ResultSet resultSet = null;
        try {
            stmt = getConnection().createStatement();
            resultSet = stmt.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return resultSet;
        }
    }
}
