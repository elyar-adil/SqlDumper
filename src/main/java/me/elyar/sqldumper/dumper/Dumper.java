package me.elyar.sqldumper.dumper;


import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Abstract dumper class.
 *
 * @author Elyar Adil
 * @since 1.0
 */
public abstract class Dumper {
    // sql delimiter
    public static final String SQL_DELIMITER = ";";

    // sql connection
    protected Connection connection;
    // where to dump results
    protected PrintWriter printWriter;

    /**
     * Constructor
     *
     * @param connection  set connection of dumper
     * @param printWriter set printWriter of dumper
     */
    public Dumper(Connection connection, PrintWriter printWriter) {
        this.connection = connection;
        this.printWriter = printWriter;
    }

    /**
     * Abstract dump method
     * @param targetName name of the target
     * @throws SQLException if a database access error occurs
     */
    public abstract void dump(String targetName) throws SQLException;

}
