package me.elyar.sqldumper;

import com.mysql.cj.jdbc.MysqlDataSource;
import me.elyar.sqldumper.dumper.TableDumper;
import me.elyar.sqldumper.utilities.DumpUtility;

import javax.sql.DataSource;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.*;
import java.util.List;

/**
 * Dump database using JDBC without binary any dependencies.
 *
 * @author Elyar Adil
 * @since 1.0
 */
public class SqlDumper {

    private final Connection connection;

    public SqlDumper(DataSource dataSource) throws SQLException {
        if (dataSource instanceof MysqlDataSource) {
            MysqlDataSource mysqlDataSource = (MysqlDataSource) dataSource;
            boolean yearIsDateType = mysqlDataSource.getYearIsDateType();
            mysqlDataSource.setYearIsDateType(false);
            this.connection = mysqlDataSource.getConnection();
            mysqlDataSource.setYearIsDateType(yearIsDateType);
        } else {
            this.connection = null;
            throw new UnsupportedOperationException("Unsupported DataSource!");
        }
    }
    public SqlDumper(String url, String user, String password) throws SQLException {
        MysqlDataSource mysqlDataSource = new MysqlDataSource();
        mysqlDataSource.setPassword(password);
        mysqlDataSource.setUrl(url);
        mysqlDataSource.setUser(user);
        mysqlDataSource.setYearIsDateType(false);
        this.connection = mysqlDataSource.getConnection();
    }

    public SqlDumper(String url) throws SQLException {
        MysqlDataSource mysqlDataSource = new MysqlDataSource();
        mysqlDataSource.setUrl(url);
        mysqlDataSource.setYearIsDateType(false);

        this.connection = mysqlDataSource.getConnection();
    }


    public void dumpDatabase(String databaseName, OutputStream outputStream) throws SQLException {
        PrintWriter printWriter = new PrintWriter(outputStream);
        DumpUtility.printHeadInfo(connection, printWriter);
        DumpUtility.printDumpPrefix(printWriter);

        String createTableStatement = "CREATE DATABASE /*!32312 IF NOT EXISTS*/ `%s` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;\n";

        printWriter.println(String.format(createTableStatement, databaseName));
        printWriter.println(String.format("USE `%s`;", databaseName));
        SqlInformation sqlInformation = new SqlInformation(connection);

        List<String> tableList = sqlInformation.listTable(databaseName);
        TableDumper tableDumper = new TableDumper(connection, printWriter);
        for (String table : tableList) {
            tableDumper.dump(table);
        }

        DumpUtility.printDumpSuffix(printWriter);
        printWriter.flush();
        printWriter.close();
    }





}
