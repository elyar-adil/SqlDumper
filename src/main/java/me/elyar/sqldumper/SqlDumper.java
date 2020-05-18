package me.elyar.sqldumper;

import com.mysql.cj.jdbc.MysqlDataSource;
import me.elyar.sqldumper.exceptions.SqlDumperException;
import me.elyar.sqldumper.utilities.DumpUtility;

import javax.sql.DataSource;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Dump database using JDBC without binary any dependencies.
 *
 * @author Elyar Adil
 * @since 1.0
 */
public class SqlDumper {

    private final static String SHOW_TABLES = "SHOW FULL TABLES WHERE Table_type != 'VIEW'";
    private final static String SHOW_VIEWS = "SHOW FULL TABLES WHERE Table_type = 'VIEW'";

    private final Connection connection;

    public SqlDumper(DataSource dataSource) throws SQLException, SqlDumperException {
        if (dataSource instanceof MysqlDataSource) {
            MysqlDataSource mysqlDataSource = (MysqlDataSource) dataSource;
            boolean yearIsDateType = mysqlDataSource.getYearIsDateType();
            mysqlDataSource.setYearIsDateType(false);
            this.connection = mysqlDataSource.getConnection();
            mysqlDataSource.setYearIsDateType(yearIsDateType);
        } else {
            this.connection = null;
            throw new SqlDumperException("Unsupported DataSource!");
        }
    }



    public void dumpDatabase(String databaseName, OutputStream outputStream) throws SQLException, SqlDumperException {
        PrintWriter printWriter = new PrintWriter(outputStream);
        DumpUtility.printHeadInfo(connection, printWriter);
        DumpUtility.printDumpPrefix(printWriter);

        String createTableStatement = "CREATE DATABASE /*!32312 IF NOT EXISTS*/ `%s` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;\n";

        printWriter.println(String.format(createTableStatement, databaseName));
        printWriter.println(String.format("USE `%s`;", databaseName));

        List<String> tableList = getTables(databaseName);
        for (String table : tableList) {
//            dumpTable(table, printWriter);
        }
        DumpUtility.printDumpSuffix(printWriter);
        printWriter.flush();
        printWriter.close();
    }


    public void selectDatabase(String database) throws SQLException {
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
     * Get
     *
     * @return
     * @throws SQLException
     */
    public List<String> getTables(String database) throws SQLException {
        selectDatabase(database);
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(SHOW_TABLES);
        List<String> tableList = new ArrayList<>();
        while (resultSet.next()) {
            String table = resultSet.getString(1);
            tableList.add(table);
        }
        resultSet.close();
        statement.close();
        return tableList;
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
