package me.elyar.sqldumper;

import com.mysql.cj.jdbc.MysqlDataSource;
import me.elyar.sqldumper.dumper.DatabaseDumper;
import me.elyar.sqldumper.dumper.TableDumper;
import me.elyar.sqldumper.dumper.ViewDumper;
import me.elyar.sqldumper.dumper.methods.EventDumper;
import me.elyar.sqldumper.dumper.methods.FunctionDumper;
import me.elyar.sqldumper.dumper.methods.ProcedureDumper;
import me.elyar.sqldumper.dumper.methods.TriggerDumper;
import me.elyar.sqldumper.utilities.DumpInfoUtility;
import me.elyar.sqldumper.utilities.SqlQueryUtility;
import me.elyar.sqldumper.utilities.SqlShowUtility;

import javax.sql.DataSource;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.*;
import java.util.List;
import java.util.Set;

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


    public void dumpAllDatabase(OutputStream outputStream, Set<String> exceptSet) throws SQLException {
        PrintWriter printWriter = new PrintWriter(outputStream);
        DumpInfoUtility.printHeadInfo(connection, printWriter);
        DumpInfoUtility.printDumpPrefix(printWriter);

        List<String> databaseList = SqlShowUtility.listDatabase(connection);
        for(String databaseName : databaseList) {
            if(exceptSet != null && !exceptSet.contains(databaseName))
            _dumpDatabase(databaseName, printWriter);
        }
        DumpInfoUtility.printDumpSuffix(printWriter);
        DumpInfoUtility.printTailInfo(printWriter);
        printWriter.flush();
        printWriter.close();
    }
    public void dumpAllDatabase(OutputStream outputStream) throws SQLException {
        dumpAllDatabase(outputStream, null);
    }

    public void dumpDatabase(String databaseName, OutputStream outputStream) throws SQLException {
        PrintWriter printWriter = new PrintWriter(outputStream);
        DumpInfoUtility.printHeadInfo(connection, printWriter);
        DumpInfoUtility.printDumpPrefix(printWriter);

        _dumpDatabase(databaseName, printWriter);

        DumpInfoUtility.printDumpSuffix(printWriter);
        DumpInfoUtility.printTailInfo(printWriter);
        printWriter.flush();
        printWriter.close();
    }

    private void _dumpDatabase(String databaseName, PrintWriter printWriter) throws SQLException {
        SqlQueryUtility.selectDatabase(connection, databaseName);

        DatabaseDumper databaseDumper = new DatabaseDumper(connection, printWriter);
        TableDumper tableDumper = new TableDumper(connection, printWriter);
        TriggerDumper triggerDumper = new TriggerDumper(connection, printWriter);
        ViewDumper viewDumper = new ViewDumper(connection, printWriter);
        FunctionDumper functionDumper = new FunctionDumper(connection, printWriter);
        ProcedureDumper procedureDumper = new ProcedureDumper(connection, printWriter);
        EventDumper eventDumper = new EventDumper(connection, printWriter);

        String createDatabaseSql = databaseDumper.getCreateDatabaseSQL(databaseName);
        printWriter.println(createDatabaseSql);
        printWriter.println(String.format("USE `%s`;", databaseName));

        List<String> tableList = SqlShowUtility.listTable(connection, databaseName);
        for (String table : tableList) {
            tableDumper.dump(table);
            List<String> triggerList = SqlShowUtility.listTriggerOfTable(connection, databaseName, table);
            for (String trigger : triggerList) {
                triggerDumper.dump(trigger);
            }
        }

        List<String> viewList = SqlShowUtility.listView(connection, databaseName);
        for (String view : viewList) {
            viewDumper.dump(view);
        }

        List<String> functionList = SqlShowUtility.listFunction(connection, databaseName);
        for (String function : functionList) {
            functionDumper.dump(function);
        }

        List<String> procedureList = SqlShowUtility.listProcedure(connection, databaseName);
        for (String procedure : procedureList) {
            procedureDumper.dump(procedure);
        }

        List<String> eventList = SqlShowUtility.listEvent(connection, databaseName);
        for (String event : eventList) {
            eventDumper.dump(event);
        }
    }


}
