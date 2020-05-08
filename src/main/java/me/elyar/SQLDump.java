package me.elyar;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class SQLDump {

    private final Connection connection;

    public SQLDump(String url) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection(url);
    }

    /**
     * Get SQL statement {@code String} that creates the specified table.
     *
     * @param tableName name of the table
     * @return SQL statement {@code String}
     * @throws SQLException if a database access error occurs
     */
    public String getCreateTableSQL(String tableName) throws SQLException {
        Statement statement = connection.createStatement();
        String sql = String.format("SHOW CREATE TABLE `%s`", tableName);
        ResultSet resultSet = statement.executeQuery(sql);
        resultSet.next();
        // First column is same as tableName, create statement is at second column.
        String createStatement = resultSet.getString(2);
        resultSet.close();
        statement.close();
        return createStatement;
    }

    /**
     * Get
     *
     * @return
     * @throws SQLException
     */
    public List<String> getDatabaseList() throws SQLException {
        Statement statement = connection.createStatement();
        String sql = "SHOW DATABASES";
        ResultSet resultSet = statement.executeQuery(sql);
        List<String> databaseList = new ArrayList<>();
        while (resultSet.next()) {
            String database = resultSet.getString(1);
            databaseList.add(database);
        }
        resultSet.close();
        statement.close();
        return databaseList;
    }

    /**
     * @param databaseName
     * @return
     * @throws SQLException
     */
    public String getCreateDatabaseSQL(String databaseName) throws SQLException {
        Statement statement = connection.createStatement();
        String sql = String.format("SHOW CREATE DATABASE IF NOT EXISTS `%s`", databaseName);

        ResultSet resultSet = statement.executeQuery(sql);
        resultSet.next();

        String createStatement = resultSet.getString(2);

        resultSet.close();
        statement.close();
        return createStatement;
    }

    /**
     * @param tableName
     * @return
     * @throws SQLException
     */
    public String cv(String tableName) throws SQLException, IOException {
        Statement statement = connection.createStatement();
        String sql = String.format("SELECT /*!40001 SQL_NO_CACHE */ * FROM %s", tableName);

        ResultSet resultSet = statement.executeQuery(sql);
        List<String> col = getColumnNames(resultSet);
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        int columnCount = resultSet.getMetaData().getColumnCount();
        while (resultSet.next()) {
            for (int i = 1; i <= columnCount; i++) {
                int columnType = resultSetMetaData.getColumnType(i);
                System.out.println(columnType);
                if (columnType == Types.VARBINARY
                        || columnType == Types.BLOB
                        || columnType == Types.NCLOB) {
                    Blob blob = resultSet.getBlob("y");

                    if (blob != null) {
                        System.out.println(Arrays.toString(blob.getBytes(1, 1)));

                    }
                } else {
                    System.out.print(resultSet.getString(i));
                }
                System.out.print(" ");
            }
            System.out.println();

        }

        resultSet.close();
        statement.close();
        return null;
    }

    public static String byteToHex(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : bytes) {
            stringBuilder.append(String.format("%02X", b));
        }
        return stringBuilder.toString();
    }

    public List<String> getColumnNames(ResultSet rs) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        List<String> retval = new ArrayList<>(rsmd.getColumnCount());

        for (int i = 0; i < rsmd.getColumnCount(); i++) {
            retval.add(rsmd.getColumnName(i + 1));
        }

        return retval;
    }
}
