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

    private final static String SHOW_TABLES = "SHOW FULL TABLES WHERE Table_type != 'VIEW'"; // name at column 1
    private final static String SHOW_VIEWS = "SHOW FULL TABLES WHERE Table_type = 'VIEW'"; // name at column 1
    private final static String SHOW_DATABASES = "SHOW DATABASES"; // name at column 1
    private final static String SHOW_FUNCTIONS ="SHOW FUNCTION STATUS WHERE Db = '%s'"; // name at column 2
    private final static String SHOW_ALL_FUNCTIONS ="SHOW FUNCTION STATUS"; // name at column 2
    private final static String SHOW_TRIGGERS = "SHOW TRIGGERS"; // name at column 1
    private final static String SHOW_EVENTS = "SHOW EVENTS WHERE Db = '%s'"; // name at column 2
    private final static String SHOW_ALL_EVENTS = "SHOW EVENTS"; // name at column 2

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

        List<String> tableList = listTable(databaseName);
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
     * Get
     *
     * @return
     * @throws SQLException
     */
    public List<String> listDatabase() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(SHOW_DATABASES);
        List<String> databaseList = getStringListFromResultSet(resultSet);
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
    public List<String> listTable(String database) throws SQLException {
        selectDatabase(database);
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(SHOW_TABLES);
        List<String> tableList = getStringListFromResultSet(resultSet);
        resultSet.close();
        statement.close();
        return tableList;
    }

    private List<String> getStringListFromResultSet(ResultSet resultSet) throws SQLException {
        List<String> stringList = new ArrayList<>();
        while (resultSet.next()) {
            String table = resultSet.getString(1);
            stringList.add(table);
        }
        return stringList;
    }


}
