package me.elyar.sqldumper.dumper;

import me.elyar.sqldumper.utilities.SqlQueryUtility;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseDumper extends Dumper {
    public DatabaseDumper(Connection connection, PrintWriter printWriter) {
        super(connection, printWriter);
    }

    @Override
    public void dump(String databaseName)  throws SQLException {

    }
    /**
     * @param databaseName
     * @return
     * @throws SQLException
     */
    public String getCreateDatabaseSQL(String databaseName) throws SQLException {
        String sql = String.format("SHOW CREATE DATABASE IF NOT EXISTS `%s`", databaseName);
        return SqlQueryUtility.queryString(connection, sql, 2);
    }
}