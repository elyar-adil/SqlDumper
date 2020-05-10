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

    public SQLDump(String url) throws SQLException {
        DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
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
     * @param procedureName
     * @return
     * @throws SQLException
     */
    public String getCreateProcedureSQL(String procedureName) throws SQLException {
        Statement statement = connection.createStatement();
        String sql = String.format("SHOW CREATE PROCEDURE `%s`", procedureName);

        ResultSet resultSet = statement.executeQuery(sql);
        resultSet.next();

        String createStatement = resultSet.getString(2);

        resultSet.close();
        statement.close();
        return createStatement;
    }

    public String getInsertSQL(String tableName) throws SQLException {
        String selectSQL = String.format("SELECT /*!40001 SQL_NO_CACHE */ * FROM %s", tableName);

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(selectSQL);

        while (resultSet.next()) {
            String insertSQL = nextInsert(tableName, resultSet);
            System.out.println(insertSQL);
        }

        resultSet.close();
        statement.close();
        return null;
    }

    private final static String INSERT_SQL_TEMPLATE = "INSERT INTO `%s` VALUES (%s);";

    private String nextInsert(String tableName, ResultSet resultSet) throws SQLException {
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

        int columnCount = resultSet.getMetaData().getColumnCount();
        List<String> rowValues = new ArrayList<>(columnCount);
        for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
            int columnType = resultSetMetaData.getColumnType(columnIndex);
            switch (columnType) {
                // text type (surrounded by quotation marks and escaped):
                case Types.BIT:
                case Types.TINYINT:
                case Types.SMALLINT:
                case Types.INTEGER:
                case Types.BIGINT:
                case Types.FLOAT:
                case Types.REAL:
                case Types.DOUBLE:
                case Types.NUMERIC:
                case Types.DECIMAL:
                    addColumnValue(resultSet, rowValues, columnIndex, false, false, false);
                    break;
                case Types.CHAR:
                case Types.VARCHAR:
                case Types.LONGVARCHAR:
                    addColumnValue(resultSet, rowValues, columnIndex, true, true, false);
                    break;
                // types need to be surrounded by quotation marks without escaping
                case Types.DATE:
                case Types.TIME:
                case Types.TIMESTAMP:
                    addColumnValue(resultSet, rowValues, columnIndex, false, true, false);
                    break;
                // binary types (convert to hex):
                case Types.BINARY:
                case Types.VARBINARY:
                case Types.LONGVARBINARY:
                case Types.BLOB:
                case Types.CLOB:
                case Types.NCLOB:
                    addColumnValue(resultSet, rowValues, columnIndex, false, false, true);
                    break;
                default:
                    throw new SQLDataException("Unsupported SQL type: " + columnType);
            }
        }
        String rowValue = String.join(", ", rowValues);
        return String.format(INSERT_SQL_TEMPLATE, tableName, rowValue);
    }

    private void addColumnValue(ResultSet resultSet, List<String> rowValues, int columnIndex,
                                boolean escaping, boolean quotation, boolean binary) throws SQLException {
        String columnValue = NULL;
        if (binary) {
            Blob blob = resultSet.getBlob(columnIndex);
            if (blob != null) {
                columnValue = HEX_PREFIX + byteToHex(blob.getBytes(1, (int) blob.length()));
            }
        } else {
            columnValue = resultSet.getString(columnIndex);
        }

        if (resultSet.wasNull()) {
            columnValue = NULL;
        } else {
            if (escaping) {
                columnValue = escapeStringForMySQL(columnValue);
            }
            if (quotation) {
                columnValue = "'" + columnValue + "'";
            }
        }
        rowValues.add(columnValue);
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

    /**
     * Convert array of {@code byte} to equivalent {@code String} of hexadecimal value.
     *
     * @param byteArray {@code byte[]} to be converted to hexadecimal value
     * @return {@code String} of hexadecimal value
     */
    public static String byteToHex(byte[] byteArray) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : byteArray) {
            stringBuilder.append(String.format("%02X", b));
        }
        return stringBuilder.toString();
    }

    /**
     * Return list of column names from given {@code ResultSet}
     *
     * @param resultSet the {@code ResultSet} to retrieve column names from
     * @return {@code List<String>} contains column names
     * @throws SQLException
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
