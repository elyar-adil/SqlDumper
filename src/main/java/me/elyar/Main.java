package me.elyar;

import com.mysql.cj.jdbc.MysqlDataSource;
import me.elyar.sqldump.SqlDump;
import me.elyar.sqldump.exceptions.SqlDumpException;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws SQLException,  SqlDumpException {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setUrl("jdbc:mysql://localhost:3306/sakila?user=root&password=password&serverTimezone=GMT%2B8");

        SqlDump sqlDump = new SqlDump(dataSource);
        sqlDump.dumpDatabase("sakila", System.out );
    }


}
