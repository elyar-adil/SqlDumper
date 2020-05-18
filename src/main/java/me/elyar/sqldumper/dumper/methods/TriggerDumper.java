package me.elyar.sqldumper.dumper.methods;



import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

public class TriggerDumper extends MethodDumper {
    private static final String SHOW_CREATE_TRIGGER_TEMPLATE = "SHOW CREATE TRIGGER `%s`";
    private static final String DROP_TRIGGER_TEMPLATE = "DROP TRIGGER IF EXISTS `%s`;";
    private static final String COMMENT_TRIGGER_STRUCTURE = "Trigger structure for %s";

    public TriggerDumper(Connection connection, PrintWriter printWriter) {
        super(connection, printWriter);
    }

    @Override
    public void dump(String triggerName) throws SQLException {
        dump(triggerName, COMMENT_TRIGGER_STRUCTURE,DROP_TRIGGER_TEMPLATE, SHOW_CREATE_TRIGGER_TEMPLATE, 3);
    }


}
