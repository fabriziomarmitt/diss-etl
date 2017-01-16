package br.com.marmitt.diss_etl.repository;

import br.com.marmitt.diss_etl.model.IModel;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Repository {

    private Connection connection;
    private List<Result> resultList = new ArrayList<>();

    public void close() {
        for (Result result : resultList) {
            result.close();
        }
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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

        public<T extends IModel> T getSingleResult(T model) throws SQLException {
            if(!resultSet.next()){
                return null;
            }
            model.setResultSet(resultSet);
            resultSet.close();
            statement.close();
            return model;
        }

        public void close(){
            try {
                resultSet.close();
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

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

    public Result query(PreparedStatement statement){
        try {
            return new Result(statement.executeQuery(), statement);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Result query(String query) {
        try {
            return query(getConnection().prepareStatement(query));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public<T extends IModel> List<T> query(PreparedStatement preparedStatement, Class<T> tClass) {
        try {
            Result result = new Result(preparedStatement.executeQuery(), preparedStatement);
            return query(result, tClass);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private<T extends IModel> List<T> query(Result result, Class<T> tClass){
        ArrayList<T> _return = new ArrayList<>();
        try {
            while(result.next()){
                T model = tClass.newInstance();
                model.setResultSet(result.getResultSet());
                _return.add(model);
            }
        } catch (SQLException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }finally {
            result.close();
        }
        return _return;

    }

    public<T extends IModel> List<T> query(String query, Class<T> classType){
        return query(query(query), classType);
    }

}
