package me.elyar.sqldumper.utilities;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
    /**
     * Return list of column names from given {@code ResultSet}
     *
     * @param resultSet the {@code ResultSet} to retrieve column names from
     * @return {@code List<String>} contains column names
     * @throws SQLException if a database access error occurs
     */
    private List<String> getColumnNameList(ResultSet resultSet) throws SQLException {
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        // set initialCapacity to column count
        List<String> columnNameList = new ArrayList<>(resultSetMetaData.getColumnCount());

        // column index starts from 1
        for (int columnIndex = 1; columnIndex <= resultSetMetaData.getColumnCount(); columnIndex++) {
            columnNameList.add(resultSetMetaData.getColumnName(columnIndex));
        }
        return columnNameList;
    }
}
