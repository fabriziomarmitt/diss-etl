package br.com.marmitt.diss_etl.repository;

import java.sql.*;
import java.util.HashMap;

public class Repository {

    private Connection connection;

    public class Result {
        private final Statement statement;
        private final ResultSet resultSet;

        public Result(ResultSet resultSet, Statement statement){
            this.resultSet = resultSet;
            this.statement = statement;
        }

        public boolean next() throws SQLException {
            boolean next = resultSet.next();
            if(!next){
                resultSet.close();
                statement.close();
            }
            return next;
        }


        public ResultSet getResultSet(){
            return resultSet;
        }

        public HashMap<String,Object> getSingleResult() throws SQLException {
            HashMap<String, Object> hashMap = new HashMap<>();
            resultSet.next();
            for(int i = 1; i<=resultSet.getMetaData().getColumnCount(); i++){
                hashMap.put(resultSet.getMetaData().getColumnName(i), resultSet.getObject(i));
            }
            resultSet.close();
            statement.close();
            return hashMap;
        }
    }

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

    public Result query(String query) {
        Statement stmt;
        Result result = null;
        try {
            stmt = getConnection().createStatement();
            result = new Result(stmt.executeQuery(query), stmt);
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            return result;
        }
    }

    public Result preparedQuery(PreparedStatement preparedStatement) {
        Result result = null;
        try {
            result = new Result(preparedStatement.executeQuery(), preparedStatement);
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            return result;
        }
    }

    public void close(){
        try {
            connection.close();
        }catch (Exception e){
            return;
        }
    }

}
