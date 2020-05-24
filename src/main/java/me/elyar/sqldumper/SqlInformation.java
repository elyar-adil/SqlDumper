package me.elyar.sqldumper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Used to retrieve information from the database server.
 * List entities like databases, tables, views...
 *
 */
public class SqlInformation {

    private final static String SHOW_TABLES = "SHOW FULL TABLES WHERE Table_type != 'VIEW'"; // name at column 1
    private final static String SHOW_VIEWS = "SHOW FULL TABLES WHERE Table_type = 'VIEW'"; // name at column 1
    private final static String SHOW_DATABASES = "SHOW DATABASES"; // name at column 1
    private final static String SHOW_FUNCTIONS = "SHOW FUNCTION STATUS WHERE Db = '%s'"; // name at column 2
    private final static String SHOW_ALL_FUNCTIONS = "SHOW FUNCTION STATUS"; // name at column 2
    private final static String SHOW_TRIGGERS = "SHOW TRIGGERS"; // name at column 1
    private final static String SHOW_TRIGGERS_OF_NAME = "SHOW TRIGGERS WHERE `Table` LIKE '%s'"; // name at column 1
    private final static String SHOW_EVENTS = "SHOW EVENTS WHERE Db = '%s'"; // name at column 2
    private final static String SHOW_ALL_EVENTS = "SHOW EVENTS"; // name at column 2
    private final Connection connection;

    public SqlInformation(Connection connection) {
        this.connection = connection;
    }

    public List<String> listDatabase() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(SHOW_DATABASES);
        List<String> databaseList = getStringListFromResultSet(resultSet, 1);
        resultSet.close();
        statement.close();
        return databaseList;
    }


    /**
     * Get
     *
     * @return
     * @throws SQLException
     */
    public List<String> listTable(String database) throws SQLException {
        selectDatabase(database);
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(SHOW_TABLES);
        List<String> tableList = getStringListFromResultSet(resultSet, 1);
        resultSet.close();
        statement.close();
        return tableList;
    }

    public void selectDatabase(String database) throws SQLException {
        Statement statement = connection.createStatement();
        String sql = String.format("USE `%s`", database);
        statement.executeQuery(sql).close();
        statement.close();
    }

    private List<String> getStringListFromResultSet(ResultSet resultSet, int columnIndex) throws SQLException {
        List<String> stringList = new ArrayList<>();
        while (resultSet.next()) {
            String table = resultSet.getString(columnIndex);
            stringList.add(table);
        }
        return stringList;
    }
}
