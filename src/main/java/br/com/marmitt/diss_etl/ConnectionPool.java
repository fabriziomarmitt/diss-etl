package br.com.marmitt.diss_etl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class ConnectionPool {

    private String driver, dsn, usr, pwd;
    private List<Connection> availableConnectionList = new ArrayList<>();
    private List<Connection> usedConnectionList = new ArrayList<>();

    public ConnectionPool(String driver, String dsn, String usr, String pwd) {
        this.driver = driver;
        this.dsn = dsn;
        this.usr = usr;
        this.pwd = pwd;
    }

    private void createConnections(){
        if(isPoolFull()) return;
        try {
            Class.forName(driver).newInstance();
            availableConnectionList.add(DriverManager.getConnection(dsn, usr, pwd));
        } catch (SQLException | InstantiationException | ClassNotFoundException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private synchronized boolean isPoolFull(){
        final int MAX_POOL_SIZE = 10;
        return availableConnectionList.size() + usedConnectionList.size() >= MAX_POOL_SIZE;
    }

    public synchronized Connection getConnection() throws Exception {
        if(availableConnectionList.size() > 0){
            Connection connection = availableConnectionList.get(0);
            availableConnectionList.remove(0);
            if(connection.isClosed()){
                createConnections();
                connection = availableConnectionList.get(0);
            }
            usedConnectionList.add(connection);
            return connection;
        }else{
            for(int i : IntStream.rangeClosed(1, 3).toArray()) {
                if(!isPoolFull()) {
                    createConnections();
                    return getConnection();
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new Exception(e);
                }
            }
        }
        throw new Exception("No Connection Available on the pool");
    }

    public synchronized void close(Connection connection) throws SQLException {
        if(!usedConnectionList.contains(connection))
            throw new SQLException("Connections does not belong to the Pool");
        usedConnectionList.remove(connection);
        availableConnectionList.add(connection);
    }

}
