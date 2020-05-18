package me.elyar.sqldumper.dumper;

import me.elyar.sqldumper.utilities.SqlCommentUtility;
import me.elyar.sqldumper.utilities.SqlQueryUtility;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

public class FunctionDumper extends Dumper {
    private static final String SHOW_CREATE_FUNCTION_TEMPLATE = "SHOW CREATE FUNCTION `%s`";
    private static final String DROP_FUNCTION_TEMPLATE = "DROP FUNCTION IF EXISTS `%s`;";
    private static final String COMMENT_FUNCTION_STRUCTURE = "Function structure for %s";

    public FunctionDumper(Connection connection, PrintWriter printWriter) {
        super(connection, printWriter);
    }

    @Override
    public void dump(String functionName) throws SQLException {
        String structureHeadComment = String.format(COMMENT_FUNCTION_STRUCTURE, functionName);
        SqlCommentUtility.printCommentHeader(printWriter, structureHeadComment);
        String dropSql = String.format(DROP_FUNCTION_TEMPLATE, functionName);
        printWriter.println(dropSql);
        printWriter.println("delimiter ;;");
        printWriter.println(getCreateFunction(functionName) + SQL_DELIMITER);
        printWriter.println(";;");
        printWriter.println("delimiter ;");
        printWriter.println();

        printWriter.flush();
    }

    public String getCreateFunction(String functionName) throws SQLException {
        String sql = String.format(SHOW_CREATE_FUNCTION_TEMPLATE, functionName);
        return SqlQueryUtility.queryString(this.connection, sql, 3);
    }
}
