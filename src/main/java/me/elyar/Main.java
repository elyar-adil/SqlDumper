package me.elyar;

import com.mysql.cj.jdbc.MysqlDataSource;
import me.elyar.sqldump.SqlDump;
import me.elyar.sqldump.exceptions.SqlDumpException;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws SQLException, SqlDumpException {
        MysqlDataSource mysqlDataSource = new MysqlDataSource();
        mysqlDataSource.setUrl("jdbc:mysql://localhost/activiti?user=root&password=password&serverTimezone=GMT%2B8");
        System.out.println(mysqlDataSource.getDatabaseName());
        SqlDump sqlDump = new SqlDump(mysqlDataSource);
        sqlDump.dumpView(System.out, "as");
    }


}
