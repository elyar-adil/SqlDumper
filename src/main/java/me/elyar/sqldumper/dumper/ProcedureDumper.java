package me.elyar.sqldumper.dumper;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

public class ProcedureDumper extends MethodDumper {
    private static final String SHOW_CREATE_PROCEDURE_TEMPLATE = "SHOW CREATE PROCEDURE `%s`";
    private static final String DROP_PROCEDURE_TEMPLATE = "DROP PROCEDURE IF EXISTS `%s`;";
    private static final String COMMENT_PROCEDURE_STRUCTURE = "Procedure structure for %s";

    public ProcedureDumper(Connection connection, PrintWriter printWriter) {
        super(connection, printWriter);
    }

    @Override
    public void dump(String procedureName) throws SQLException {
        dump(procedureName, COMMENT_PROCEDURE_STRUCTURE, DROP_PROCEDURE_TEMPLATE, SHOW_CREATE_PROCEDURE_TEMPLATE, 3);
    }

}
