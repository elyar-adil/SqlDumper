package me.elyar.sqldumper.utilities;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Wrap "SHOW ..." query. Used to list entities like databases,
 * tables, views...
 */
public class SqlShowUtility {

    private final static String SHOW_TABLES = "SHOW FULL TABLES WHERE Table_type != 'VIEW'"; // name at column 1
    private final static String SHOW_VIEWS = "SHOW FULL TABLES WHERE Table_type = 'VIEW'"; // name at column 1
    private final static String SHOW_DATABASES = "SHOW DATABASES"; // name at column 1
    private final static String SHOW_FUNCTIONS = "SHOW FUNCTION STATUS WHERE Db = '%s'"; // name at column 2
    private final static String SHOW_TRIGGERS = "SHOW TRIGGERS"; // name at column 1
    private final static String SHOW_EVENTS = "SHOW EVENTS WHERE Db = '%s'"; // name at column 2

//    private final static String SHOW_TRIGGERS_OF_NAME = "SHOW TRIGGERS WHERE `Table` LIKE '%s'"; // name at column 1
//    private final static String SHOW_ALL_FUNCTIONS = "SHOW FUNCTION STATUS"; // name at column 2
//    private final static String SHOW_ALL_EVENTS = "SHOW EVENTS"; // name at column 2

    public static List<String> listDatabase(Connection connection) throws SQLException {
        return SqlQueryUtility.queryStringList(connection, SHOW_DATABASES, 1);
    }

    public static List<String> listTable(Connection connection, String database) throws SQLException {
        SqlQueryUtility.selectDatabase(connection, database);
        return SqlQueryUtility.queryStringList(connection, SHOW_TABLES, 1);
    }

    public static List<String> listView(Connection connection, String database) throws SQLException {
        SqlQueryUtility.selectDatabase(connection, database);
        return SqlQueryUtility.queryStringList(connection, SHOW_VIEWS, 1);
    }

    public static List<String> listFunction(Connection connection, String database) throws SQLException {
        return SqlQueryUtility.queryStringList(connection, String.format(SHOW_FUNCTIONS, database), 2);
    }

    public static List<String> listTrigger(Connection connection, String database) throws SQLException {
        SqlQueryUtility.selectDatabase(connection, database);
        return SqlQueryUtility.queryStringList(connection, SHOW_TRIGGERS, 1);
    }

    public static List<String> listEvent(Connection connection, String database) throws SQLException {
        return SqlQueryUtility.queryStringList(connection, String.format(SHOW_EVENTS, database), 2);
    }
}
