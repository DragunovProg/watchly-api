package ua.dragunov.watchly.util;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class TransactionManager {
    private final DataSource dataSource;
    private final ThreadLocal<Connection> connectionThreadLocal = new ThreadLocal<>();

    public TransactionManager(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void begin() throws SQLException {
        Connection connection = dataSource.getConnection();
        connection.setAutoCommit(false);
        connectionThreadLocal.set(connection);

    }

    public void commit()  {
        Connection connection = connectionThreadLocal.get();
        if (connection != null) {
            try {
                connection.commit();
                connection.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }

            finally {
                connectionThreadLocal.remove();
            }
        }
    }

    public void rollback() throws SQLException {
        Connection connection = connectionThreadLocal.get();
        if (connection != null) {
            connection.rollback();
            connection.close();
            connectionThreadLocal.remove();
        }
    }

    public Connection getCurrentConnection() {
        return connectionThreadLocal.get();
    }
}
