package me.elyar.sqldumper.dumper;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * This class is used to dump view.
 *
 * @author Elyar Adil
 * @since 1.0
 */
public class ViewDumper extends MethodDumper {

    private static final String SHOW_CREATE_VIEW_TEMPLATE = "SHOW CREATE VIEW `%s`";
    private static final String DROP_VIEW_TEMPLATE = "DROP VIEW IF EXISTS `%s`;";
    private static final String COMMENT_VIEW_STRUCTURE = "View structure for %s";


    public ViewDumper(Connection connection, PrintWriter printWriter) {
        super(connection, printWriter);
    }

    /**
     * Dump view.
     *
     * @param viewName name of the view
     * @throws SQLException if a database access error occurs
     */
    @Override
    public void dump(String viewName) throws SQLException {
        dump(viewName, COMMENT_VIEW_STRUCTURE,DROP_VIEW_TEMPLATE, SHOW_CREATE_VIEW_TEMPLATE, 2);
    }

}
