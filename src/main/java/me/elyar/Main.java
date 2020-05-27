package me.elyar;

import com.mysql.cj.jdbc.MysqlDataSource;
import me.elyar.sqldumper.SqlDumper;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws SQLException {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setUrl("jdbc:mysql://localhost:3306/sakila?user=root&password=password&serverTimezone=GMT%2B8");

        SqlDumper sqlDumper = new SqlDumper("jdbc:mysql://localhost:3306/sakila?user=root&password=password&serverTimezone=GMT%2B8");
        sqlDumper.dumpAllDatabase(System.out);
    }


}
