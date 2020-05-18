package me.elyar.sqldumper.dumper;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

public class EventDumper extends MethodDumper {
    private static final String SHOW_CREATE_EVENT_TEMPLATE = "SHOW CREATE EVENT `%s`";
    private static final String DROP_EVENT_TEMPLATE = "DROP EVENT IF EXISTS `%s`;";
    private static final String COMMENT_EVENT_STRUCTURE = "Event structure for %s";

    public EventDumper(Connection connection, PrintWriter printWriter) {
        super(connection, printWriter);
    }

    @Override
    public void dump(String eventName) throws SQLException {
        dump(eventName, COMMENT_EVENT_STRUCTURE, DROP_EVENT_TEMPLATE, SHOW_CREATE_EVENT_TEMPLATE, 4);
    }

}
