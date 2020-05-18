package me.elyar.sqldumper.dumper;

import me.elyar.sqldumper.utilities.SqlCommentUtility;
import me.elyar.sqldumper.utilities.SqlQueryUtility;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

public class EventDumper extends Dumper {
    private static final String SHOW_CREATE_EVENT_TEMPLATE = "SHOW CREATE EVENT `%s`";
    private static final String DROP_EVENT_TEMPLATE = "DROP EVENT IF EXISTS `%s`;";
    private static final String COMMENT_EVENT_STRUCTURE = "Event structure for %s";
    public EventDumper(Connection connection, PrintWriter printWriter) {
        super(connection, printWriter);
    }

    @Override
    public void dump(String eventName) throws SQLException {

        String structureHeadComment = String.format(COMMENT_EVENT_STRUCTURE, eventName);
        SqlCommentUtility.printCommentHeader(printWriter, structureHeadComment);
        String dropSql = String.format(DROP_EVENT_TEMPLATE, eventName);
        printWriter.println(dropSql);
        printWriter.println("delimiter ;;");
        printWriter.println(getCreateFunction(eventName) + SQL_DELIMITER);
        printWriter.println(";;");
        printWriter.println("delimiter ;");
        printWriter.println();

        printWriter.flush();
    }

    public String getCreateFunction(String eventName) throws SQLException {
        String sql = String.format(SHOW_CREATE_EVENT_TEMPLATE, eventName);
        return SqlQueryUtility.queryString(this.connection, sql, 4);
    }
}
