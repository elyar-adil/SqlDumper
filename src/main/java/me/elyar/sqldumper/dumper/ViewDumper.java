package me.elyar.sqldumper.dumper;

import me.elyar.sqldumper.utilities.SqlCommentUtility;
import me.elyar.sqldumper.utilities.SqlQueryUtility;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

public class ViewDumper extends Dumper {
    public ViewDumper(Connection connection, PrintWriter printWriter) {
        super(connection, printWriter);
    }

    private final static String COMMENT_VIEW_STRUCTURE = "View structure for %s";

    @Override
    public void dump(String viewName) throws SQLException {
        String structureHeadComment = String.format(COMMENT_VIEW_STRUCTURE, viewName);
        SqlCommentUtility.printCommentHeader(printWriter, structureHeadComment);
        String DROP_TABLE_TEMPLATE = "DROP VIEW IF EXISTS `%s`;";
        String dropTableSql = String.format(DROP_TABLE_TEMPLATE, viewName);
        printWriter.println(dropTableSql);

        printWriter.println(getCreateViewSQL(viewName) + SQL_DELIMITER);
        printWriter.println();
    }

    /**
     * Get SQL statement {@code String} that creates the specified view.
     *
     * @param viewName name of the view
     * @return SQL statement {@code String}
     * @throws SQLException if a database access error occurs
     */
    public String getCreateViewSQL(String viewName) throws SQLException {
        return SqlQueryUtility.queryString(this.connection, String.format("SHOW CREATE VIEW `%s`", viewName), 2);
    }
}
