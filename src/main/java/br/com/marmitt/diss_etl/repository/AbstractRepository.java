package br.com.marmitt.diss_etl.repository;

import br.com.marmitt.diss_etl.ConnectionPool;
import br.com.marmitt.diss_etl.model.IModel;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class AbstractRepository {
    protected Connection connection;
    protected ConnectionPool connectionPool;

    public AbstractRepository() {
        String namespace = this.getClass().getSimpleName().toLowerCase().replace("repository", "");
        namespace = String.format("app.db.%s", namespace);
        Properties prop = new Properties();
        try {
            prop.load(AbstractRepository.class.getClassLoader().getResourceAsStream("application.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String url = prop.getProperty(namespace + ".url");
        String username = prop.getProperty(namespace + ".username");
        String password = prop.getProperty(namespace + ".password");
        String driverClassName = prop.getProperty(namespace + ".driverClassName");
        connectionPool = new ConnectionPool(driverClassName, url, username, password);
        try {
            connection = connectionPool.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class Result{
        private final Statement statement;
        private final ResultSet resultSet;
        private final Connection connection;
        private final ConnectionPool connectionPool;
        public Result(ResultSet resultSet, Statement statement, Connection connection, ConnectionPool connectionPool){
            this.resultSet = resultSet;
            this.statement = statement;
            this.connection = connection;
            this.connectionPool = connectionPool;
        }
        public boolean next() throws SQLException {
            boolean next = resultSet.next();
            if(!next) close();
            return next;
        }
        public<T extends IModel> T getResult(Class<T> tClass){
            T model = null;
            try {
                model = tClass.newInstance();
                model.setResultSet(getResultSet());
            } catch (InstantiationException | IllegalAccessException | SQLException e) {
                e.printStackTrace();
            }
            return model;
        }
        public ResultSet getResultSet(){
            return resultSet;
        }
        public void close(){
            try {
                resultSet.close();
                statement.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public Result query(PreparedStatement statement){
        try {
            return new Result(statement.executeQuery(), statement, connection, connectionPool);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Result query(String query) {
        try {
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            preparedStatement.setFetchSize(0);
            return query(preparedStatement);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public<T extends IModel> List<T> query(PreparedStatement preparedStatement, Class<T> tClass) {
        try {
            Result result = new Result(preparedStatement.executeQuery(), preparedStatement, connection, connectionPool);
            connection.setAutoCommit(true);
            return query(result, tClass);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
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

    public void close(){
        try {
            connectionPool.close(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
