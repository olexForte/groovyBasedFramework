package com.fortegrp.at.common.utils.dbutils

import com.fortegrp.at.common.env.Environment
import com.microsoft.sqlserver.jdbc.SQLServerDriver

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

import static com.fortegrp.at.common.utils.LogHelper.logInfo

/**
 * Created by aivarouski on 6/2/14.
 * Utility class that performs DB communication work
 */

final class DBHelper {
    private static DBHelper instance = null
    private String connectionUrl
    private Connection connection
    private String SQL_CONNECTION_STRING_NAMED_INSTANCE = "jdbc:sqlserver://%s\\%s;user=%s;password=%s;database=%s"
    private String SQL_CONNECTION_STRING_PORT_NUMBER = "jdbc:sqlserver://%s:%s;user=%s;password=%s;database=%s"

    static synchronized DBHelper getInstance() {
        if (instance == null) {
            instance = new DBHelper(Environment.getConfig().dbConfig.dbHost ?: Environment.getConfig().host,
                    Environment.getConfig().dbConfig.dbPort ?: "",
                    Environment.getConfig().dbConfig.dbInstance ?: "",
                    Environment.getConfig().dbConfig.dbName,
                    Environment.getConfig().dbConfig.dbUser,
                    Environment.getConfig().dbConfig.dbPassword)
        }
        return instance
    }

    DBHelper(String serverName, String port, String instanceName, String dbName, String username, String password) {
        if (instanceName.isEmpty()) {
            connectionUrl = String.format(SQL_CONNECTION_STRING_PORT_NUMBER, serverName, port, username, password, dbName)
        }else{
            connectionUrl = String.format(SQL_CONNECTION_STRING_NAMED_INSTANCE, serverName, instanceName, username, password, dbName)
        }
        try {
            DriverManager.registerDriver(new SQLServerDriver())
        } catch (SQLException e) {
            throw new RuntimeException("Unable to register SQL connection", e)
        }
    }

    Connection getConnection() {
        if (connection != null)
            try {
                if (!connection.isClosed())
                    return connection
            } catch (SQLException e) {
                throw new RuntimeException("Unable get SQL connection state", e)
            }
        try {
            connection = DriverManager.getConnection(connectionUrl)
//            log.info("DB connection established.");
        } catch (SQLException e) {
            throw new RuntimeException("Cannot establish db connection. Connection url: " + connectionUrl, e)
        }
        return connection
    }

    void closeConnection() {
        if (connection == null) return
        try {
            connection.close()
            logInfo("DB connection closed.")
        } catch (SQLException e) {
            logInfo("Cannot close DB connection.")
        }
    }
}
