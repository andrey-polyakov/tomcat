package com.codeminders.homework.parser;

/**
 * To do: count lines in place
 */
public class JavaCommentsRemover {
    public static String removeComments(String code) {
        StringBuilder output = new StringBuilder();
        boolean lineComment = false;
        boolean blockComment = false;
        boolean stringSwitch = false;
        for (int i = 0; i < code.length(); i++) {
            if (lineComment && lineCommentEnds(code, i)) {
                lineComment = false;
            } else if (blockComment && blockCommentEnds(code, i)) {
                blockComment = false;
            } else if (stringSwitch && stringEnds(code, i)) {
                stringSwitch = false;
            } else if (!blockComment && !stringSwitch && lineCommentStarts(code, i)) {
                lineComment = true;
            } else if (!lineComment && !stringSwitch && blockCommentStarts(code, i)) {
                blockComment = true;
            } else if (!lineComment && !blockComment && stringStarts(code, i)) {
                stringSwitch = true;
            }

            if (!lineComment && !blockComment) {
                output.append(code.charAt(i));
            } else if (blockComment && (code.charAt(i) == '\n' || code.charAt(i) == '\r')) {
                output.append(code.charAt(i));
            }
        }

        return output.toString();
    }

    private static boolean stringEnds(String code, int i) {
        return code.charAt(i - 1) != '\\' && code.charAt(i) == '"';
    }

    private static boolean stringStarts(String code, int i) {
        return code.charAt(i) == '"';
    }

    private static boolean blockCommentEnds(String code, int i) {
        return i - 2 >= 0 && code.charAt(i - 2) == '*' && code.charAt(i - 1) == '/';
    }

    private static boolean lineCommentEnds(String code, int i) {
        return code.charAt(i) == '\n' || code.charAt(i) == '\r' && code.length() > i + 1 && code.charAt(i + 1) == '\n';
    }

    private static boolean blockCommentStarts(String code, int i) {
        return code.charAt(i) == '/' && code.length() > i + 1 && code.charAt(i + 1) == '*';
    }

    private static boolean lineCommentStarts(String code, int i) {
        return code.charAt(i) == '/' && code.length() > i + 1 && code.charAt(i + 1) == '/';
    }
}