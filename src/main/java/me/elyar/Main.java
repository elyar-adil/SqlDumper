package me.elyar;

import java.io.IOException;
import java.sql.*;

public class Main {

    public static void main(String[] args) throws ClassNotFoundException, SQLException, SQLDumpException, IOException {

        SQLDump sqlDump = new SQLDump("jdbc:mysql://localhost/activiti?user=root&password=password&serverTimezone=GMT%2B8");
        System.out.println(sqlDump.cv("newtable"));

    }


}
