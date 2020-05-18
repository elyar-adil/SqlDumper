package me.elyar.sqldumper.dumper;


import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class Dumper {
    public static final String SQL_DELIMITER = ";";

    protected Connection connection;
    protected PrintWriter printWriter;

    public Dumper(Connection connection, PrintWriter printWriter) {
        this.connection = connection;
        this.printWriter = printWriter;
    }

    public abstract void dump(String targetName) throws SQLException;

}
