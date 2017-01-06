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

    public ResultSet query(String query) {
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            return stmt.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
