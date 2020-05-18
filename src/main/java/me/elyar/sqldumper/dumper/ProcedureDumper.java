package me.elyar.sqldumper.dumper;

import me.elyar.sqldumper.utilities.SqlCommentUtility;
import me.elyar.sqldumper.utilities.SqlQueryUtility;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

public class ProcedureDumper extends Dumper {
    private static final String SHOW_CREATE_PROCEDURE_TEMPLATE = "SHOW CREATE PROCEDURE `%s`";
    private static final String DROP_PROCEDURE_TEMPLATE = "DROP PROCEDURE IF EXISTS `%s`;";
    private static final String COMMENT_PROCEDURE_STRUCTURE = "Procedure structure for %s";

    public ProcedureDumper(Connection connection, PrintWriter printWriter) {
        super(connection, printWriter);
    }

    @Override
    public void dump(String procedureName) throws SQLException {
        String structureHeadComment = String.format(COMMENT_PROCEDURE_STRUCTURE, procedureName);
        SqlCommentUtility.printCommentHeader(printWriter, structureHeadComment);
        String dropSql = String.format(DROP_PROCEDURE_TEMPLATE, procedureName);
        printWriter.println(dropSql);
        printWriter.println("delimiter ;;");
        printWriter.println(getCreateProcedureSql(procedureName) + SQL_DELIMITER);
        printWriter.println(";;");
        printWriter.println("delimiter ;");
        printWriter.println();

        printWriter.flush();
    }

    public String getCreateProcedureSql(String procedureName) throws SQLException {
        String sql = String.format(SHOW_CREATE_PROCEDURE_TEMPLATE, procedureName);
        return SqlQueryUtility.queryString(this.connection, sql, 3);
    }
}
