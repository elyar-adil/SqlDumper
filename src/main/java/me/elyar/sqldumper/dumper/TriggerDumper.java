package me.elyar.sqldumper.dumper;

import me.elyar.sqldumper.utilities.SqlCommentUtility;
import me.elyar.sqldumper.utilities.SqlQueryUtility;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

public class TriggerDumper extends Dumper {
    private static final String SHOW_CREATE_TRIGGER_TEMPLATE = "SHOW CREATE TRIGGER `%s`";
    private static final String DROP_TRIGGER_TEMPLATE = "DROP TRIGGER IF EXISTS `%s`;";
    private static final String COMMENT_TRIGGER_STRUCTURE = "Trigger structure for %s";

    public TriggerDumper(Connection connection, PrintWriter printWriter) {
        super(connection, printWriter);
    }

    @Override
    public void dump(String triggerName) throws SQLException {
        String structureHeadComment = String.format(COMMENT_TRIGGER_STRUCTURE, triggerName);
        SqlCommentUtility.printCommentHeader(printWriter, structureHeadComment);
        String dropSql = String.format(DROP_TRIGGER_TEMPLATE, triggerName);
        printWriter.println(dropSql);
        printWriter.println("delimiter ;;");
        printWriter.println(getCreateTriggerSql(triggerName) + SQL_DELIMITER);
        printWriter.println(";;");
        printWriter.println("delimiter ;");
        printWriter.println();

        printWriter.flush();
    }

    public String getCreateTriggerSql(String triggerName) throws SQLException {
        String sql = String.format(SHOW_CREATE_TRIGGER_TEMPLATE, triggerName);
        return SqlQueryUtility.queryString(this.connection, sql, 3);
    }
}
