package me.elyar.sqldumper.dumper;


import me.elyar.sqldumper.exceptions.SqlDumperException;
import me.elyar.sqldumper.utilities.SqlCommentUtility;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class Dumper {
    public static final String SQL_DELIMITER = ";";

    protected Connection connection;
    protected PrintWriter printWriter;

    public Dumper(Connection connection, PrintWriter printWriter) {
        this.connection = connection;
        this.printWriter = printWriter;
    }

    public abstract void dump(String targetName) throws SQLException;

    /**
     * Print comment before dump.
     *
     * @throws SQLException if a database access error occurs
     */
    protected void printHeadComment() throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();

        SqlCommentUtility.printCommentLine(printWriter, "SqlDump v1.0");
        SqlCommentUtility.printEmptyLine(printWriter);
        SqlCommentUtility.printCommentLine(printWriter, "HOST: " + metaData.getURL().split("/")[2]);
        SqlCommentUtility.printCommentLine(printWriter, "DBMS Name: " + metaData.getDatabaseProductName());
        SqlCommentUtility.printCommentLine(printWriter, "BMS Version: " + metaData.getDatabaseProductVersion());
        SqlCommentUtility.printCommentLineSeparator(printWriter);
        SqlCommentUtility.printEmptyLine(printWriter);
    }

    /**
     * Print comment after dump.
     *
     * @throws SQLException if a database access error occurs
     */
    protected void printTailComment(PrintWriter printWriter) {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        SqlCommentUtility.printCommentLine(printWriter, "Dump completed on "+ time);
    }
}
