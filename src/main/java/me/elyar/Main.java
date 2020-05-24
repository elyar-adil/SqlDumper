package me.elyar;

import com.mysql.cj.jdbc.MysqlDataSource;
import me.elyar.sqldumper.SqlDumper;
import me.elyar.sqldumper.dumper.ViewDumper;
import me.elyar.sqldumper.exceptions.SqlDumperException;

import java.io.PrintWriter;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws SQLException, SqlDumperException {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setUrl("jdbc:mysql://localhost:3306/sakila?user=root&password=password&serverTimezone=GMT%2B8");

        SqlDumper sqlDumper = new SqlDumper("jdbc:mysql://localhost:3306/sakila?user=root&password=password&serverTimezone=GMT%2B8");
        sqlDumper.dumpDatabase("sakila", System.out);
    }


}
