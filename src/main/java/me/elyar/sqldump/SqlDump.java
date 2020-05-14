package me.elyar.sqldump;

import me.elyar.sqldump.exceptions.SqlDumpException;
import me.elyar.sqldump.utilities.ValueUtility;

import javax.sql.DataSource;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Elyar Adil
 * @since 1.0
 */
public class SqlDump {

    public static final String STRING_NULL = "NULL";
    public static final String HEX_PREFIX = "0x";
    public static final String LEFT_QUOTATION_MARK = "'";
    public static final String RIGHT_QUOTATION_MARK = "'";
    public static final String SQL_DELIMITER = ";";
    public static final String VALUE_DELIMITER = ", ";
    private final static String SET_UTF8MB4_SQL = "SET NAMES utf8mb4;";
    private final static String DISABLE_FOREIGN_KEY_CHECKS_SQL = "SET FOREIGN_KEY_CHECKS = 0;";
    private final static String ENABLE_FOREIGN_KEY_CHECKS_SQL = "SET FOREIGN_KEY_CHECKS = 1;";
    private final static String SEPARATE_INSERT_SQL_TEMPLATE = "INSERT INTO `%s` VALUES (%s);";
    private final static String COMPACT_INSERT_SQL_PREFIX_TEMPLATE = "INSERT INTO `%s` VALUES ";

    private final static String COMMENT_TABLE_STRUCTURE = "--\n-- Table structure for %s\n--";
    private final static String COMMENT_VIEW_STRUCTURE = "--\n-- View structure for %s\n--";
    private final static String COMMENT_RECORDS = "--\n-- Data of table `%s`\n--";
    private final static String SHOW_TABLES = "SHOW FULL TABLES WHERE Table_type != 'VIEW'";
    private final static String SHOW_VIEWS = "SHOW FULL TABLES WHERE Table_type = 'VIEW'";

    private final Connection connection;


    public SqlDump(DataSource dataSource) throws SQLException {
        this.connection = dataSource.getConnection();

    }

    private void printHead(PrintWriter printWriter) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();

