package me.elyar.sqldumper.dumper;

import me.elyar.sqldumper.utilities.SqlCommentUtility;
import me.elyar.sqldumper.utilities.SqlQueryUtility;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class MethodDumper extends Dumper {
    public MethodDumper(Connection connection, PrintWriter printWriter) {
        super(connection, printWriter);
    }

    @Override
    public abstract void dump(String targetName) throws SQLException;


    public void dump(String methodName,String commentTemplate,String dropTemplate, String createTemplate, int createIndex) throws SQLException {
        String structureHeadComment = String.format(commentTemplate, methodName);
        SqlCommentUtility.printCommentHeader(printWriter, structureHeadComment);
        String dropSql = String.format(dropTemplate, methodName);
        printWriter.println(dropSql);
        printWriter.println("delimiter ;;");
        String sql = String.format(createTemplate, methodName);
        printWriter.println(SqlQueryUtility.queryString(this.connection, sql, createIndex) + SQL_DELIMITER);
        printWriter.println(";;");
        printWriter.println("delimiter ;");
        printWriter.println();

        printWriter.flush();
    }
}
