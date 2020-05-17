package me.elyar.sqldumper.dumper;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

public class ProcedureDumper extends Dumper {
    public ProcedureDumper(Connection connection, PrintWriter printWriter) {
        super(connection, printWriter);
    }

    @Override
    public void dump(String procedureName)  throws SQLException {

    }
}
