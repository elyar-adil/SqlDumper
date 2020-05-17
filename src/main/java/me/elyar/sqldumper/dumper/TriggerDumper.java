package me.elyar.sqldumper.dumper;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

public class TriggerDumper extends Dumper {
    public TriggerDumper(Connection connection, PrintWriter printWriter) {
        super(connection, printWriter);
    }


    @Override
    public void dump(String triggerName)  throws SQLException {

    }
}
