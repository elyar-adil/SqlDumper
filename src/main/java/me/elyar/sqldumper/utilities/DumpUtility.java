package me.elyar.sqldumper.utilities;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DumpUtility {
    private final static String SET_UTF8MB4_SQL = "SET NAMES utf8mb4;";
    private final static String DISABLE_FOREIGN_KEY_CHECKS_SQL = "SET FOREIGN_KEY_CHECKS = 0;";
    private final static String ENABLE_FOREIGN_KEY_CHECKS_SQL = "SET FOREIGN_KEY_CHECKS = 1;";

    public static void printHeadInfo(Connection connection, PrintWriter printWriter) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();

        SqlCommentUtility.printCommentLine(printWriter, "SqlDump v1.0");
        SqlCommentUtility.printEmptyLine(printWriter);
        SqlCommentUtility.printCommentLine(printWriter, "HOST: " + metaData.getURL().split("/")[2]);
        SqlCommentUtility.printCommentLine(printWriter, "DBMS Name: " + metaData.getDatabaseProductName());
        SqlCommentUtility.printCommentLine(printWriter, "BMS Version: " + metaData.getDatabaseProductVersion());
        SqlCommentUtility.printCommentLineSeparator(printWriter);
        SqlCommentUtility.printEmptyLine(printWriter);
    }

    public static void printDumpPrefix(PrintWriter printWriter) {
        printWriter.println(SET_UTF8MB4_SQL);
        printWriter.println(DISABLE_FOREIGN_KEY_CHECKS_SQL);
        printWriter.println();
    }

    public static void printDumpSuffix(PrintWriter printWriter) {
        printWriter.println(ENABLE_FOREIGN_KEY_CHECKS_SQL);
        printWriter.println();
    }


    public static void printTailInfo(PrintWriter printWriter) {
        printWriter.println("-- Dump completed on " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }
}
