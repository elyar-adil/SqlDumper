package me.elyar.sqldumper.dumper;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

public class FunctionDumper extends MethodDumper {
    private static final String SHOW_CREATE_FUNCTION_TEMPLATE = "SHOW CREATE FUNCTION `%s`";
    private static final String DROP_FUNCTION_TEMPLATE = "DROP FUNCTION IF EXISTS `%s`;";
    private static final String COMMENT_FUNCTION_STRUCTURE = "Function structure for %s";

    public FunctionDumper(Connection connection, PrintWriter printWriter) {
        super(connection, printWriter);
    }

    @Override
    public void dump(String functionName) throws SQLException {
        dump(functionName, COMMENT_FUNCTION_STRUCTURE, DROP_FUNCTION_TEMPLATE, SHOW_CREATE_FUNCTION_TEMPLATE, 3);
    }

}
