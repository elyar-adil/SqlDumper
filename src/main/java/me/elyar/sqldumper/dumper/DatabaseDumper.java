package me.elyar.sqldumper.dumper;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseDumper extends Dumper {
    public DatabaseDumper(Connection connection, PrintWriter printWriter) {
        super(connection, printWriter);
    }

    @Override
    public void dump(String databaseName)  throws SQLException {

    }
}