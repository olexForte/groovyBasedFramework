package com.fortegrp.at.common.utils.dbutils

import org.codehaus.groovy.runtime.typehandling.GroovyCastException

import java.sql.*

/**
 * Created by yhraichonak
 *
 */
class BaseDataLoadHelper {

    static int CHANGES_TIMEOUT = 500
    static dbConnection = DBHelper.getInstance().getConnection()
    static dateCreated
    static dateUpdated

    static getDateCreated() {
        dateCreated = new java.sql.Date(System.currentTimeMillis())
    }

    static getDateUpdated() {
        dateUpdated = new java.sql.Date(System.currentTimeMillis())
    }

    static prepareStatement(PreparedStatement ps, Object... args) throws SQLException {
        int i = 1
        for (Object arg : args) {
            try {
                if (arg instanceof java.sql.Date) {
                    ps.setTimestamp(i++, new Timestamp(((Date) arg).getTime()))
                } else if (arg instanceof Integer) {
                    ps.setInt(i++, (Integer) arg)
                } else if (arg instanceof Long) {
                    ps.setLong(i++, (Long) arg)
                } else if (arg instanceof Double) {
                    ps.setDouble(i++, (Double) arg)
                } else if (arg instanceof Float) {
                    ps.setFloat(i++, (Float) arg)
                } else if (arg instanceof Boolean) {
                    ps.setBoolean(i++, (Boolean) arg)
                } else {
                    ps.setString(i++, (String) arg)
                }
            } catch (GroovyCastException ex) {
                throw new RuntimeException("Unable process SQL argument number " + i + " with value " + arg, ex)
            }
        }
    }

    static prepareAndExecuteStatement(PreparedStatement ps, Object... args) {
        prepareStatement(ps, args)
        ps.executeUpdate()
        def retval= null
        try {
            ps.getGeneratedKeys().next()
            retval = ps.getGeneratedKeys().getString(1)
        }
        catch (SQLServerException) {
            retval = null
        }
        return retval
    }

    static executeSelectQuery(stmt) {
        def result
        try {
            ResultSet rs = stmt.executeQuery()
            while (rs.next()) {
                try {
                    result = rs.getInt(1)
                } catch (NumberFormatException) {
                    result = rs.getString(1)
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error during the SQL Query execution", e)
        }
        result
    }
}
