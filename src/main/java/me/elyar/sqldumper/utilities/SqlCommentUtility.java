package me.elyar.sqldumper.utilities;

import java.io.PrintWriter;

public class SqlCommentUtility {

    private static final String COMMENT_PREFIX = "-- ";
    private static final String COMMENT_SEPARATOR = "----------------------------------------------------";
    private static final String COMMENT_BLOCK_PREFIX = "/*";
    private static final String COMMENT_BLOCK_SUFFIX = "*/";
    private static final String COMMENT_BLOCK_PADDING = "  ";

    public static void printCommentLine(PrintWriter printWriter, String comment) {
        printWriter.println(COMMENT_PREFIX + comment);
    }

    public static void printCommentLine(PrintWriter printWriter) {
        printWriter.println(COMMENT_PREFIX);
    }

    public static void printCommentLineSeparator(PrintWriter printWriter) {
        printWriter.println(COMMENT_PREFIX + COMMENT_SEPARATOR);
    }

    public static void printCommentHeader(PrintWriter printWriter, String commentHeader) {
        printCommentLine(printWriter);
        printCommentLine(printWriter, commentHeader);
        printCommentLine(printWriter);
    }

    public static void printEmptyLine(PrintWriter printWriter) {
        printWriter.println();
    }

    public static void printCommentBlockLine(PrintWriter printWriter, String comment) {
        printWriter.println(COMMENT_BLOCK_PREFIX);
        String lines[] = comment.split("\\r?\\n");
        for(String line : lines) {
            printWriter.println(COMMENT_BLOCK_PADDING + line);
        }
        printWriter.println(COMMENT_BLOCK_SUFFIX);
    }
}
