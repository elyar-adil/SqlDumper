package me.elyar;

import java.sql.*;

public class Main {

    public static void main(String[] args) throws ClassNotFoundException, SQLException {

        SQLDump sqlDump = new SQLDump("jdbc:mysql://localhost/activiti?user=root&password=password&serverTimezone=GMT%2B8");
        sqlDump.getInsertSQL("act_ru_task");

    }


}
