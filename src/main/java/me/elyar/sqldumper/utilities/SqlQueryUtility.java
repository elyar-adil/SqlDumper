package me.elyar.sqldumper.utilities;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SqlQueryUtility {
    public static String queryString(Connection connection, String sql, int columnIndex) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        resultSet.next();
        // First column is same as tableName, create statement is at second column.
        String createStatement = resultSet.getString(columnIndex);
        resultSet.close();
        statement.close();
        return createStatement;
    }
}