        printWriter.println("-- SqlDump 2020");
        printWriter.println();
        printWriter.println("-- HOST: " +metaData.getURL().split("/")[2]);
        printWriter.println("-- DBMS Name: " + metaData.getDatabaseProductName());
        printWriter.println("-- DBMS Version: " + metaData.getDatabaseProductVersion());
        printWriter.println("-- ----------------------------------------------------");
        printWriter.println();
    }

    public void dumpTable(OutputStream outputStream, String tableName) throws SQLException, SqlDumpException {
        PrintWriter printWriter = new PrintWriter(outputStream);
        printHead(printWriter);

        printDumpPrefix(printWriter);

        dumpTable(tableName, printWriter);

        printDumpSuffix(printWriter);

        printWriter.flush();
        printWriter.close();
    }

    public void dumpView(OutputStream outputStream, String tableName) throws SQLException, SqlDumpException {
        PrintWriter printWriter = new PrintWriter(outputStream);

        printHead(printWriter);

        printDumpPrefix(printWriter);

        dumpView(tableName, printWriter);

        printDumpSuffix(printWriter);

        printWriter.flush();
        printWriter.close();
    }

    private void printDumpSuffix(PrintWriter printWriter) {
        printWriter.println(ENABLE_FOREIGN_KEY_CHECKS_SQL);
    }

    private void printDumpPrefix(PrintWriter printWriter) {
        printWriter.println(SET_UTF8MB4_SQL);
        printWriter.println(DISABLE_FOREIGN_KEY_CHECKS_SQL);
        printWriter.println();
    }

    private void dumpTable(String tableName, PrintWriter printWriter) throws SQLException, SqlDumpException {
        printWriter.println(String.format(COMMENT_TABLE_STRUCTURE, tableName));
        String DROP_TABLE_TEMPLATE = "DROP TABLE IF EXISTS `%s`;";
        String dropTableSql = String.format(DROP_TABLE_TEMPLATE, tableName);
        printWriter.println(dropTableSql);

        printWriter.println(getCreateTableSQL(tableName) + SQL_DELIMITER);
        printWriter.println();
        printWriter.println(String.format(COMMENT_RECORDS, tableName));
        getCompactInsertSQL(tableName, printWriter);
        printWriter.println();
    }

    private void dumpView(String tableName, PrintWriter printWriter) throws SQLException {
        printWriter.println(String.format(COMMENT_VIEW_STRUCTURE, tableName));
        String DROP_TABLE_TEMPLATE = "DROP VIEW IF EXISTS `%s`;";
        String dropTableSql = String.format(DROP_TABLE_TEMPLATE, tableName);
        printWriter.println(dropTableSql);

        printWriter.println(getCreateViewSQL(tableName) + SQL_DELIMITER);
        printWriter.println();
    }

    private void selectDatabase(String database) throws SQLException {
        Statement statement = connection.createStatement();
        String sql = String.format("USE `%s`", database);
        statement.executeQuery(sql).close();
        statement.close();
    }

    /**
     * Get SQL statement {@code String} that creates the specified table.
     *
     * @param tableName name of the table
     * @return SQL statement {@code String}
     * @throws SQLException if a database access error occurs
     */
    public String getCreateTableSQL(String tableName) throws SQLException {
        return getCreateStatement(tableName, "SHOW CREATE TABLE `%s`");
    }

    /**
     * Get SQL statement {@code String} that creates the specified view.
     *
     * @param viewName name of the view
     * @return SQL statement {@code String}
     * @throws SQLException if a database access error occurs
     */
    public String getCreateViewSQL(String viewName) throws SQLException {
        return getCreateStatement(viewName, "SHOW CREATE VIEW `%s`");
    }

    private String getCreateStatement(String viewName, String sqlTemplate) throws SQLException {
        Statement statement = connection.createStatement();
        String sql = String.format(sqlTemplate, viewName);
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
        return getCreateStatement(databaseName, "SHOW CREATE DATABASE IF NOT EXISTS `%s`");
    }

    /**
     * @param procedureName
     * @return
     * @throws SQLException
     */
    public String getCreateProcedureSQL(String procedureName) throws SQLException {
        return getCreateStatement(procedureName, "SHOW CREATE PROCEDURE `%s`");
    }

    public void getInsertSQL(String tableName, PrintWriter printWriter) throws SQLException, SqlDumpException {
        String selectSQL = String.format("SELECT /*!40001 SQL_NO_CACHE */ * FROM %s", tableName);

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(selectSQL);

        while (resultSet.next()) {
            String insertSQL = nextInsert(tableName, resultSet);
            printWriter.println(insertSQL);
        }

        resultSet.close();
        statement.close();
    }

    public void getCompactInsertSQL(String tableName, PrintWriter printWriter) throws SQLException, SqlDumpException {
        String selectSQL = String.format("SELECT /*!40001 SQL_NO_CACHE */ * FROM `%s`", tableName);

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(selectSQL);
        String insertPrefix = String.format(COMPACT_INSERT_SQL_PREFIX_TEMPLATE, tableName);
        printWriter.print(insertPrefix);
        while (resultSet.next()) {
            String value = nextValue(resultSet);
            printWriter.print(value);

            if (!resultSet.isLast()) {
                printWriter.print(VALUE_DELIMITER);
            }
        }
        printWriter.println(SQL_DELIMITER);
        resultSet.close();
        statement.close();
    }

    private String nextInsert(String tableName, ResultSet resultSet) throws SQLException, SqlDumpException {
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

        int columnCount = resultSet.getMetaData().getColumnCount();
        List<String> rowValues = new ArrayList<>(columnCount);
        for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
            int columnType = resultSetMetaData.getColumnType(columnIndex);
            rowValues.add(getLiteralValue(resultSet, columnIndex, columnType));
        }
        String rowValue = String.join(VALUE_DELIMITER, rowValues);
        return String.format(SEPARATE_INSERT_SQL_TEMPLATE, tableName, rowValue);
    }

    String INSERT_VALUE_TEMPLATE = "(%s)";

    private String nextValue(ResultSet resultSet) throws SQLException, SqlDumpException {
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

        int columnCount = resultSet.getMetaData().getColumnCount();
        List<String> rowValues = new ArrayList<>(columnCount);
        for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
            int columnType = resultSetMetaData.getColumnType(columnIndex);
            rowValues.add(getLiteralValue(resultSet, columnIndex, columnType));
        }
        String rowValue = String.join(VALUE_DELIMITER, rowValues);
        return String.format(INSERT_VALUE_TEMPLATE, rowValue);
    }

    /**
     * Get literal value of a column's value.
     *
     * @param resultSet   where values come from
     * @param columnIndex which column value comes from
     * @param columnType  to identify the column's type
     * @return Literal value of given column
     * @throws SQLException     if a database access error occurs
     * @throws SqlDumpException Encounters unsupported SQL type
     */
    private String getLiteralValue(ResultSet resultSet, int columnIndex, int columnType) throws SQLException, SqlDumpException {
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
                return getColumnValue(resultSet, columnIndex, false, false);
            case Types.CHAR:
            case Types.VARCHAR:
            case Types.LONGVARCHAR:
                return getColumnValue(resultSet, columnIndex, true, true);
            // types need to be surrounded by quotation marks without escaping
            case Types.DATE:
            case Types.TIME:
            case Types.TIMESTAMP:
                return getColumnValue(resultSet, columnIndex, false, true);
            // binary types (convert to hex):
            case Types.BINARY:
            case Types.VARBINARY:
            case Types.LONGVARBINARY:
            case Types.BLOB:
            case Types.CLOB:
            case Types.NCLOB:
                return getHexColumnValue(resultSet, columnIndex);
            default:
                throw new SqlDumpException("Unsupported SQL type: " + columnType);
        }
    }

    /**
     * Convert value from column specified by {@code columnIndex} in resultSet
     * to {@code String} for use in "INSERT" statement.
     *
     * @param resultSet   where values come from
     * @param columnIndex which column value comes from
     * @param escaping    whether escaping the value
     * @param quotation   whether surround the value with quotation mark
     * @return value of given column.
     * @throws SQLException if a database access error occurs
     */
    private String getColumnValue(ResultSet resultSet, int columnIndex,
                                  boolean escaping, boolean quotation) throws SQLException {
        String columnValue = resultSet.getString(columnIndex);
        if (resultSet.wasNull()) {
            columnValue = STRING_NULL;
        } else {
            if (escaping) {
                columnValue = ValueUtility.escapeString(columnValue);
            }
            if (quotation) {
                columnValue = LEFT_QUOTATION_MARK + columnValue + RIGHT_QUOTATION_MARK;
            }
        }
        return columnValue;
    }

    /**
     * Convert binary value from column specified by {@code columnIndex}
     * in resultSet to hex {@code String} for use in "INSERT" statement.
     *
     * @param resultSet   where values come from
     * @param columnIndex which column value comes from
     * @return hex value of given column.
     * @throws SQLException if a database access error occurs
     */
    private String getHexColumnValue(ResultSet resultSet, int columnIndex) throws SQLException {
        Blob blob = resultSet.getBlob(columnIndex);
        String columnValue = STRING_NULL;
        if (blob != null) {
            byte[] bytes = blob.getBytes(1, (int) blob.length());
            columnValue = HEX_PREFIX + ValueUtility.bytesToHex(bytes);
        }
        return columnValue;
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
