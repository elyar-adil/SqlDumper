package me.elyar;

import com.mysql.cj.jdbc.MysqlDataSource;
import me.elyar.sqldumper.SqlDumper;
import me.elyar.sqldumper.dumper.TableDumper;
import me.elyar.sqldumper.dumper.ViewDumper;
import me.elyar.sqldumper.exceptions.SqlDumperException;
import me.elyar.sqldumper.utilities.SqlCommentUtility;

import java.io.PrintWriter;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws SQLException, SqlDumperException {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setUrl("jdbc:mysql://localhost:3306/sakila?user=root&password=password&serverTimezone=GMT%2B8");

        PrintWriter printWriter = new PrintWriter(System.out);
        ViewDumper viewDumper = new ViewDumper(dataSource.getConnection(),printWriter);

        viewDumper.dump("actor_info");
        printWriter.flush();
    }


}
