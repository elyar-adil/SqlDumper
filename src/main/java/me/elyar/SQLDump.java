package me.elyar;


import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class SQLDump {

    public static final String NULL = "NULL";
    public static final String HEX_PREFIX = "0x";
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

    public String getInsertSQL(String tableName) throws SQLException, IOException {
        String selectSQL = String.format("SELECT /*!40001 SQL_NO_CACHE */ * FROM %s", tableName);
        String insertSQLTemplate = "INSERT INTO `%s` VALUES (%s);";

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(selectSQL);
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

        int columnCount = resultSet.getMetaData().getColumnCount();

        while (resultSet.next()) {
            List<String> rowValues = new ArrayList<>(columnCount);
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                int columnType = resultSetMetaData.getColumnType(columnIndex);
                switch (columnType) {
                    case Types.VARBINARY:
                    case Types.BLOB:
                    case Types.CLOB:
                    case Types.NCLOB:
                        Blob blob = resultSet.getBlob(columnIndex);
                        if (resultSet.wasNull()) {
                            rowValues.add(NULL);
                        } else {
                            String hex = byteToHex(blob.getBytes(1, (int) blob.length()));
                            rowValues.add(HEX_PREFIX + hex);
                        }
                        break;
                    case Types.VARCHAR:
                        String varchar = resultSet.getString(columnIndex);
                        if(resultSet.wasNull()) {
                            rowValues.add(NULL);
                        } else {
                            varchar = escapeStringForMySQL(varchar);
                            rowValues.add("'" + varchar + "'");
                        }
                        break;
                    case Types.TIME:
                    case Types.TIMESTAMP:
                        String time = resultSet.getString(columnIndex);
                        if (resultSet.wasNull()) {
                            rowValues.add(NULL);
                        } else {
                            rowValues.add("'" + time + "'");
                        }
                        break;
                }
            }
            String rowValue = String.join(", ", rowValues);
            System.out.println(String.format(insertSQLTemplate, tableName, rowValue));

        }

        resultSet.close();
        statement.close();
        return null;
    }


    private String escapeStringForMySQL(String s) {
        return s.replace("\\", "\\\\")
                .replace("\b", "\\b")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t")
                .replace("\\x1A", "\\Z")
                .replace("\\x00", "\\0")
                .replace("'", "\\'")
                .replace("\"", "\\\"");
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
